package views;

import display.Display;
import engine.Engine;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import modele.Modele;

import java.util.List;

public class VueApprentissageMain extends Pane implements Observateur {
	List<Agent> agents = null;
	GameMap map = null;
	List<GameObject> objects = null;
	Engine engine = null;
	Display display = null;

	public VueApprentissageMain() {
		super();
	}

	@Override
	public void actualiser(Modele modele) {
		this.getChildren().clear();  // efface toute la vue

		if (modele.getVue().equals(ViewsEnum.VueApprentissageMain)) {
			//Création des objets
			VBox simulationBox = new VBox();

			//Bouton pour changer les FPS
			Button boutonDeceleration = new Button("Décélerer");
			Button boutonPasArriere = new Button("Pas Arrière");
			Button boutonPause = new Button("Pause");
			Button boutonPasAvant = new Button("Pas Avant");
			Button boutonAcceleration = new Button("Accélérer");
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
