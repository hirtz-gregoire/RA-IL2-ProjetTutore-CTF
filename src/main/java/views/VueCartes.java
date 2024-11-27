package views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
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