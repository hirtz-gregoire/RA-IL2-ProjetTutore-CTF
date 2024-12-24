package display.model;

import display.views.ViewMVC;
import display.views.ViewType;
import javafx.scene.layout.Pane;

public class ModelMVC {

    public ViewMVC currentView;

    public ModelMVC(ViewType view) {
        this.currentView = ViewType.createView(this, view);
    }

    public void updateCurrentView() {
        currentView.update();
    }

    public Pane getRootPane(){
        return currentView.rootPane;
    }

}
