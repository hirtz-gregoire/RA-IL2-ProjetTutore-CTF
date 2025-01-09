package display.controllers.MapEditor;

import display.controllers.Controller;
import display.model.MapEditorModel;
import engine.map.GameMap;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import display.model.MapEditorModel.CellType;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class MapCreatorController extends Controller {
    public void updateMapName(ActionEvent event) {
        MapEditorModel model = (MapEditorModel) this.model;
        TextField textField = (TextField) event.getSource();
        model.setMapName(textField.getText());
        System.out.println(model.getMapName());
    }

    public void setSelectedCellType(ActionEvent event) {
        MapEditorModel model = (MapEditorModel) this.model;
        //Récupération du bouton cliqué
        Button clickedButton = (Button) event.getSource();

        CellType selectedCellType = null;

        switch (clickedButton.getId()) {
            case "mur" -> selectedCellType = CellType.MUR;
            case "ground" -> selectedCellType = CellType.VIDE;
            case "flag" -> selectedCellType = CellType.FLAG;
            case "spawn" -> selectedCellType = CellType.SPAWN;
        }
        model.setSelectedCellType(selectedCellType);
    }

    public void saveMap() throws IOException {
        MapEditorModel model = (MapEditorModel) this.model;

        //Vérifier que la carte est valide (au moins un chemin est une zone de spawn pour chaque équipe + chemin disponible entre tous les drapeaux)
        //TODO
        GameMap.saveFile(model.getMapName(), model.getHeightMap(), model.getWidthMap(), model.getMapTeam(), model.getMapCellType());
    }
}
