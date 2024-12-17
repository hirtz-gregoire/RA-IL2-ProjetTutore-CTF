package display.views;

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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import display.modele.Modele;

import java.io.BufferedReader;
import java.io.FileReader;
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
	public void actualiser(Modele modele) throws Exception {
		//Arrêter la simulation si on est plus dans la vue
		if (!this.getChildren().isEmpty()) {
			stopSimulation();
		}
		this.getChildren().clear();  // efface toute la vue

		if (modele.getVue().equals(ViewsEnum.SimulationMain)) {
			//Chargement d'une partie
			if (modele.getPartie() != null) {
				BufferedReader reader = new BufferedReader(new FileReader("ressources/parties/"+modele.getPartie()+".txt"));
				String[] header = reader.readLine().split(";");
				String map = reader.readLine();
				modele.setCarte(map);
				String[] models = reader.readLine().split(";");
				String nbJoueurs = reader.readLine();
				modele.setNbJoueurs(Integer.parseInt(nbJoueurs));
				String vitesseDeplacement = reader.readLine();
				modele.setVitesseDeplacement(Integer.parseInt(vitesseDeplacement));
				String tempsReaparition = reader.readLine();
				modele.setTempsReaparition(Integer.parseInt(tempsReaparition));
			}

			//Création des objets
			VBox simulationBox = new VBox();
			//Label d'affichage des TPS actuels de l'engine
			Label labelTpsActualEngine = new Label("TPS actuels : " + 0);
			//Nombre de joueurs morts par équipe
			Label[] labelsNbJoueursMorts = new Label[modele.getNbEquipes()];
			for (int numEquipe = 0; numEquipe < modele.getNbEquipes(); numEquipe++) {
				labelsNbJoueursMorts[numEquipe] = new Label("Nombre joueur mort équipe " + numEquipe + " : " + 0);
			}
			//Temps réstant avant la prochaine apparition
			Label[] labelsTempsProchaineReaparitionEquipes = new Label[modele.getNbEquipes()];
			for (int numEquipe = 0; numEquipe < modele.getNbEquipes(); numEquipe++) {
				labelsTempsProchaineReaparitionEquipes[numEquipe] = new Label("Temps prochaine réaparition équipe " + numEquipe + " : " + 0);
			}
			map = GameMap.loadFile("ressources/maps/"+ modele.getCarte() + ".txt");
			display = new Display(simulationBox, map, "grand", labelTpsActualEngine, labelsNbJoueursMorts, labelsTempsProchaineReaparitionEquipes);
			agents = new ArrayList<>();
			for(int i = 0; i < modele.getNbJoueurs(); i++) {
				for (int numEquipe = 1; numEquipe <= modele.getNbEquipes() + 1; numEquipe++) {
					agents.add(new Agent(
							new Coordinate(0, 0),
							0.35,
							modele.getVitesseDeplacement(),
							0.5,
							180,
							Team.numEquipeToTeam(numEquipe),
							Optional.empty(),
							new Random()
					));
				}
			}
			objects = map.getGameObjects();
			engine = new Engine(modele.getNbEquipes(), agents, map, objects, display, modele.getTempsReaparition(), 1.5);

			//Label d'affichage des TPS de l'engine
			Label labelTpsEngine = new Label("TPS : "+ engine.getTps());

			//Bouton pour changer les TPS
			Button boutonDeceleration = new Button("Décélerer");
			Button boutonPause = new Button("Pause");
			Button boutonAcceleration = new Button("Accélérer");
			Button boutonStop = new Button("Stop");
			HBox boutons = new HBox(boutonDeceleration, boutonPause, boutonAcceleration, boutonStop);

			//Button d'affichage du débug
			CheckBox buttonDebug = new CheckBox("Debug");

			//Choix du Tps
			Slider choixTpsSlider = new Slider(1, 64, engine.getTps());
			choixTpsSlider.setMajorTickUnit(1);         // Espacement entre les ticks principaux
			choixTpsSlider.setMinorTickCount(0);        // Pas de ticks intermédiaires
			choixTpsSlider.setSnapToTicks(true);        // Alignement sur les ticks
			choixTpsSlider.setShowTickMarks(true);      // Afficher les ticks
			choixTpsSlider.setShowTickLabels(true);     // Afficher les labels
			Label choixTpsLabel = new Label("TPS");
			VBox choixTps = new VBox(choixTpsLabel, choixTpsSlider);

			VBox vboxControleurs = new VBox(labelTpsEngine, labelTpsActualEngine, boutons, choixTps, buttonDebug);
			VBox vboxInfos = new VBox();
			//ajout des informations des équipes
			for (int numEquipe = 0; numEquipe < modele.getNbEquipes(); numEquipe++) {
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
				//supprimer les boutons inutiles
				this.getChildren().clear();
				modele.setVue(ViewsEnum.SimulationMenu);
			});
			choixTpsSlider.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
				int newTps = (int)engine.getTps()/2;
				engine.setTps(new_val.intValue());
				labelTpsEngine.setText("TPS : " + String.valueOf(new_val.intValue()));
			});
			buttonDebug.setOnMouseClicked((EventHandler<MouseEvent>) e -> {
				display.setDebug(!display.getDebug());
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
			engine.stop(); // Implémentez un mécanisme d'arrêt dans votre classe Engine si nécessaire
		}
		if (gameThread != null && gameThread.isAlive()) {
			gameThread.interrupt(); // Arrête le thread
		}
	}
}
