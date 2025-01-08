package display.controllers.MapEditor;

import display.controllers.Controller;
import display.model.MapEditorModel;
import display.model.ModelMVC;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

public class MapCreatorController extends Controller {
    private int[][] mapTeam;
    private CellType[][] mapCellType;
    private CellType selectedCellType = CellType.VIDE;
    enum CellType {
        MUR, VIDE, FLAG, SPAWN;
    }

    @FXML
    private GridPane gridPaneMapTeam;
    @FXML
    private GridPane gridPaneMapCellType;

    @FXML
    public void initialize() {
        MapEditorModel model = (MapEditorModel) this.model;
        int height = model.getHeightMap();
        int width = model.getWidthMap();
        int nbTeam = model.getNbTeam();

        mapTeam = new int[height][width];
        mapCellType = new CellType[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                mapTeam[row][col] = 0;
                Rectangle cell = createCellTeam();
                gridPaneMapTeam.add(cell, row, col);

                mapCellType[row][col] = CellType.VIDE;
                
            }
        }
    }

    public void setSelectedCellType(ActionEvent event) {
        //Récupération du bouton cliqué
        Button clickedButton = (Button) event.getSource();

        switch (clickedButton.getId()) {
            case "mur" -> selectedCellType = CellType.MUR;
            case "vide" -> selectedCellType = CellType.VIDE;
            case "flag" -> selectedCellType = CellType.FLAG;
            case "spawn" -> selectedCellType = CellType.SPAWN;
        }
    }


    public void saveMap() {
        //Vérifier que la carte est valide (au moins un chemin est une zone de spawn pour chaque équipe + chemin disponible entre tous les drapeaux)
    }
}
