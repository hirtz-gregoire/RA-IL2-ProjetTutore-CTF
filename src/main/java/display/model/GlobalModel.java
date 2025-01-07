package display.model;

import display.App;
import display.views.View;
import display.views.ViewType;
import javafx.scene.layout.Pane;

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

    public GlobalModel(ViewType viewType) {
        this.currentViewType = viewType;
    }

    public Pane getPane() throws IOException {
        return ViewType.getViewInstance(this.currentViewType).getPane();
    }
}
