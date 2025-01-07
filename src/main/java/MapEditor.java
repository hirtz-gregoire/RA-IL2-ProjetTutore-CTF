import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class MapEditor extends Application {
    // Taille de la grille
    private static final int GRID_SIZE = 10;
    private static final int TILE_SIZE = 40;

    // Type de case sélectionnée
    private CellType selectedType = CellType.VIDE;

    // Enum pour les types de cases
    enum CellType {
        MUR, VIDE_EQUIPE_1, VIDE_EQUIPE_2, VIDE
    }

    @Override
    public void start(Stage primaryStage) {
        // Conteneur principal
        BorderPane root = new BorderPane();

        // Grille
        GridPane grid = new GridPane();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Rectangle cell = createCell();
                grid.add(cell, col, row);
            }
        }
        root.setCenter(grid);

        // Menu de sélection
        HBox menu = new HBox(10);
        menu.setStyle("-fx-padding: 10; -fx-background-color: #ddd;");

        Button murButton = new Button("Mur");
        murButton.setOnAction(e -> selectedType = CellType.MUR);

        Button equipe1Button = new Button("Equipe 1");
        equipe1Button.setOnAction(e -> selectedType = CellType.VIDE_EQUIPE_1);

        Button equipe2Button = new Button("Equipe 2");
        equipe2Button.setOnAction(e -> selectedType = CellType.VIDE_EQUIPE_2);

        Button videButton = new Button("Vide");
        videButton.setOnAction(e -> selectedType = CellType.VIDE);

        menu.getChildren().addAll(murButton, equipe1Button, equipe2Button, videButton);
        root.setTop(menu);

        // Créer la scène
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setTitle("Éditeur de Carte 2D");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Rectangle createCell() {
        Rectangle cell = new Rectangle(TILE_SIZE, TILE_SIZE, Color.WHITE);
        cell.setStroke(Color.BLACK);

        cell.setOnMouseClicked(event -> {
            switch (selectedType) {
                case MUR -> cell.setFill(Color.BLACK);
                case VIDE_EQUIPE_1 -> cell.setFill(Color.BLUE);
                case VIDE_EQUIPE_2 -> cell.setFill(Color.RED);
                case VIDE -> cell.setFill(Color.WHITE);
            }
        });

        return cell;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
