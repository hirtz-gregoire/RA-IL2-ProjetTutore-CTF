import display.Display;
import display.OtherDisplay;
import engine.Engine;
import engine.agent.*;
import engine.map.*;
import engine.object.GameObject;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;

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
        display = new OtherDisplay(boxDisplay);
        engine = new Engine(agents, map, objects, display);

        //Lancement de l'engine
        engine.run();

        //La page principale
        BorderPane page = new BorderPane();
        //Box du display au millieu de la page
        page.setCenter(boxDisplay);

        //scene et stage
        Scene scene = new Scene(page,600,600);
        stage.setScene(scene);
        stage.setTitle("CTF");
        stage.show();
    }
}
