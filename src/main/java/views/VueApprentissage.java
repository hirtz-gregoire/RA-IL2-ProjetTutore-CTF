package views;

import javafx.scene.layout.StackPane;
import modele.Modele;

public class VueApprentissage extends StackPane implements Observateur {

	public VueApprentissage() {
		super();
	}

	/**
	 * Actualisation du Label avec la nouvelle valeur du compteur obtenue grace au modele mod
	 * Methode lancee a chaque modification du modele
	 */
	@Override
	public void actualiser(Modele modele) {
		this.getChildren().clear();
		//on n'utilise la vue que si la vue est en liste
		if (modele.getVue().equals("apprentissage")) {

		}
	}
}