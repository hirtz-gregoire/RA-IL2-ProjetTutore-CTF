package views;

import controlers.ControlerSimulation;
import display.*;
import engine.Coordinate;
import engine.Engine;
import engine.Team;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.Flag;
import engine.object.GameObject;
import ia.model.Random;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import modele.Modele;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
	public void actualiser(Modele modele) throws Exception {
		this.getChildren().clear();  // efface toute la vue

		if (modele.getVue().equals("simulation_main")) {
			//Création des objets
			VBox simulationBox = new VBox();
			display = new DisplaySimulation(simulationBox);
			agents = new ArrayList<>();
			agents.add(new Agent(
					new Coordinate(5, 5),
					1.0,
					0.5,
					0.3,
					10,
					Team.BLUE,
					Optional.empty(),
					new Random()
			));
			agents.add(new Agent(
					new Coordinate(5, 5),
					1.0,
					0.5,
					0.3,
					10,
					Team.PINK,
					Optional.empty(),
					new Random()
			));
			map = GameMap.loadFile("ressources/maps/open_space.txt");
			objects = new ArrayList<>();
			//Ajout de deux drapeaux
			objects.add(
					new Flag(
						new Coordinate(400, -100),
						Team.PINK
					)
			);
			objects.add(
					new Flag(
						new Coordinate(-400, 100),
						Team.BLUE
					)
			);
			engine = new Engine(agents, map, objects, display, 10);

			//Le controleur de la simulation
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
			Task<Void> gameTask = new Task<>() {
				@Override
				protected Void call() {
					engine.run();
					return null;
				}
			};
			Thread gameThread = new Thread(gameTask);
			gameThread.setDaemon(true); // Stop thread when exiting
			gameThread.start();
		}
	}
}
