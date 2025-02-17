package display;

import display.model.GlobalModel;
import display.views.ViewType;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    public static final ViewType DEFAULT_VIEWTYPE = ViewType.RunSimu;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {

        GlobalModel model = GlobalModel.getInstance();

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        double sceneWidth = screenWidth * 0.8;
        double sceneHeight = screenHeight * 0.8;

        double sceneMinWidth = screenWidth * 0.3;
        double sceneMinHeight = screenHeight * 0.3;

        Scene scene = new Scene(model.getPane(), sceneWidth, sceneHeight);
        stage.setMinWidth(sceneMinWidth);
        stage.setMinHeight(sceneMinHeight);

        stage.setScene(scene);
        stage.setTitle("Projet Tutoré - CTF");
        stage.show();
    }
}
