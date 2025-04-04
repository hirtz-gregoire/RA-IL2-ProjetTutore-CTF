package display.views.Learning;

import display.Display;
import display.controllers.Learning.ChoiceMapController;
import display.model.LearningModel;
import display.model.ModelMVC;
import display.views.View;
import engine.Files;
import engine.map.GameMap;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ChoiceMap extends View {

    public ChoiceMap(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("Learning/ChoiceMap", this.modelMVC);
        this.update();
    }

    @Override
    public void update() {
        LearningModel model = (LearningModel) this.modelMVC;
        // Afficher liste des maps dans la scrollPane
        VBox vbox = (VBox)((ScrollPane)this.pane.lookup("#mapList")).getContent();
        Label exempleLabel1 = (Label)vbox.getChildren().getFirst();
        vbox.getChildren().clear();

        File[] files = Files.getListFilesMaps();
        model.setFiles(files);
        for (File file : files) {
            String fileName = file.getName();

            // Vérifie si le fichier correspond à un des noms dans la liste des GameMap
            boolean existsInList = model.getMap().stream()
                    .anyMatch(gameMap -> gameMap.getName().equals(fileName));

            Label label = new Label(fileName);
            label.setOnMouseClicked(exempleLabel1.getOnMouseClicked());
            if (existsInList) {
                label.setStyle("-fx-text-fill: green;");
            }
            vbox.getChildren().add(label);
        }


        if (model.getIndiceMapSelected().isPresent()){
            // afficher la préview
            int indice = model.getIndiceMapSelected().get();
            File file = files[indice];

            HBox hboxCenter = (HBox)this.pane.lookup("#previewMap");
            hboxCenter.getChildren().clear();

            GameMap gameMap = model.getPreviewGameMap();
            Display carteImage = new Display(new HBox(), gameMap, (int)Math.min(hboxCenter.getHeight() * 2, hboxCenter.getWidth()), new HashMap<>());
            hboxCenter.getChildren().add(carteImage.getGrid());

            // debloquer les buttons
            Button nextBtn = (Button)this.pane.lookup("#nextBtn");
            if (!model.getMap().isEmpty()) {
                nextBtn.setDisable(false);
            }else{
                nextBtn.setDisable(true);
            }


            Button selecBtn = (Button)this.pane.lookup("#selecBtn");

            boolean existsInList = model.getMap().stream()
                    .anyMatch(gameMap2 -> gameMap2.getName().equals(model.getPreviewGameMap().getName()));
            if (existsInList){
                selecBtn.setText("Desélectionner");
                selecBtn.setStyle("-fx-background-color: orange;");
            }else{
                selecBtn.setText("Sélectionner");
                selecBtn.setStyle("-fx-background-color: green;");
            }
            selecBtn.setDisable(false);
        }

        super.update();
    }
}
