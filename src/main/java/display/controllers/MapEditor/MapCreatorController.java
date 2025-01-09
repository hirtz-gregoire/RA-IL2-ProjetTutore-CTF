package display.controllers.MapEditor;

import display.controllers.Controller;
import display.model.MapEditorModel;
import engine.Team;
import engine.map.Cell;
import engine.map.GameMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import display.model.MapEditorModel.CellType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MapCreatorController extends Controller {
    @FXML
    private TextField textFieldMapName;
    @FXML
    private Label labelErrorSaveMap;

    public void setSelectedCellType(ActionEvent event) {
        MapEditorModel model = (MapEditorModel) this.model;
        //Récupération du bouton cliqué
        Button clickedButton = (Button) event.getSource();
        CellType selectedCellType = null;

        switch (clickedButton.getId()) {
            case "mur" -> selectedCellType = CellType.WALL;
            case "ground" -> selectedCellType = CellType.EMPTY;
            case "flag" -> selectedCellType = CellType.FLAG;
            case "spawn" -> selectedCellType = CellType.SPAWN;
        }
        model.setSelectedCellType(selectedCellType);
    }

    public void saveMap() throws IOException {
        MapEditorModel model = (MapEditorModel) this.model;

        labelErrorSaveMap.setText("");

        if (textFieldMapName.getText().isEmpty()) {
            labelErrorSaveMap.setText("Nom de la map non renseignée");
        }
        else {
            model.setMapName(textFieldMapName.getText());
            //Remettre ces deux lignes pour créer des cartes librement
            //GameMap.saveFile(model.getMapName(), model.getHeightMap(), model.getWidthMap(), model.getMapTeam(), model.getMapCellType());
            //return;

            //Comptage des types de cases pour chaque équipe
            //HashMap<numEquipe, HashMap<cellType, count>>
            HashMap<Integer, HashMap<CellType, Integer>> count = new HashMap<>();
            for (int row = 0; row < model.getHeightMap(); row++) {
                for (int col = 0; col < model.getWidthMap(); col++) {
                    int numTeam = model.getCellTeam(row, col);
                    CellType cellType = model.getCellType(row, col);
                    if (numTeam != 0 && !count.containsKey(numTeam)) {
                        count.put(numTeam, new HashMap<>());
                        count.get(numTeam).put(CellType.FLAG, 0);
                        count.get(numTeam).put(CellType.SPAWN, 0);
                    }
                    switch (cellType) {
                        case FLAG -> count.get(numTeam).replace(cellType, count.get(numTeam).get(CellType.FLAG) + 1);
                        case SPAWN -> count.get(numTeam).replace(cellType, count.get(numTeam).get(CellType.SPAWN) + 1);
                    }
                }
            }

            if (count.size() < model.getNbTeam()) {
                labelErrorSaveMap.setText("Toutes les équipes n'ont pas de drapeau ni de zone de spawn");
            }
            else {
                boolean carteValide = true;
                ArrayList<Integer> numEquipeInvalide = new ArrayList<>();
                for (Map.Entry<Integer, HashMap<CellType, Integer>> entry : count.entrySet()) {
                    int numEquipe = entry.getKey();
                    HashMap<CellType, Integer> value = entry.getValue();
                    for (Map.Entry<CellType, Integer> entry2 : value.entrySet()) {
                        if (entry2.getValue() == 0) {
                            numEquipeInvalide.add(numEquipe);
                            carteValide = false;
                            break;
                        }
                    }
                }

                if (!carteValide) {
                    for (Integer numEquipe : numEquipeInvalide) {
                        labelErrorSaveMap.setText(labelErrorSaveMap.getText() + "Equipe "+ Team.numEquipeToString(numEquipe)+" invalide (zone de spawn ou drapeau non présent). ");
                    }
                }
                else {
                    //Verification que toutes les cases importantes sont reliées entre elles (drapeaux, zone de spawn)
                    //Chercher la première case
                    for (int row = 0; row < model.getHeightMap(); row++) {
                        for (int col = 0; col < model.getWidthMap(); col++) {
                            CellType cellType = model.getCellType(row, col);
                            if (cellType != CellType.SPAWN) {
                                int startRow = row;
                                int startCol = col;
                            }
                        }
                    }
                    //Partir de la première case


                    GameMap.saveFile(model.getMapName(), model.getHeightMap(), model.getWidthMap(), model.getMapTeam(), model.getMapCellType());
                    labelErrorSaveMap.setText(" ~ Partie sauvgardée avec succès ! ~ ");
                }
            }
        }
    }
}
