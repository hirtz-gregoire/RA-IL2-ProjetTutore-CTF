package display.controllers.MapEditor;

import display.controllers.Controller;
import display.model.MapEditorModel;
import display.model.ModelMVC;
import engine.Coordinate;
import engine.Team;
import engine.map.Cell;
import engine.map.Ground;
import engine.map.Wall;
import engine.object.Flag;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MapCreatorController extends Controller {
    private int[][] mapTeam;
    private CellType[][] mapCellType;
    private CellType selectedCellType = CellType.VIDE;
    enum CellType {
        MUR, VIDE, FLAG, SPAWN;
    }

    @FXML
    public void initialize() {

    }

    public void setSelectedCellType(ActionEvent event) {
        //Récupération du bouton cliqué
        Button clickedButton = (Button) event.getSource();

        switch (clickedButton.getId()) {
            case "mur" -> selectedCellType = CellType.MUR;
            case "ground" -> selectedCellType = CellType.VIDE;
            case "flag" -> selectedCellType = CellType.FLAG;
            case "spawn" -> selectedCellType = CellType.SPAWN;
        }
    }


    public void saveMap() {
        MapEditorModel model = (MapEditorModel) this.model;
        System.out.println(model);
        //Vérifier que la carte est valide (au moins un chemin est une zone de spawn pour chaque équipe + chemin disponible entre tous les drapeaux)
    }
}
