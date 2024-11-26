package views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;

/**
 * Une Vue du MVC - correspond au JLabel contenant la valeur du compteur
 */
public class VueCartes extends VBox implements Observateur {

	/**
	 * Constructeur - positionne la valeur 0 au centre du JLabel
	 */
	public VueCartes(double d) {
		super(d);
	}
	/**
	 * Actualisation du Label avec la nouvelle valeur du compteur obtenue grace au modele mod
	 * Methode lancee a chaque modification du modele
	 */
	@Override
	public void actualiser(Modele modele) {
		this.getChildren().clear();  // efface

		ControlerGantt control = new ControlerGantt(modele);

		//on n'utilise la vue que si la vue est gantt
		if (modele.getVue().equals("cartes")) {
			this.setPadding(new Insets(10));
			this.setAlignment(Pos.CENTER);

			//la duree totale du projet
			Text dureeTotale = new Text("Durée totale du projet : "+String.valueOf(modele.getDureeTotale())+" jours");
			dureeTotale.setFont(Font.font("verdana", FontWeight.MEDIUM, FontPosture.REGULAR, 20));

			//la duree totale du projet
			Text dureeRestante = new Text("Durée restante du projet : "+String.valueOf(modele.getDureeRestante())+" jours");
			dureeRestante.setFont(Font.font("verdana", FontWeight.MEDIUM, FontPosture.REGULAR, 20));

			//Ajout d'une HBox qui contiendra toutes les CheckBox alignées en haut
			HBox allCheckBox = new HBox(10);
			allCheckBox.setAlignment(Pos.TOP_CENTER);
			this.getChildren().addAll(dureeTotale, dureeRestante, allCheckBox);

			//on parcourt les listes
			for (Tache liste : modele.getListes()) {
				CheckBox uneListe = new CheckBox(liste.getTitre());
				uneListe.setOnAction(control);
				uneListe.setUserData(liste);
				uneListe.setFont(Font.font("verdana", FontWeight.MEDIUM, FontPosture.REGULAR, 20));
				uneListe.setBackground(new Background(new BackgroundFill(liste.getColor(), CornerRadii.EMPTY, Insets.EMPTY)));

				allCheckBox.getChildren().add(uneListe);

				if (modele.isSelected(liste)) {
					uneListe.setSelected(true);
					//on récupère la liste des tache
					//on ajoute chaque tâche
					for (Tache enfant : modele.getAllEnfants(liste)) {
						HBox uneTache = new HBox(20);
						uneTache.setBackground(new Background(new BackgroundFill(enfant.getColor(), new CornerRadii(10), Insets.EMPTY)));
						uneTache.setMinHeight(20);

						//le titre de la tâche
						Text titreTache = new Text(enfant.getTitre());
						titreTache.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15 - enfant.getProfondeur()*2));
						titreTache.setWrappingWidth(100);
						titreTache.setTextAlignment(TextAlignment.CENTER);

						//la representation de la tache
						StackPane reprTache = new StackPane();

						Text duree = new Text(String.valueOf(enfant.getDuree()));
						duree.setFont(Font.font("verdana", FontWeight.MEDIUM, FontPosture.REGULAR, 10));
						duree.setStroke(Color.WHITE);

						//la durée de la tâche
						// le rectangle de la tâche
						Rectangle rect = new Rectangle(enfant.getDuree(), 15);
						rect.setFill(Color.BLACK);
						rect.setAccessibleText(String.valueOf(enfant.getDuree()));
						rect.setArcWidth(10);
						rect.setArcHeight(10);

						reprTache.setTranslateX(enfant.getDateDebut());
						reprTache.getChildren().addAll(rect, duree);

						uneTache.getChildren().addAll(titreTache, reprTache);

						//on ajoute la tâche dans la liste
						this.getChildren().addAll(uneTache);
					}
				}
			}
		}
		else{
			this.setBackground(null);
		}
	}
}