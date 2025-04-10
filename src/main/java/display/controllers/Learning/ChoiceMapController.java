package display.controllers.Learning;

import display.controllers.Controller;
import display.model.LearningModel;
import display.views.Learning.EnumLearning;
import engine.map.GameMap;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ChoiceMapController extends Controller {

    @FXML
    private VBox vbox;

    @FXML
    public void choiceMap(MouseEvent event) throws IOException {
        LearningModel model = (LearningModel) this.model;
        int indice = vbox.getChildren().indexOf((Label) event.getSource());
        model.setIndiceMapSelected(indice);

        if (model.getIndiceMapSelected().isPresent()){
           model.setPreviewGameMap(GameMap.loadFile(model.getFiles()[model.getIndiceMapSelected().get()]));
        }
        model.updateViews();
    }

    public void nextMenu(){
        LearningModel model = (LearningModel) this.model;
        model.setEnumLearning(EnumLearning.ChoiceParameters);
        model.update();
        model.getGlobalModel().updateRacine();
    }

    public void selecMap(){
        LearningModel model = (LearningModel) this.model;
        if (model.getIndiceMapSelected().isPresent()){
            if (model.getMap().stream().noneMatch(gameMap ->
                    gameMap.getName().equals(model.getPreviewGameMap().getName()))) {
                model.getMap().add(model.getPreviewGameMap());
            } else {
                model.getMap().removeIf(gameMap ->
                        gameMap.getName().equals(model.getPreviewGameMap().getName()));
            }
        }
        model.updateViews();

    }
}
