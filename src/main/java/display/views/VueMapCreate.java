package display.views;

import display.modele.ModeleMVC;
import javafx.scene.layout.Pane;

public class VueMapCreate extends Pane implements Observateur {

    public VueMapCreate() {
        super();
    }

    @Override
    public void actualiser(ModeleMVC modeleMVC) throws Exception {
        this.getChildren().clear();

        if (modeleMVC.getVue().equals(ViewsEnum.MapCreate)) {

        }
    }
}
