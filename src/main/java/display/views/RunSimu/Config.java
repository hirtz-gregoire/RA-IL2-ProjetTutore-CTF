package display.views.RunSimu;

import display.model.ModelMVC;
import display.views.View;

import java.io.IOException;

public class Config extends View {

    private Thread gameThread;

    public Config(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("RunSimu/Config", this.modelMVC);

        this.update();
    }

    @Override
    public void update() {
        super.update();
    }
}
