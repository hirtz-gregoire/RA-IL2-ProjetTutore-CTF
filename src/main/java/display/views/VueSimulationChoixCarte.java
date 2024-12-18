package display.views;

import java.io.*;

import display.Display;
import display.controlers.ControlerVue;
import display.modele.Modele;
import engine.Files;
import engine.map.GameMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class VueSimulationChoixCarte extends BorderPane implements Observateur {

	public VueSimulationChoixCarte() {
		super();
	}

	@Override
	public void actualiser(Modele modele) {
		this.getChildren().clear();

		if (modele.getVue().equals(ViewsEnum.SimulationChoixCarte)) {
			ControlerVue control = new ControlerVue(modele);

			//VBox avec toutes les cartes enregistrées
			VBox cartesBox = new VBox();

			//ToggleGroup pour enregistrer les radios buttons
			ToggleGroup toggleGroup = new ToggleGroup();

			//Boucle avec toutes les cartes enregistrées
			for (File fichierCarte : Files.getListeFichiersCartes()) {
				//La carte est une HBox
				HBox carteBox = new HBox();

				//Petite image de la carte
				//ESSAYER D'ENLEVER LE TRY CATCH
				try {
					GameMap gameMap = GameMap.loadFile(fichierCarte.getAbsolutePath());
					//Label d'affichage des TPS actuels de l'engine
					Display carteImage = new Display(new HBox(), gameMap, "petit", null, null, null);
					carteBox.getChildren().add(carteImage.getGridPaneCarte());
					//RadioButton pour choisir cette carte
					RadioButton radioButton = new RadioButton(fichierCarte.getName().replace(".txt", ""));
					radioButton.setToggleGroup(toggleGroup);
					radioButton.setUserData(gameMap.getNbEquipes());
					carteBox.getChildren().add(radioButton);
					cartesBox.getChildren().add(carteBox);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			//Affichage du choix de la carte
			Label choixCarteLabel = new Label("Choix cartes : " + modele.getCarte());

			//Boutton pour choisir la partie
			Button buttonLancerPartie = new Button("Choisir paramètres");
			//Ajout des controles sur les boutons
			buttonLancerPartie.setOnMouseClicked(control);

			//Le menu est une vbox contenu dans une scrollPane
			VBox vBox = new VBox(cartesBox, choixCarteLabel, buttonLancerPartie);
			ScrollPane scrollPane = new ScrollPane();
			scrollPane.setContent(vBox);
			scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
			scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
			scrollPane.setFitToWidth(true); // Adapte la largeur au parent

			this.setCenter(scrollPane);

			//Listener pour détécter le choix d'une carte
			toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
				public void changed(ObservableValue<? extends Toggle> ob, Toggle o, Toggle n) {
					RadioButton rb = (RadioButton)toggleGroup.getSelectedToggle();
					if (rb != null) {
						String s = rb.getText();
						//Changer le label du choix
						choixCarteLabel.setText("Choix carte : " + s);
						//Enregistrer la carte et le nombre d'équipe choisie dans le modèle pour la génération de la partie
						modele.setCarte(s);
						int nbEquipes = (int) rb.getUserData();
						modele.setNbEquipes(nbEquipes);
						modele.setModelsEquipes(new String[nbEquipes]);
					}
				}
			});
		}
	}
}
