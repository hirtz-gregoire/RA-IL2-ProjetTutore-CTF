package display.model;

import display.views.MapEditor.EnumMapEditor;
import engine.map.EditorMap;
import engine.map.GameMap;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MapEditorModel extends ModelMVC {

    private EnumMapEditor actualMapEditorView = EnumMapEditor.Mode;

    // attribut pour vue ChoiceMap
    private File[] files;
    private Optional<Integer> indiceMapSelected = Optional.empty();
    EditorMap map = new EditorMap();
    GameMap mapChoice = GameMap.loadFile("ressources/maps/open_space.txt");

    private int cellSize;
    private int selectedTeam = 0;
    private CellType selectedCellType = CellType.EMPTY;
    public enum CellType {
        WALL, EMPTY, FLAG, SPAWN;
    }

    protected MapEditorModel(GlobalModel globalModel) throws IOException {
        super(globalModel);
        update();
    }

    public void update() {
        try{
            this.view = EnumMapEditor.getMapEditorEnum(actualMapEditorView, this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public EnumMapEditor getActualMapEditorView() {
        return actualMapEditorView;
    }
    public void setActualMapEditorView(EnumMapEditor actualMapEditorView) {
        this.actualMapEditorView = actualMapEditorView;
    }
    public File[] getFiles() {return files;}
    public void setFiles(File[] files) {this.files = files;}
    public Optional<Integer> getIndiceMapSelected() {return indiceMapSelected;}
    public void setIndiceMapSelected(int selected) {this.indiceMapSelected = Optional.of(selected);}
    public EditorMap getMap() {return map;}
    public void setMap(EditorMap map) {this.map = map;}
    public CellType getSelectedCellType() {
        return selectedCellType;
    }
    public void setSelectedCellType(CellType selectedCellType) {
        this.selectedCellType = selectedCellType;
    }
    public int getCellSize() {
        return cellSize;
    }
    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }
    public int getSelectedTeam() {
        return selectedTeam;
    }
    public void setSelectedTeam(int selectedTeam) {
        this.selectedTeam = selectedTeam;
    }
    public GameMap getMapChoice() {
        return mapChoice;
    }

    public void setMapChoice(GameMap mapChoice) {
        this.mapChoice = mapChoice;
    }

    //Méthode pour retourner le numéro de l'équipe dont les cases importantes ne sont pas toutes présentes sur la carte (drapeau et zone de spawn)
    public int getInvalidTeamByCellPresence() {
        //Comptage des types de cases pour chaque équipe
        HashMap<Integer, HashMap<CellType, Integer>> count = new HashMap<>();
        for (int numTeam = 1; numTeam <= map.getNbTeam(); numTeam++) {
            count.put(numTeam, new HashMap<>());
            count.get(numTeam).put(CellType.FLAG, 0);
            count.get(numTeam).put(CellType.SPAWN, 0);
        }
        for (int row = 0; row < map.getHeight(); row++) {
            for (int col = 0; col < map.getWidth(); col++) {
                int numTeam = map.getCellTeam(row, col);
                CellType cellType = map.getCellType(row, col);
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

    //Méthode pour vérifier la validité d'une carte
    public boolean getValidityMapByPath() {
        //Verification que toutes les cases importantes sont reliées entre elles (drapeaux, zone de spawn)
        //Chercher la première case
        int startRow = 0;
        int startCol = 0;
        for (int row = 0; row < map.getHeight(); row++) {
            for (int col = 0; col < map.getWidth(); col++) {
                CellType cellType = map.getCellType(row, col);
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
        for (int numTeam = 1; numTeam <= map.getNbTeam(); numTeam++) {
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
            int numTeam = map.getCellTeam(row, col);
            CellType cellType = map.getCellType(row, col);
            switch (cellType) {
                case FLAG -> count.get(numTeam).replace(cellType, count.get(numTeam).get(CellType.FLAG) + 1);
                case SPAWN -> count.get(numTeam).replace(cellType, count.get(numTeam).get(CellType.SPAWN) + 1);
            }
            //Récupération des cases adjacentes sans murs
            //HAUT
            if (row > 0 && map.getCellType(row - 1, col) != CellType.WALL && !cellVisited.contains(new Point(row - 1, col))) {
                cellToVisit.add(new Point(row - 1, col));
            }
            //BAS
            if (row < map.getHeight() - 1 && map.getCellType(row + 1, col) != CellType.WALL && !cellVisited.contains(new Point(row + 1, col))) {
                cellToVisit.add(new Point(row + 1, col));
            }
            //GAUCHE
            if (col > 0 && map.getCellType(row, col - 1) != CellType.WALL && !cellVisited.contains(new Point(row, col - 1))) {
                cellToVisit.add(new Point(row, col - 1));
            }
            //DROITE
            if (col < map.getWidth() - 1 && map.getCellType(row, col + 1) != CellType.WALL && !cellVisited.contains(new Point(row, col + 1))) {
                cellToVisit.add(new Point(row, col + 1));
            }
        }

        //Une fois que toutes les cases possibles ont été visitées, on regarde si toutes les cases importantes sont présentes
        for (Map.Entry<Integer, HashMap<CellType, Integer>> entry : count.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        for (Map.Entry<Integer, HashMap<CellType, Integer>> entry : count.entrySet()) {
            int numEquipe = entry.getKey();
            HashMap<CellType, Integer> value = entry.getValue();
            for (Map.Entry<CellType, Integer> entry2 : value.entrySet()) {
                System.out.println(entry2.getValue());
                if (entry2.getValue() == 0) {
                    return false;
                }
            }
        }

        return true;
    }
}
