package display.views;

import display.controlers.ControlerVue;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import display.modele.ModeleMVC;

public class VueMapMenu extends Pane implements Observateur {

    public VueMapMenu() {
        super();
    }

    @Override
    public void actualiser(ModeleMVC modeleMVC) {
        this.getChildren().clear();

        if (modeleMVC.getVue().equals(ViewsEnum.MapMenu)) {
            ControlerVue control = new ControlerVue(modeleMVC);

            Button buttonNouvelleCarte = new Button("Nouvelle Carte");
            Button buttonModifierCarte = new Button("Modifier Carte");
            buttonNouvelleCarte.setOnMouseClicked(control);
            buttonModifierCarte.setOnMouseClicked(control);

            VBox vBox = new VBox(buttonNouvelleCarte, buttonModifierCarte);

            this.getChildren().add(vBox);
        }
    }
}