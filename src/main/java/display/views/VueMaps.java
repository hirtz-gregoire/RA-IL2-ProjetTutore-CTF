package display.views;

import javafx.scene.layout.*;
import display.modele.Modele;

public class VueMaps extends Pane implements Observateur {

	public VueMaps() {
		super();
	}

	@Override
	public void actualiser(Modele modele) {
		this.getChildren().clear();  // efface

		//on n'utilise la vue que si la vue est gantt
		if (modele.getVue().equals(ViewsEnum.Maps)) {

		}
	}
}