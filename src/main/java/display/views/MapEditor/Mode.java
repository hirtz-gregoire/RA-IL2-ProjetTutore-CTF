package display.views.MapEditor;

import display.model.ModelMVC;
import display.views.View;

import java.io.IOException;

public class Mode extends View {

    public Mode(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("MapEditor/Mode", this.modelMVC);

        this.update();
    }

    @Override
    public void update() {
        super.update();
    }
}
