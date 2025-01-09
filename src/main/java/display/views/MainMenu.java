package display.views;

import display.model.ModelMVC;

import java.io.IOException;

public class MainMenu extends View {

    public MainMenu(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("MainMenu", this.modelMVC);
        //System.out.println(this.pane);

        //View v = new Test(modelMVC);
        //addChildrenView(v);
        //VBox vbox = (VBox)this.pane.lookup("#VBOX");
        //vbox.getChildren().setAll(v.pane.getChildren());
    }

    @Override
    public void update() {
        super.update();
    }
}
