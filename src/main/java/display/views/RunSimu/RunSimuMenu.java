package display.views.RunSimu;

import display.model.ModelMVC;
import display.views.View;

import java.io.IOException;

public class RunSimuMenu extends View {

    private Thread gameThread;

    public RunSimuMenu(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("RunSimuMenu", this.modelMVC);

        this.update();
    }

    @Override
    public void update() {
        super.update();
    }
}
