package display.views.MapEditor;

import display.model.ModelMVC;
import display.views.View;

import java.io.IOException;

public class MapParameters extends View {

    public MapParameters(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("MapEditor/MapParameters", this.modelMVC);

        this.update();
    }

    @Override
    public void update() {
        super.update();
    }
}
