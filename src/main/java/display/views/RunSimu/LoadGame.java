package display.views.RunSimu;

import display.Display;
import display.model.ModelMVC;
import display.model.RunSimuModel;
import display.views.View;
import engine.Files;
import engine.map.GameMap;
import ia.model.Model;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

            // debloquer le button
            Button nextBtn = (Button)this.pane.lookup("#nextBtn");
            nextBtn.setDisable(false);
        }

        super.update();
    }

    public void setUpGame(RunSimuModel model, File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        long seed = Long.parseLong(br.readLine());
        String mapName = br.readLine();
        String[] teamModels = br.readLine().split(";");
        int playerCount = Integer.parseInt(br.readLine());
        int moveSpeed = Integer.parseInt(br.readLine());
        int respawnTime = Integer.parseInt(br.readLine());

        model.setSeed(seed);
        model.setMap(GameMap.loadFile("ressources/maps/"+mapName+".txt"));
        model.setNbPlayers(playerCount);

        List<List<ModelEnum>> gameTeamsModels = new ArrayList<>();
        for (int i = 0; i < teamModels.length; i++) {
            ModelEnum modelEnum = ModelEnum.getEnum(Integer.parseInt(teamModels[i]));
            List<ModelEnum> teamModel = new ArrayList<>();
            gameTeamsModels.add(teamModel);
            for (int j = 0; j < playerCount; j++) {
                teamModel.add(modelEnum);
            }
        }
        model.setModelList(gameTeamsModels);

        model.setSpeedPlayers(moveSpeed);
        model.setRespawnTime(respawnTime);

    }
}
