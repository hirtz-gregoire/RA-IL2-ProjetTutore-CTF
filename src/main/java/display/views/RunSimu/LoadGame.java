package display.views.RunSimu;

import com.sun.scenario.effect.Blend;
import display.Display;
import display.model.ModelMVC;
import display.model.RunSimuModel;
import display.views.View;
import engine.Files;
import engine.map.GameMap;
import ia.model.ModelEnum;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class LoadGame extends View {
    public LoadGame(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("RunSimu/LoadGame", this.modelMVC);

        this.update();
    }

    @Override
    public void update() {
        RunSimuModel model = (RunSimuModel) this.modelMVC;

        // Afficher liste des maps dans la scrollPane
        VBox vbox = (VBox)((ScrollPane)this.pane.lookup("#gameList")).getContent();
        Label exempleLabel = (Label)vbox.getChildren().getFirst();
        vbox.getChildren().clear();

        File[] files = Files.getListFilesParties();
        model.setFiles(files);
        for (File file : files) {
            Label label = new Label(file.getName());
            label.setOnMouseClicked(exempleLabel.getOnMouseClicked());
            vbox.getChildren().add(label);
        }

        if (model.getIndiceMapSelected().isPresent()){

            // afficher la préview
            int indice = model.getIndiceMapSelected().get();
            File file = files[indice];
            try {
                setUpGame(model,file);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            HBox hboxCenter = (HBox)this.pane.lookup("#previewMap");
            hboxCenter.getChildren().clear();

            GameMap gameMap = model.getMap();
            Display carteImage = new Display(new HBox(), gameMap, (int)Math.min(hboxCenter.getHeight() * 2, hboxCenter.getWidth()), new HashMap<>());
            hboxCenter.getChildren().add(carteImage.getGrid());

            // débloquer le button
            Button nextBtn = (Button)this.pane.lookup("#nextBtn");
            nextBtn.setDisable(false);
        }

        super.update();
    }

    public void setUpGame(RunSimuModel model, File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        long seed = Long.parseLong(br.readLine());
        model.setSeed(seed);

        String mapName = br.readLine();
        model.setMap(GameMap.loadFile("ressources/maps/"+mapName));

        String[] teamsModels = br.readLine().split(";");
        String[] teamsNNModels = br.readLine().split(";");
        //Chargment des modèles de chaque équipe
        List<ModelEnum> modelsTeams = new ArrayList<>();
        List<String> modelsNNTeams = new ArrayList<>();
        int numNNModel = 0;
        for (int i = 0; i < teamsModels.length; i++) {
            ModelEnum modelEnum = ModelEnum.getEnum(Integer.parseInt(teamsModels[i]));
            modelsTeams.add(modelEnum);
            if (modelEnum.equals(ModelEnum.NeuralNetwork)) {
                modelsNNTeams.add(teamsNNModels[numNNModel]);
                numNNModel++;
            }
            else
                modelsNNTeams.add("");
        }
        model.setModelList(modelsTeams);
        model.setNeuralNetworkTeam(modelsNNTeams);

        int playerCount = Integer.parseInt(br.readLine());
        model.setNbPlayers(playerCount);

        double moveSpeed = Double.parseDouble(br.readLine());
        model.setSpeedPlayers(moveSpeed);

        int respawnTime = Integer.parseInt(br.readLine());
        model.setRespawnTime(respawnTime);

        int maxTurns = Integer.parseInt(br.readLine());
        model.setMaxTurns(maxTurns);
    }
}
