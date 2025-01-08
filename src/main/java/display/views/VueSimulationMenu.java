package display.views;

import display.controlers.ControlerVue;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import display.modele.ModeleMVC;

public class VueSimulationMenu extends Pane implements Observateur {
	public VueSimulationMenu() {
		super();
	}

	@Override
	public void actualiser(ModeleMVC modeleMVC) {
		this.getChildren().clear();

		if (modeleMVC.getVue().equals(ViewsEnum.SimulationMenu)) {
			ControlerVue control = new ControlerVue(modeleMVC);

			Button buttonNouvellePartie = new Button("Nouvelle Partie");
			Button buttonChargerPartie = new Button("Charger Partie");
			buttonNouvellePartie.setOnMouseClicked(control);
			buttonChargerPartie.setOnMouseClicked(control);

			VBox vBox = new VBox(buttonNouvellePartie, buttonChargerPartie);

			this.getChildren().add(vBox);
		}
	}
}
