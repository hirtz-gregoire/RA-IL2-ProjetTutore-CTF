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

		//on n'utilise la vue que si la vue est en colonne
		if (modele.getVue().equals("simulation_menu")) {
			//controleur pour modifier, créer tache et liste
			ControlerVue control = new ControlerVue(modele);

			//Boutton Nouvelle partie
			Button buttonNouvellePartie = new Button("Nouvelle Partie");
			//on assigne le bouton à la liste correspondante
			buttonNouvellePartie.setMinWidth(50);
			buttonNouvellePartie.setMinHeight(15);

			//Boutton Charger Partie
			Button buttonChargerPartie = new Button("Charger Partie");
			//on assigne le bouton à la liste correspondante
			buttonChargerPartie.setMinWidth(50);
			buttonChargerPartie.setMinHeight(15);

			//ajout des controles sur les boutons
			buttonNouvellePartie.setOnMouseClicked(control);
			buttonChargerPartie.setOnMouseClicked(control);

			//Menu est une VBox
			VBox vBox = new VBox(buttonNouvellePartie, buttonChargerPartie);
			vBox.setAlignment(Pos.CENTER);
			vBox.setPadding(new Insets(10));
			vBox.setSpacing(10);
			vBox.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(10), Insets.EMPTY)));
			//Bordure
			vBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(2))));
			//La vbox prend toute la place possible
			vBox.setMaxHeight(Double.MAX_VALUE);

			this.getChildren().add(vBox);
		}
	}
}
