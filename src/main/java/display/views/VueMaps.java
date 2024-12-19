package display.views;

import javafx.scene.layout.*;
import display.modele.ModeleMVC;

public class VueMaps extends Pane implements Observateur {

	public VueMaps() {
		super();
	}

	@Override
	public void actualiser(ModeleMVC modeleMVC) {
		this.getChildren().clear();  // efface

		//on n'utilise la vue que si la vue est gantt
		if (modeleMVC.getVue().equals(ViewsEnum.Maps)) {

		}
	}
}