import controlers.ControlerSimulation;
import display.Display;
import display.DisplaySimulation;
import engine.Engine;
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

import java.util.List;

public class App extends Application {
    List<Agent> agents = null;
    GameMap map = null;
    List<GameObject> objects = null;
    Engine engine = null;
    Display display = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        //Box dans lequel va être le display
        VBox boxDisplay = new VBox();

        //Création des objets
        display = new DisplaySimulation(boxDisplay);
        engine = new Engine(agents, map, objects, display);

        //Controler de l'affichage qui contient l'engine pour modifier le fps
        ControlerSimulation controlerSimulation = new ControlerSimulation(engine);
        //Controler pour modifier les vues
        ControlerVue controlerVue = new ControlerVue(engine);

        //Lancement de l'engine
        engine.run();

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
        stage.show();
    }
}
