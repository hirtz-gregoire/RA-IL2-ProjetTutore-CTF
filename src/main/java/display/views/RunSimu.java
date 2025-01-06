package display.views;

import display.controllers.Controller;
import display.model.ModelMVC;

import java.io.IOException;

public class RunSimu extends View {

    public RunSimu(ModelMVC modelMVC, Controller controller) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("RunSimu", this.model);
    }

    @Override
    protected void update() {
        super.update();
    }
}
