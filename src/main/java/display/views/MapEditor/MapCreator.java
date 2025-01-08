package display.views.MapEditor;

import display.model.ModelMVC;
import display.views.View;

import java.io.IOException;

public class MapCreator extends View {

    public MapCreator(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("MapEditor/MapCreator", this.modelMVC);

        this.update();
    }

    @Override
    public void update() {
        super.update();
    }
}
