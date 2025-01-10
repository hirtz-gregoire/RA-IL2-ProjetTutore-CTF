package display.controllers.MapEditor;

import display.controllers.Controller;
import display.model.MapEditorModel;
import display.model.MapEditorModel.CellType;
import engine.Team;
import engine.map.EditorMap;
import engine.map.GameMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapModifyController extends Controller {
    @FXML
    private TextField textFieldMapName;
    @FXML
    private Label labelErrorSaveMap;
    @FXML
    private Rectangle rectangleTeamChoice;
    @FXML
    private Label labelTeamChoice;

    @FXML
    public void setSelectedTeam(ActionEvent event) {
        MapEditorModel model = (MapEditorModel) this.model;
        model.setSelectedTeam(model.getSelectedTeam()+1);
        if (model.getSelectedTeam() > model.getNbTeam()) {
            model.setSelectedTeam(0);
        }
        rectangleTeamChoice.setFill(Team.TeamToColor(Team.numEquipeToTeam(model.getSelectedTeam())));
        labelTeamChoice.setText(Team.numEquipeToString(model.getSelectedTeam()));
    }

    @FXML
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

            int numInvalidTeam = getInvalidTeamByCellPresence(model);
            if (numInvalidTeam != 0) {
                labelErrorSaveMap.setText(labelErrorSaveMap.getText() + "Equipe "+ Team.numEquipeToString(numInvalidTeam)+" invalide (zone de spawn ou drapeau non présent).");
            }
            else {
                if (getValidityMapByPath(model)) {
                    labelErrorSaveMap.setText(labelErrorSaveMap.getText() + "Carte Invalide : Chemin inexistant entre toutes les équipes");
                }
                else {
                    EditorMap.saveFile(model.getMapName(), model.getHeightMap(), model.getWidthMap(), model.getMapTeam(), model.getMapCellType());
                    labelErrorSaveMap.setText(" ~ Partie sauvgardée avec succès ! ~ ");
                }
            }
        }
    }

    //Méthode pour retourner le numéro de l'équipe dont les cases importantes ne sont pas toutes présentes sur la carte (drapeau et zone de spawn)
    public int getInvalidTeamByCellPresence(MapEditorModel model) {
        //Comptage des types de cases pour chaque équipe
        HashMap<Integer, HashMap<CellType, Integer>> count = new HashMap<>();
        for (int numTeam = 1; numTeam <= model.getNbTeam(); numTeam++) {
            count.put(numTeam, new HashMap<>());
            count.get(numTeam).put(CellType.FLAG, 0);
            count.get(numTeam).put(CellType.SPAWN, 0);
        }
        for (int row = 0; row < model.getHeightMap(); row++) {
            for (int col = 0; col < model.getWidthMap(); col++) {
                int numTeam = model.getCellTeam(row, col);
                CellType cellType = model.getCellType(row, col);
                switch (cellType) {
                    case FLAG -> count.get(numTeam).replace(cellType, count.get(numTeam).get(CellType.FLAG) + 1);
                    case SPAWN -> count.get(numTeam).replace(cellType, count.get(numTeam).get(CellType.SPAWN) + 1);
                }
            }
        }
        for (Map.Entry<Integer, HashMap<CellType, Integer>> entry : count.entrySet()) {
            int numEquipe = entry.getKey();
            HashMap<CellType, Integer> value = entry.getValue();
            for (Map.Entry<CellType, Integer> entry2 : value.entrySet()) {
                if (entry2.getValue() == 0) {
                    return numEquipe;
                }
            }
        }
        return 0;
    }

    //Méthode pour retourner le numéro de l'équipe dont les cases importantes ne sont pas toutes reliées avec les autres équipes sur la carte
    public boolean getValidityMapByPath(MapEditorModel model) {
        //Verification que toutes les cases importantes sont reliées entre elles (drapeaux, zone de spawn)
        //Chercher la première case
        int startRow = 0;
        int startCol = 0;
        for (int row = 0; row < model.getHeightMap(); row++) {
            for (int col = 0; col < model.getWidthMap(); col++) {
                CellType cellType = model.getCellType(row, col);
                if (cellType == CellType.SPAWN || cellType == CellType.FLAG) {
                    startRow = row;
                    startCol = col;
                }
            }
        }
        //Liste des cases à visiter
        ArrayList<Point> cellToVisit = new ArrayList<>();
        cellToVisit.add(new Point(startRow, startCol));
        //Liste des cases déjà visitées
        ArrayList<Point> cellVisited = new ArrayList<>();
        //Comptage des types de cases pour chaque équipe
        HashMap<Integer, HashMap<CellType, Integer>> count = new HashMap<>();
        for (int numTeam = 1; numTeam <= model.getNbTeam(); numTeam++) {
            count.put(numTeam, new HashMap<>());
            count.get(numTeam).put(CellType.FLAG, 0);
            count.get(numTeam).put(CellType.SPAWN, 0);
        }
        //Partir de la première case et toutes les visiter
        while (!cellToVisit.isEmpty()) {
            Point cell = cellToVisit.removeFirst();
            cellVisited.add(cell);
            int row = cell.x;
            int col = cell.y;
            int numTeam = model.getCellTeam(row, col);
            CellType cellType = model.getCellType(row, col);
            switch (cellType) {
                case FLAG -> count.get(numTeam).replace(cellType, count.get(numTeam).get(CellType.FLAG) + 1);
                case SPAWN -> count.get(numTeam).replace(cellType, count.get(numTeam).get(CellType.SPAWN) + 1);
            }
            //Récupération des cases adjacentes sans murs
            //HAUT
            if (row > 0 && model.getCellType(row-1, col) != CellType.WALL && !cellVisited.contains(new Point(row-1, col))) {
                cellToVisit.add(new Point(row-1, col));
            }
            //BAS
            if (row < model.getHeightMap()-1 && model.getCellType(row+1, col) != CellType.WALL && !cellVisited.contains(new Point(row+1, col))) {
                cellToVisit.add(new Point(row+1, col));
            }
            //GAUCHE
            if (col > 0 && model.getCellType(row, col-1) != CellType.WALL && !cellVisited.contains(new Point(row, col-1))) {
                cellToVisit.add(new Point(row, col-1));
            }
            //DROITE
            if (col < model.getWidthMap()-1 && model.getCellType(row, col+1) != CellType.WALL && !cellVisited.contains(new Point(row, col+1))) {
                cellToVisit.add(new Point(row, col+1));
            }
        }
        System.out.println(startRow +" "+startCol);
        for (Map.Entry<Integer, HashMap<CellType, Integer>> entry : count.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        for (Map.Entry<Integer, HashMap<CellType, Integer>> entry : count.entrySet()) {
            int numEquipe = entry.getKey();
            HashMap<CellType, Integer> value = entry.getValue();
            for (Map.Entry<CellType, Integer> entry2 : value.entrySet()) {
                if (entry2.getValue() == 0) {
                    return false;
                }
            }
        }

        return true;
    }
}
