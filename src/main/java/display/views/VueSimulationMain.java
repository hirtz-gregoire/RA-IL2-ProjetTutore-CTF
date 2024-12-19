package display.views;

import display.*;
import display.controlers.ControlerSave;
import display.controlers.ControlerVue;
import engine.Coordinate;
import engine.Engine;
import engine.Team;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;
import ia.model.DecisionTree;
import ia.model.Random;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import display.modele.ModeleMVC;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VueSimulationMain extends BorderPane implements Observateur {
	private List<Agent> agents = null;
	private GameMap map = null;
	private List<GameObject> objects = null;
	private Engine engine = null;
	private Display display = null;
	private Thread gameThread;

	public VueSimulationMain() {
		super();
	}

	@Override
	public void actualiser(ModeleMVC modeleMVC) throws Exception {
		//Arrêter la simulation si on est plus dans la vue
		if (!this.getChildren().isEmpty()) {
			stopSimulation();
		}
		this.getChildren().clear();  // efface toute la vue

		if (modeleMVC.getVue().equals(ViewsEnum.SimulationMain)) {
			ControlerVue controlerVue = new ControlerVue(modeleMVC);

			VBox simulationBox = new VBox();
			//Label d'affichage des TPS actuels de l'engine
			Label labelTpsActualEngine = new Label("TPS actuels : " + 0);
			//Nombre de joueurs morts par équipe
			Label[] labelsNbJoueursMorts = new Label[modeleMVC.getNbEquipes()];
			for (int numEquipe = 0; numEquipe < modeleMVC.getNbEquipes(); numEquipe++) {
				labelsNbJoueursMorts[numEquipe] = new Label("Nombre joueur mort équipe " + numEquipe + " : " + 0);
			}
			//Temps réstant avant la prochaine apparition
			Label[] labelsTempsProchaineReaparitionEquipes = new Label[modeleMVC.getNbEquipes()];
			for (int numEquipe = 0; numEquipe < modeleMVC.getNbEquipes(); numEquipe++) {
				labelsTempsProchaineReaparitionEquipes[numEquipe] = new Label("Temps prochaine réaparition équipe " + numEquipe + " : " + 0);
			}
			map = GameMap.loadFile("ressources/maps/"+ modeleMVC.getCarte() + ".txt");
			display = new Display(simulationBox, map, 1024, labelTpsActualEngine, labelsNbJoueursMorts, labelsTempsProchaineReaparitionEquipes);
			agents = new ArrayList<>();
			for(int i = 0; i < modeleMVC.getNbJoueurs(); i++) {
				for (int numEquipe = 0; numEquipe < modeleMVC.getNbEquipes(); numEquipe++) {
					agents.add(new Agent(
							new Coordinate(0, 0),
							0.35,
							modeleMVC.getVitesseDeplacement(),
							0.5,
							180,
							Team.numEquipeToTeam(numEquipe+1),
							Optional.empty(),
							modeleMVC.getModelEquipeIndex(numEquipe)
					));
				}
			}
			objects = map.getGameObjects();
			engine = new Engine(modeleMVC.getNbEquipes(), agents, map, objects, display, modeleMVC.getTempsReaparition(), 1.5, modeleMVC.getSeed());

			//Label d'affichage des TPS de l'engine
			Label labelTpsEngine = new Label("TPS : "+ engine.getTps());
			// label seed
			Label labelSeed = new Label("Seed : "+ modeleMVC.getSeed());
			//Bouton pour changer les TPS
			Button boutonDeceleration = new Button("Décélerer");
			Button boutonPause = new Button("Pause");
			Button boutonAcceleration = new Button("Accélérer");
			Button boutonStop = new Button("Stop");
			Button bouttonRelancerPartie = new Button("Recommencer");
			HBox boutons = new HBox(boutonDeceleration, boutonPause, boutonAcceleration, boutonStop, bouttonRelancerPartie);
			//Button d'affichage du débug
			CheckBox buttonDebug = new CheckBox("Debug");
			// Sauvegarder Partie
			Button boutonSave = new Button("Save");
			ControlerSave controlerSave = new ControlerSave(modeleMVC);
			boutonSave.setOnMouseClicked(controlerSave::handle);
			Button bouttonNouvellePartie = new Button("Nouvelle Partie");
			Button bouttonChargerPartie = new Button("Charger Partie");

			//Choix du Tps
			Slider choixTpsSlider = new Slider(1, 256, engine.getTps());
			choixTpsSlider.setMajorTickUnit(1);         // Espacement entre les ticks principaux
			choixTpsSlider.setMinorTickCount(0);        // Pas de ticks intermédiaires
			choixTpsSlider.setSnapToTicks(true);        // Alignement sur les ticks
			choixTpsSlider.setShowTickMarks(true);      // Afficher les ticks
			choixTpsSlider.setShowTickLabels(true);     // Afficher les labels
			Label choixTpsLabel = new Label("TPS");
			VBox choixTps = new VBox(choixTpsLabel, choixTpsSlider);

			VBox vboxControleurs = new VBox(labelTpsEngine, labelTpsActualEngine, boutons, choixTps, buttonDebug, boutonSave, bouttonNouvellePartie, bouttonChargerPartie);
			VBox vboxInfos = new VBox(labelSeed);
			//ajout des informations des équipes
			for (int numEquipe = 0; numEquipe < modeleMVC.getNbEquipes(); numEquipe++) {
				vboxInfos.getChildren().add(labelsNbJoueursMorts[numEquipe]);
				vboxInfos.getChildren().add(labelsTempsProchaineReaparitionEquipes[numEquipe]);
			}

			//Positionnement des box
			this.setCenter(simulationBox);
			this.setBottom(vboxControleurs);
			this.setRight(vboxInfos);

			//Controlers des boutons et slider
			boutonDeceleration.setOnMouseClicked((EventHandler<MouseEvent>) e -> {
				int newTps = (int)engine.getTps()/2;
				engine.setTps(newTps);
				labelTpsEngine.setText("TPS : " + String.valueOf(newTps));
				choixTpsSlider.setValue(newTps);
			});
			boutonPause.setOnMouseClicked((EventHandler<MouseEvent>) e -> {
				int newTps;
				if (engine.getTps() == 0) {
					newTps = 1;
					boutonPause.setText("Pause");
				}
				else {
					newTps = 0;
					boutonPause.setText("Play");
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
			boutonStop.setOnMouseClicked((EventHandler<? super MouseEvent>) e -> {
				stopSimulation();
				labelTpsEngine.setText("Simulation arrêtée");
            });
			choixTpsSlider.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
				int newTps = (int)engine.getTps()/2;
				engine.setTps(new_val.intValue());
				labelTpsEngine.setText("TPS : " + String.valueOf(new_val.intValue()));
			});
			buttonDebug.setOnMouseClicked((EventHandler<MouseEvent>) e -> {
				display.setDebug(!display.getDebug());
			});
			bouttonChargerPartie.setOnMouseClicked((EventHandler<MouseEvent>) e -> {
				stopSimulation();
				controlerVue.handle(e);
			});
			bouttonNouvellePartie.setOnMouseClicked((EventHandler<MouseEvent>) e -> {
				stopSimulation();
				controlerVue.handle(e);
			});
			bouttonRelancerPartie.setOnMouseClicked((EventHandler<MouseEvent>) e -> {
				stopSimulation();
				controlerVue.handle(e);
			});

			//Lancement de l'engine
			Task<Void> gameTask = new Task<>() {
				@Override
				protected Void call() {
					engine.run();
					return null;
				}
			};
			gameThread = new Thread(gameTask);
			gameThread.setDaemon(true); // Stop thread when exiting
			gameThread.start();
			gameThread.interrupt();
		}
	}
	public void stopSimulation() {
		if (engine != null) {
			engine.stop();
		}
		if (gameThread != null && gameThread.isAlive()) {
			gameThread.interrupt(); // Arrête le thread
		}
	}
}
