package display;

import display.model.ModelMVC;
import display.views.ViewType;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        ModelMVC modele = new ModelMVC(ViewType.MainMenu);

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();
        double sceneWidth = screenWidth * 0.3;
        double sceneHeight = screenHeight * 0.3;

        Scene scene = new Scene(modele.getRootPane(), sceneWidth, sceneHeight);
        stage.setScene(scene);
        stage.setTitle("CTF");
        stage.show();
    }
}
