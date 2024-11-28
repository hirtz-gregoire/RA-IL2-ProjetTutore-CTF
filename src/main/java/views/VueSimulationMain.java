package views;

import display.*;
import engine.Coordinate;
import engine.Engine;
import engine.Team;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.Flag;
import engine.object.GameObject;
import ia.model.Random;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
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


		if (modele.getVue().equals(ViewsEnum.VueSimulationMain)) {
			//Création des objets
			VBox simulationBox = new VBox();
			display = new DisplaySimulation(simulationBox);
			agents = new ArrayList<>();
//			for (int i = 2; i < 7; i++) {
//				for (int j = 2; j < 7; j++) {
//					if (i%2 == 0) {
//						agents.add(new Agent(
//								new Coordinate(i, j),
//								0.25,
//								0.1,
//								0.3,
//								40,
//								Team.RED,
//								Optional.empty(),
//								new Random()
//						));
//						agents.getFirst().setInGame(true);
//					}
//					else {
//						agents.add(new Agent(
//								new Coordinate(i, j),
//								0.25,
//								0.1,
//								0.3,
//								40,
//								Team.BLUE,
//								Optional.empty(),
//								new Random()
//						));
//						agents.getFirst().setInGame(true);
//					}
//
//				}
//			}

			agents.add(new Agent(
					new Coordinate(4, 3),
					0.25,
					0.1,
					0.3,
					40,
					Team.BLUE,
					Optional.empty(),
					new Random()
			));
			agents.getFirst().setInGame(true);
			map = GameMap.loadFile("ressources/maps/open_space.txt");
			objects = new ArrayList<>();
			//Ajout de deux drapeaux
			objects.add(
					new Flag(
						new Coordinate(14, 2),
						Team.RED
					)
			);
			objects.add(
					new Flag(
						new Coordinate(2, 6),
						Team.BLUE
					)
			);
			engine = new Engine(agents, map, objects, display, 10);

			//Label d'affichage des TPS de l'engine
			Label labelTpsEngine = new Label("TPS : "+ engine.getTps());

			//Bouton pour changer les TPS
			Button boutonDeceleration = new javafx.scene.control.Button("Décélerer");
			Button boutonPause = new javafx.scene.control.Button("Pause");
			Button boutonAcceleration = new Button("Accélérer");
			HBox boutons = new HBox(boutonDeceleration, boutonPause, boutonAcceleration);

			//Choix du Tps
			Slider choixTpsSlider = new Slider(1, 64, engine.getTps());
			choixTpsSlider.setMajorTickUnit(1);         // Espacement entre les ticks principaux
			choixTpsSlider.setMinorTickCount(0);        // Pas de ticks intermédiaires
			choixTpsSlider.setSnapToTicks(true);        // Alignement sur les ticks
			choixTpsSlider.setShowTickMarks(true);      // Afficher les ticks
			choixTpsSlider.setShowTickLabels(true);     // Afficher les labels
			Label choixTpsLabel = new Label("TPS");
			VBox choixTps = new VBox(choixTpsLabel, choixTpsSlider);

			VBox vboxControleurs = new VBox(labelTpsEngine, boutons, choixTps);

			//box principale
			VBox vbox = new VBox(simulationBox, vboxControleurs);
			this.getChildren().add(vbox);

			//Controlers des boutons et slider
			boutonDeceleration.setOnMouseClicked((EventHandler<MouseEvent>) e -> {
				int newTps = (int)engine.getTps()/2;
				engine.setTps(newTps);
				labelTpsEngine.setText("TPS : " + String.valueOf(newTps));
				choixTpsSlider.setValue(newTps);
			});
			boutonPause.setOnMouseClicked((EventHandler<MouseEvent>) e -> {
				int newTps = 0;
				if (engine.getTps() == 0) {
					newTps = 1;
				}
				engine.setTps(newTps);
				labelTpsEngine.setText("TPS : " + String.valueOf(newTps));
				choixTpsSlider.setValue(newTps);
			});
			boutonAcceleration.setOnMouseClicked((EventHandler<MouseEvent>) e -> {
				int newTps = (int)engine.getTps()*2;
				engine.setTps(newTps);
				labelTpsEngine.setText("TPS : " + String.valueOf(newTps));
				choixTpsSlider.setValue(newTps);
			});
			choixTpsSlider.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
				int newTps = (int)engine.getTps()/2;
				engine.setTps(new_val.intValue());
				labelTpsEngine.setText("TPS : " + String.valueOf(new_val.intValue()));
			});

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
