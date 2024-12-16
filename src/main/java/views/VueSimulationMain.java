package views;

import display.*;
import engine.Coordinate;
import engine.Engine;
import engine.Team;
import engine.agent.Agent;
import engine.map.Cell;
import engine.map.GameMap;
import engine.object.Flag;
import engine.object.GameObject;
import ia.model.Random;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
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
			map = GameMap.loadFile("ressources/maps/dust.txt");
			display = new Display(simulationBox, map);
			agents = new ArrayList<>();
			for(int i = 0; i < 10; i++) {
				agents.add(new Agent(
						new Coordinate(0, 0),
						0.35,
						1,
						0.5,
						180,
						Team.RED,
						Optional.empty(),
						new Random()
				));
				agents.add(new Agent(
						new Coordinate(0, 0),
						0.35,
						1,
						0.5,
						180,
						Team.BLUE,
						Optional.empty(),
						new Random()
				));
			}
			objects = map.getGameObjects();
			engine = new Engine(agents, map, objects, display, 10);

			//Label d'affichage des TPS de l'engine
			Label labelTpsEngine = new Label("TPS : "+ engine.getTps());
			//Label d'affichage des TPS actuels de l'engine
			Label labelTpsActualEngine = new Label("TPS actuels : "+ engine.getActualTps());

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

			VBox vboxControleurs = new VBox(labelTpsEngine, labelTpsActualEngine, boutons, choixTps);

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
				boutonPause.setText("Play");
				if (engine.getTps() == 0) {
					newTps = 1;
					boutonPause.setText("Pause");
				}
				System.out.println(engine.getTps());
				engine.setTps(newTps);
				System.out.println(engine.getTps());
				labelTpsEngine.setText("TPS : " + String.valueOf(newTps));
				choixTpsSlider.setValue(newTps);
				System.out.println(engine.getTps());
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
