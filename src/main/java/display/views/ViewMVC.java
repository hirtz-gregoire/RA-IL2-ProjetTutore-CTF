package display.views;

import display.controllers.ControllerMVC;
import display.model.ModelMVC;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;

public abstract class ViewMVC {

    public Pane rootPane;

    public ViewMVC(ViewType viewType, ModelMVC modelMVC) {
        try{
            FXMLLoader loader = ViewType.loadFxml(viewType);
            rootPane = loader.load();
            ControllerMVC ctrl = loader.getController();
            ctrl.setModel(modelMVC);
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public abstract void update();
}
