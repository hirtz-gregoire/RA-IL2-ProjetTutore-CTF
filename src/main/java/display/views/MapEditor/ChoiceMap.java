package display.views.MapEditor;

import display.model.ModelMVC;
import display.views.View;

import java.io.IOException;

public class ChoiceMap extends View {

    public ChoiceMap(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("MapEditor/ChoiceMap", this.modelMVC);

        this.update();
    }

    @Override
    public void update() {
        super.update();
    }
}
