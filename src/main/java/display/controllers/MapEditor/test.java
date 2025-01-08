package display.controllers.MapEditor;

import display.modele.ModeleMVC;
import engine.Team;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import javax.swing.text.html.ImageView;

public class VueMapCreate extends Pane implements Observateur {
    //Grille de la carte des équipes
    GridPane gridMapEquipes;
    //Grille de la carte des types de case
    GridPane gridMapType;
    // Type de case sélectionnée
    private CellType selectedType = CellType.VIDE;
    // Enum pour les types de cases
    enum CellType {
        MUR, VIDE,
    }
    private static int TAILLE_CASE;

    public VueMapCreate() {
        super();
    }

    @Override
    public void actualiser(ModeleMVC modeleMVC) throws Exception {
        this.getChildren().clear();

        if (modeleMVC.getVue().equals(ViewsEnum.MapCreate)) {
            TAILLE_CASE = Math.round(512 / Math.max(modeleMVC.getWidthCarte(), modeleMVC.getHeightCarte()*2));

            BorderPane borderPane = new BorderPane();

            // Grille des équipes
            gridMapEquipes = new GridPane();
            for (int row = 0; row < modeleMVC.getHeightCarte(); row++) {
                for (int col = 0; col < modeleMVC.getWidthCarte(); col++) {
                    Rectangle cell = createCellEquipe(modeleMVC);
                    gridMapEquipes.add(cell, col, row);
                }
            }

            // Grille de la carte
            gridMapType = new GridPane();
            for (int row = 0; row < modeleMVC.getHeightCarte(); row++) {
                for (int col = 0; col < modeleMVC.getWidthCarte(); col++) {
                    //Rectangle cell = createCellType(modeleMVC);
                    //gridMapType.add(cell, col, row);
                }
            }

            HBox hboxgridMaps = new HBox(gridMapType, gridMapEquipes);
            borderPane.setCenter(hboxgridMaps);

            // Menu de sélection du type de case
            VBox menuChoixCase = new VBox();

            Button murButton = new Button("Mur");
            murButton.setOnAction(e -> selectedType = CellType.MUR);

            Button videButton = new Button("Vide");
            videButton.setOnAction(e -> selectedType = CellType.VIDE);

            menuChoixCase.getChildren().addAll(murButton, videButton);
            borderPane.setRight(menuChoixCase);

            this.getChildren().add(borderPane);
        }
    }

    private Rectangle createCellEquipe(ModeleMVC modeleMVC) {
        Rectangle cell = new Rectangle(TAILLE_CASE, TAILLE_CASE, Color.WHITE);
        cell.setUserData(1);
        cell.setStroke(Color.BLACK);

        cell.setOnMouseClicked(event -> {
            cell.setUserData((int)cell.getUserData() + 1);
            if ((int)cell.getUserData() > modeleMVC.getNbEquipes()) {
                cell.setUserData(0);
                cell.setFill(Color.rgb(255, 255, 255));
            }
            else {
                cell.setFill(Team.numEquipeToColor((int)cell.getUserData()));
            }
        });

        return cell;
    }

    private ImageView createCellType(ModeleMVC modeleMVC) {
        return null;
    }
}
