import controlers.ControlerSimulation;
import display.Display;
import display.DisplaySimulation;
import engine.Engine;
import engine.Team;
import engine.agent.*;
import engine.map.*;
import engine.object.GameObject;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import views.ControlerVue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class App extends Application {
    List<Agent> agents = null;
    GameMap map = null;
    List<GameObject> objects = null;
    Engine engine = null;
    Display display = null;

    /**
     * Méthode de lancement de l'application, répare l'erreur "Error: JavaFX runtime components are missing, and are required to run this application"
     * d'après <a href=https://stackoverflow.com/questions/56894627/how-to-fix-error-javafx-runtime-components-are-missing-and-are-required-to-ru>ce lien</a>
     */
    public void go() {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        //Box dans lequel va être le display
        VBox boxDisplay = new VBox();

        //Création des objets
        display = new OtherDisplay(boxDisplay);
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
        map = GameMap.loadFile("ressources/maps/open_space.txt");
        objects = new ArrayList<>();
        objects.add(
                new Flag(
                        new Coordinate(1, 1),
                        Team.PINK
                )
        );

        engine = new Engine(agents, map, objects, display, 10);


        //La page principale
        BorderPane page = new BorderPane();
        //Box du display au millieu de la page
        page.setCenter(boxDisplay);

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
        //Choix du Tps
        Slider choixTpsSlider = new Slider(-100, 100, 20);
        choixTpsSlider.setMajorTickUnit(1);         // Espacement entre les ticks principaux
        choixTpsSlider.setMinorTickCount(0);        // Pas de ticks intermédiaires
        choixTpsSlider.setSnapToTicks(true);        // Alignement sur les ticks
        choixTpsSlider.setShowTickMarks(true);      // Afficher les ticks
        choixTpsSlider.setShowTickLabels(true);     // Afficher les labels
        Label choixTpsLabel = new Label("TPS");
        VBox choixTps = new VBox(choixTpsLabel, choixTpsSlider);
        //vbox contenant les boutons + modifier le tps précisément
        VBox controlesSimulation = new VBox(boutons, choixTps);
        //Les boutons s'affichent en bas de la border pane
        page.setBottom(controlesSimulation);

        //scene et stage
        Scene scene = new Scene(page,600,600);
        stage.setScene(scene);
        stage.setTitle("CTF");

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
        stage.show();
        gameThread.interrupt();
    }
}
