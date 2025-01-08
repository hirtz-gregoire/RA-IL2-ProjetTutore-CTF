package display.views;

import display.controllers.Controller;
import display.model.ModelMVC;

import java.io.IOException;

public class MainMenu extends View {

    public MainMenu(ModelMVC modelMVC, Controller controller) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("MainMenu", this.model);

        //View v = new Test(modelMVC);
        //addChildrenView(v);
        //VBox vbox = (VBox)this.pane.lookup("#VBOX");
        //vbox.getChildren().setAll(v.pane.getChildren());
    }

    @Override
    protected void update() {
        super.update();
    }
}
