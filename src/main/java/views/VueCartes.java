package views;

import javafx.scene.layout.*;
import modele.Modele;

public class VueCartes extends VBox implements Observateur {

	public VueCartes(double d) {
		super(d);
	}

	@Override
	public void actualiser(Modele modele) {
		this.getChildren().clear();  // efface

		//on n'utilise la vue que si la vue est gantt
		if (modele.getVue().equals("cartes")) {

		}
	}
}