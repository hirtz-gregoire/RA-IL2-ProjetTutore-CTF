package display.views.RunSimu;

import display.model.ModelMVC;
import display.views.View;

import java.io.IOException;

public class ChoiceMap extends View {

    private Thread gameThread;

    public ChoiceMap(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("RunSimu/ChoiceMap", this.modelMVC);

        this.update();
    }

    @Override
    public void update() {
        super.update();
    }
}
