package views;

import controlers.ControlerVue;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import modele.Modele;

public class VueSimulationChoixPartie extends Pane implements Observateur {
	public VueSimulationChoixPartie() {
		super();
	}

	@Override
	public void actualiser(Modele modele) {
		this.getChildren().clear();  // efface toute la vue

		if (modele.getVue().equals(ViewsEnum.VueSimulationChoixPartie)) {
			//controleur pour changer la vue
			ControlerVue control = new ControlerVue(modele);

			//Boucle avec toutes les parties enregistrées

			//Boutton pour choisir la partie
			Button buttonLancerPartie = new Button("Lancer Simulation");
			//Ajout des controles sur les boutons
			buttonLancerPartie.setOnMouseClicked(control);

			//Menu est une VBox
			VBox vBox = new VBox(buttonLancerPartie);

			this.getChildren().add(vBox);
		}
	}
}
