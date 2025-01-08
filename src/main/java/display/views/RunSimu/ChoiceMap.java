package display.views.RunSimu;

import display.Display;
import display.model.ModelMVC;
import display.model.RunSimuModel;
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

public class ChoiceMap extends View {

    public ChoiceMap(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("RunSimu/ChoiceMap", this.modelMVC);

        this.update();
    }

    @Override
    public void update() {

        RunSimuModel model = (RunSimuModel) this.modelMVC;

        // Afficher liste des maps dans la scrollPane
        VBox vbox = (VBox)((ScrollPane)this.pane.lookup("#mapList")).getContent();
        Label exempleLabel = (Label)vbox.getChildren().getFirst();
        vbox.getChildren().clear();

        File[] files = Files.getListFilesMaps();
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

            HBox hboxCenter = (HBox)this.pane.lookup("#previewMap");
            hboxCenter.getChildren().clear();

            GameMap gameMap = model.getMap();
            Display carteImage = new Display(new HBox(), gameMap, 256);
            hboxCenter.getChildren().add(carteImage.getGrid());

            // debloquer le button
            Button nextBtn = (Button)this.pane.lookup("#nextBtn");
            nextBtn.setDisable(false);
        }

        super.update();
    }
}
