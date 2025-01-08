package display.views.RunSimu;

import display.model.ModelMVC;
import display.views.View;

import java.io.IOException;

public class Mode extends View {

    private Thread gameThread;

    public Mode(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("RunSimu/Mode", this.modelMVC);

        this.update();
    }

    @Override
    public void update() {
        super.update();
    }
}
