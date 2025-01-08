package display.model;

import display.views.ViewType;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class ModelMVC {

    private ViewType currentViewType;

    public ModelMVC(ViewType viewType) {
        this.currentViewType = viewType;
    }

    public Pane getPane() throws IOException {
        return ViewType.getViewInstance(this.currentViewType, this).getPane();
    }
}
