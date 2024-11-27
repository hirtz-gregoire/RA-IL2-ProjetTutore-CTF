package views;

import controlers.ControlerVue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import modele.Modele;

public class VueSimulationMenu extends Pane implements Observateur {
	public VueSimulationMenu() {
		super();
	}

	@Override
	public void actualiser(Modele modele) {
		this.getChildren().clear();  // efface toute la vue

		if (modele.getVue().equals("simulation_menu")) {
			//controleur pour changer la vue
			ControlerVue control = new ControlerVue(modele);

			//Boutton Nouvelle partie
			Button buttonNouvellePartie = new Button("Nouvelle Partie");
			//Boutton Charger Partie
			Button buttonChargerPartie = new Button("Charger Partie");
			//Ajout des controles sur les boutons
			buttonNouvellePartie.setOnMouseClicked(control);
			buttonChargerPartie.setOnMouseClicked(control);

			//Menu est une VBox
			VBox vBox = new VBox(buttonNouvellePartie, buttonChargerPartie);

			this.getChildren().add(vBox);
		}
	}
}
