package display.controllers.MapEditor;

import display.controllers.Controller;
import display.model.MapEditorModel;
import display.model.RunSimuModel;
import display.views.MapEditor.EnumMapEditor;
import display.views.RunSimu.EnumRunSimu;
import engine.map.EditorMap;
import engine.map.GameMap;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class ChoiceMapController extends Controller {

    @FXML
    private VBox vboxMapNames;

    @FXML
    public void choiceMap(MouseEvent event) throws IOException {
        MapEditorModel model = (MapEditorModel) this.model;
        int indice = vboxMapNames.getChildren().indexOf((Label) event.getSource());
        model.setIndiceMapSelected(indice);

        if (model.getIndiceMapSelected().isPresent()){
            model.setMapChoice(GameMap.loadFile(model.getFiles()[model.getIndiceMapSelected().get()]));
        }

        model.updateViews();
    }

    public void nextMenu(){
        MapEditorModel model = (MapEditorModel) this.model;
        model.setActualMapEditorView(EnumMapEditor.MapModify);
        model.update();
        model.getGlobalModel().updateRacine();
    }
}
