package display.controllers.RunSimu;

import display.controllers.Controller;
import display.model.RunSimuModel;
import display.views.RunSimu.EnumRunSimu;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class LoadGameController extends Controller {
    @FXML
    private VBox vbox;

    @FXML
    public void choiceGame(MouseEvent event) throws IOException {
        RunSimuModel model = (RunSimuModel) this.model;
        int indice = vbox.getChildren().indexOf((Label) event.getSource());
        model.setIndiceMapSelected(indice);
        model.updateViews();
    }

    public void nextMenu() {
        RunSimuModel model = (RunSimuModel) this.model;
        model.setEnumRunSimu(EnumRunSimu.Main);
        model.update();
        model.getGlobalModel().updateRacine();
    }
}
