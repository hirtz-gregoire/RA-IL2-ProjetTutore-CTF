package display.model;

import display.views.MapEditor.EnumMapEditor;
import engine.map.EditorMap;
import engine.map.GameMap;

import java.awt.Point;
import java.util.*;
import java.io.File;
import java.io.IOException;

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
        try {
            this.view = EnumMapEditor.getMapEditorEnum(actualMapEditorView, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public EnumMapEditor getActualMapEditorView() {
        return actualMapEditorView;
    }

    public void setActualMapEditorView(EnumMapEditor actualMapEditorView) {
        this.actualMapEditorView = actualMapEditorView;
    }

    public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }

    public Optional<Integer> getIndiceMapSelected() {
        return indiceMapSelected;
    }

    public void setIndiceMapSelected(int selected) {
        this.indiceMapSelected = Optional.of(selected);
    }

    public EditorMap getMap() {
        return map;
    }

    public void setMap(EditorMap map) {
        this.map = map;
    }

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
        AStarPathfinding aStarPathfinding = new AStarPathfinding(map);
        if (aStarPathfinding.isValidMap()) {
            return true;
        } else {
            return false;
        }
    }

    class Node implements Comparable<Node> {
        Point position;
        int g, h, f;
        Node parent;

        public Node(Point position, int g, int h, Node parent) {
            this.position = position;
            this.g = g;
            this.h = h;
            this.f = g + h;
            this.parent = parent;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.f, other.f);
        }
    }

    public class AStarPathfinding {
        private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        private EditorMap map;

        public AStarPathfinding(EditorMap map) {
            this.map = map;
        }

        //Distance de Manhattan
        private int heuristic(Point a, Point b) {
            return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
        }

        public boolean existsPath(Point start, Point goal) {
            PriorityQueue<Node> openSet = new PriorityQueue<>();
            HashSet<Point> closedSet = new HashSet<>();

            openSet.add(new Node(start, 0, heuristic(start, goal), null));

            while (!openSet.isEmpty()) {
                Node current = openSet.poll();
                if (current.position.equals(goal)) {
                    return true;
                }
                closedSet.add(current.position);

                for (int[] dir : DIRECTIONS) {
                    int newRow = current.position.x + dir[0];
                    int newCol = current.position.y + dir[1];
                    Point neighbor = new Point(newRow, newCol);

                    if (newRow < 0 || newRow >= map.getHeight() || newCol < 0 || newCol >= map.getWidth()) {
                        continue;
                    }
                    if (map.getCellType(newRow, newCol) == MapEditorModel.CellType.WALL || closedSet.contains(neighbor)) {
                        continue;
                    }

                    int newG = current.g + 1;
                    Node neighborNode = new Node(neighbor, newG, heuristic(neighbor, goal), current);

                    openSet.add(neighborNode);
                }
            }
            return false;
        }

        public boolean isValidMap() {
            List<Point> importantCells = new ArrayList<>();

            for (int row = 0; row < map.getHeight(); row++) {
                for (int col = 0; col < map.getWidth(); col++) {
                    if (map.getCellType(row, col) == MapEditorModel.CellType.SPAWN || map.getCellType(row, col) == MapEditorModel.CellType.FLAG) {
                        importantCells.add(new Point(row, col));
                    }
                }
            }

            for (int i = 0; i < importantCells.size(); i++) {
                for (int j = i + 1; j < importantCells.size(); j++) {
                    if (!existsPath(importantCells.get(i), importantCells.get(j))) {
                        return false;
                    }
                }
            }
            return true;
        }
    }
}


