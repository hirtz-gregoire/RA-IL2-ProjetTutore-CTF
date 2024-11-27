package views;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;
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