package display.controllers.RunSimu;

import display.controllers.Controller;
import display.model.RunSimuModel;
import display.views.RunSimu.EnumRunSimu;
import engine.map.GameMap;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class ChoiceMapController extends Controller {

    @FXML
    private VBox vbox;

    @FXML
    public void choiceMap(MouseEvent event) throws IOException {
        RunSimuModel model = (RunSimuModel) this.model;
        int indice = vbox.getChildren().indexOf((Label) event.getSource());
        model.setIndiceMapSelected(indice);
        if (model.getIndiceMapSelected().isPresent()){
            model.setMap(GameMap.loadFile(model.getFiles()[model.getIndiceMapSelected().get()]));
        }
        model.updateViews();
    }

    public void nextMenu(){
        RunSimuModel model = (RunSimuModel) this.model;
        model.setEnumRunSimu(EnumRunSimu.ChoiceParameters);
        model.update();
        model.getGlobalModel().updateRacine();
    }
}
