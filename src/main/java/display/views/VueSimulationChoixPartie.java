package display.views;

import com.sun.javafx.scene.SceneHelper;
import display.Display;
import display.controlers.ControlerVue;
import engine.map.GameMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import display.modele.Modele;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class VueSimulationChoixPartie extends BorderPane implements Observateur {
	String cheminParties = "ressources/parties";
	String cheminCartes = "ressources/maps";

	public VueSimulationChoixPartie() {
		super();
	}

	@Override
	public void actualiser(Modele modele) throws Exception {
		this.getChildren().clear();  // efface toute la vue

		if (modele.getVue().equals(ViewsEnum.SimulationChoixPartie)) {
			//controleur pour changer la vue
			ControlerVue control = new ControlerVue(modele);

			//VBox avec toutes les cartes enregistrées
			VBox partiesBox = new VBox();

			//ToggleGroup pour enregistrer les radios buttons
			ToggleGroup toggleGroup = new ToggleGroup();

			//Boucle avec toutes les parties enregistrées
			File repertoireParties  = new File(cheminParties);
			File[] listeParties = repertoireParties.listFiles();
			for (File fichierPartie : listeParties) {
				//Lecture du fichier
				BufferedReader reader = new BufferedReader(new FileReader(cheminParties+"/"+fichierPartie.getName()));
				String header = reader.readLine();
				String fichierCarte = reader.readLine();
				//La carte est une HBox
				HBox partieBox = new HBox();
				//Petite image de la carte
				try {
					GameMap gameMap = GameMap.loadFile(cheminCartes + "/" + fichierCarte+".txt");
					//Label d'affichage des TPS actuels de l'engine
					Display carteImage = new Display(new HBox(), gameMap, "petit", null, null, null);
					partieBox.getChildren().add(carteImage.getGridPaneCarte());
				} catch (Exception e) {
					e.printStackTrace();
				}
				//RadioButton pour choisir cette carte
				RadioButton radioButton = new RadioButton(fichierPartie.getName().replace(".txt", ""));
				radioButton.setToggleGroup(toggleGroup);
				//informations
				Label label = new Label(header);
				partieBox.getChildren().addAll(radioButton, label);

				partiesBox.getChildren().add(partieBox);
			}
			//Affichage du choix de la partie
			Label choixPartieLabel = new Label("Choix cartes : " + modele.getCarte());

			//Boutton pour choisir la partie
			Button buttonLancerPartie = new Button("Lancer Simulation");
			//Ajout des controles sur les boutons
			buttonLancerPartie.setOnMouseClicked(control);

			//Le menu est une vbox contenu dans une scrollPane
			VBox vBox = new VBox(partiesBox, choixPartieLabel, buttonLancerPartie);
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
						choixPartieLabel.setText("Choix partie : " + s);
						//Enregistrer la carte choisie dans le modèle pour la génération de la partie
						modele.setPartie(s);
					}
				}
			});
		}
	}
}
