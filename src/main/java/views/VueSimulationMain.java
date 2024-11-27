package views;

import controlers.ControlerSimulation;
import display.*;
import engine.Engine;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import modele.Modele;

import java.util.List;

public class VueSimulationMain extends Pane implements Observateur {
	List<Agent> agents = null;
	GameMap map = null;
	List<GameObject> objects = null;
	Engine engine = null;
	Display display = null;

	public VueSimulationMain() {
		super();
	}

	@Override
	public void actualiser(Modele modele) {
		this.getChildren().clear();  // efface toute la vue

		if (modele.getVue().equals("simulation_main")) {
			//Création des objets
			VBox simulationBox = new VBox();
			display = new DisplaySimulation(simulationBox);
			engine = new Engine(agents, map, objects, display);

			ControlerSimulation controlerSimulation = new ControlerSimulation(modele);

			//Bouton pour changer les FPS
			Button boutonDeceleration = new javafx.scene.control.Button("Décélerer");
			boutonDeceleration.setOnMouseClicked(controlerSimulation);
			Button boutonPasArriere = new javafx.scene.control.Button("Pas Arrière");
			boutonPasArriere.setOnMouseClicked(controlerSimulation);
			Button boutonPause = new javafx.scene.control.Button("Pause");
			boutonPause.setOnMouseClicked(controlerSimulation);
			Button boutonPasAvant = new javafx.scene.control.Button("Pas Avant");
			boutonPasAvant.setOnMouseClicked(controlerSimulation);
			Button boutonAcceleration = new Button("Accélérer");
			boutonAcceleration.setOnMouseClicked(controlerSimulation);
			HBox boutons = new HBox(boutonDeceleration, boutonPasArriere, boutonPause, boutonPasAvant, boutonAcceleration);

			CheckBox checkBoxCouperAffichage = new CheckBox("Couper l'affichage");

			//Choix du Tps
			Slider choixTpsSlider = new Slider(-100, 100, 20);
			choixTpsSlider.setMajorTickUnit(1);         // Espacement entre les ticks principaux
			choixTpsSlider.setMinorTickCount(0);        // Pas de ticks intermédiaires
			choixTpsSlider.setSnapToTicks(true);        // Alignement sur les ticks
			choixTpsSlider.setShowTickMarks(true);      // Afficher les ticks
			choixTpsSlider.setShowTickLabels(true);     // Afficher les labels
			Label choixTpsLabel = new Label("TPS");
			VBox choixTps = new VBox(choixTpsLabel, choixTpsSlider);

			VBox vboxControleurs = new VBox(boutons, checkBoxCouperAffichage, choixTps);

			//box principale
			VBox vbox = new VBox(simulationBox, vboxControleurs);

			this.getChildren().add(vbox);

			//Lancement de l'engine
			engine.run();
		}
	}
}
