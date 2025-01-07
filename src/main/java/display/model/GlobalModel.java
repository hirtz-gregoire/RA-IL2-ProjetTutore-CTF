package display.model;

import display.App;
import display.views.View;
import display.views.ViewType;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GlobalModel {

    // SINGLETON

    private static GlobalModel instance;
    public static GlobalModel getInstance() {
        if (instance == null) {
            instance = new GlobalModel(App.DEFAULT_VIEWTYPE);
        }
        return instance;
    }

    // CLASS

    private ViewType currentViewType;
    private Pane racine;

    public GlobalModel(ViewType viewType) {
        this.currentViewType = viewType;
    }

    public Pane getPane() throws IOException {
        racine = ViewType.getViewInstance(this.currentViewType, this).getPane();
        return racine;
    }

    public void updateRacine() throws IOException {
        Stage stage = (Stage) racine.getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(ViewType.getViewInstance(this.currentViewType, this).getPane());
    }

    public ViewType getCurrentViewType() {
        return this.currentViewType;
    }

    public void setCurrentViewType(ViewType viewType) {
        this.currentViewType = viewType;
    }
}
