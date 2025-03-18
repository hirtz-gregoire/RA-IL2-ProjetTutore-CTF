package engine.map;

import engine.Vector2;
import engine.Team;
import engine.object.Flag;
import engine.object.GameObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/** Class representing the floor and the walls presents in the world. */
public class GameMap implements Cloneable {

    /** A list containing lists of cells, representing the board */
    private Cell[][] cells;
    private List<SpawningCell> spawningCells;
    private List<GameObject> gameObjects;
    private List<Team> teams;
    private int nbEquipes;

    private String mapPath;
    private String name;

    public GameMap(){
        cells = new Cell[0][0];
    }
    public GameMap(Cell[][] cells) {
        this.cells = cells;
    }
    public GameMap(Cell[][] cells, List<SpawningCell> spawningCells, List<GameObject> gameObjects, List<Team> teams, int nbEquipes, String path, String name) {
        this.cells = cells;
        this.spawningCells = spawningCells;
        this.gameObjects = gameObjects;
        this.teams = teams;
        this.nbEquipes = nbEquipes;
        this.mapPath = path;
        this.name = name;

        runDistanceBaker(this);
    }

    /**
     * Load a file from the specified path and build a new GameMap with
     * @param fileName The path of the file to load
     * @return A new GameMap containing the world describe in the given file
     * @throws IOException when fileName is empty or null, or when the file does not exist
     */
    public static GameMap loadFile(String fileName) throws IOException {
        if(fileName == null || fileName.isBlank()) {
            throw new IOException("File name cannot be null or empty");
        }
        File file = new File(fileName);
        if(!file.exists()) {
            throw new IOException("File does not exist: " + fileName);
        }
        return loadFile(file);
    }

    /**
     * Build a new GameMap with the file
     * @param file The file to load
     * @return A new GameMap containing the world describe in the given file
     * @throws IOException when the file does not exist
     */
    public static GameMap loadFile(File file) throws IOException {
        if(file == null || !file.exists()) {
            throw new IOException("File does not exist: " + file);
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String[] header = reader.readLine().split(";");
        //System.out.println(Arrays.toString(header));
        int rows = Integer.parseInt(header[1].trim());
        int columns = Integer.parseInt(header[0].trim());

        Cell[][] cells = new Cell[rows][columns];
        List<List<Character>> cellType = new ArrayList<>();

        // Skipping empty line
        reader.readLine();

        // Init lists of list
        for(int i = 0; i < rows; i++) {
            cellType.add(new ArrayList<>());
        }

        for(int column = 0; column < columns; column++) {
            String line = reader.readLine();
            for(int row = 0; row < rows; row++) {
                cellType.get(row).add(line.charAt(row));
            }
        }

        // Skipping empty line
        reader.readLine();
        List<SpawningCell> spawningCells = new ArrayList<>();
        List<GameObject> gameObjects = new ArrayList<>();

        int nbEquipes = 0;
        List<Team> teamsPresents = new ArrayList<>();
        for(int column = 0; column < columns; column++) {
            String line = reader.readLine();
            for(int row = 0; row < rows; row++) {
                Team team = Team.charToTeam(line.charAt(row));
                if (!teamsPresents.contains(team) && !team.equals(Team.NEUTRAL)) {
                    teamsPresents.add(team);
                    nbEquipes++;
                }
                Cell newCell;
                switch (cellType.get(row).get(column)){
                    case '#'-> newCell = new Wall(new Vector2(row,column), Team.NEUTRAL);
                    case 'O'-> {
                        newCell = new SpawningCell(new Vector2(row,column), team);
                        spawningCells.add((SpawningCell) newCell);
                    }
                    case '@' -> {
                        gameObjects.add(new Flag(new Vector2(row+0.5,column+0.5), team));
                        newCell = new Ground(new Vector2(row,column), team);
                    }
                    default -> newCell = new Ground(new Vector2(row,column), team);
                }
                cells[row][column] = newCell;
            }
        }
        reader.close();

        // Compute neighbors
        for(int x = 0; x < cells.length; x++) {
            for(int y = 0; y < cells[x].length; y++) {
                Cell[] neighbor = getCellNeighbors(cells, x, y).toArray(new Cell[0]);
                cells[x][y].setNeighbours(neighbor);
            }
        }

        return new GameMap(cells, spawningCells, gameObjects, teamsPresents, nbEquipes, file.getPath(), file.getName());
    }

    /** @return the number of team */
    public int getNbEquipes() {
        return nbEquipes;
    }
    /** @return a copy of the list of lists of cells contained in the GameMap */
    public Cell[][] getCells() {
        return cells.clone();
    }

    /**
     * Get all cells in a given rectangle area for optimization
     * @param base_x The X position of the area's corner
     * @param base_y The Y position of the area's corner
     * @param width The width of the area
     * @param height The height of the area
     * @return A list of all the cell in the area
     */
    public List<Cell> getCellsInRange(int base_x, int base_y, int width, int height) {
        int size = width * height;
        Cell[] res = new Cell[size];
        int index = 0;

        for (int x = base_x; x < base_x + width; x++) {
            Cell[] row = (x >= 0 && x < cells.length) ? cells[x] : null;
            if (row == null) continue;

            for (int y = base_y; y < base_y + height; y++) {
                if (y < 0 || y >= row.length) continue;
                Cell cell = row[y];
                if (cell != null) res[index++] = cell;
            }
        }
        return Arrays.asList(Arrays.copyOf(res, index));
    }

    public Cell getCellFromXY(int x, int y) {
        if(x < 0 || x >= cells.length) return null;
        if(y < 0 || y >= cells[x].length) return null;
        return cells[x][y];
    }

    /**
     * Get all 4 neighbors of a given position, please use Cell.neighbors, this function is only here to compute them
     * @param cells The map from which we will extract neighbors
     * @param x X position of the parent in the map
     * @param y Y position of the parent in the map
     * @return A list of all the neighboring cells of the given position, including wall cells
     */
    private static List<Cell> getCellNeighbors(Cell[][] cells, int x, int y) {
        if(x < 0 || x >= cells.length) return null;
        if(y < 0 || y >= cells[x].length) return null;

        List<Cell> neighbors = new ArrayList<>(4);
        if(x + 1 < cells.length) neighbors.add(cells[x+1][y]);
        if(x - 1 >= 0) neighbors.add(cells[x-1][y]);
        if(y + 1 < cells[x].length) neighbors.add(cells[x][y + 1]);
        if (y - 1 >= 0) neighbors.add(cells[x][y - 1]);

        return neighbors;
    }

    /**
     * Pre-compute distances to territory and flags
     * @param map The map to pre-compute distances on
     */
    private static void runDistanceBaker(GameMap map) {
        for(int x = 0; x < map.getWidth(); x++) {
            for(int y = 0; y < map.getHeight(); y++) {
                map.getCellFromXY(x, y).clearBakingData();
            }
        }
        // ---------- Flags
        List<Flag> flags = new ArrayList<>();
        for(GameObject object : map.gameObjects) {
            if(object instanceof Flag flag) {
                flags.add(flag);
            }
        }
        DistanceBaker.computeDistancesForFlags(flags, map);

        // ---------- Territory
        Map<Team, List<Cell>> territory = new HashMap<>();

        for(int x = 0; x < map.getWidth(); x++) {
            for(int y = 0; y < map.getHeight(); y++) {
                var cell = map.cells[x][y];
                var cells = territory.getOrDefault(cell.getTeam(), new ArrayList<>());
                cells.add(cell);
                territory.put(cell.getTeam(), cells);
            }
        }

        for(Team team : map.teams) {
            DistanceBaker.computeDistancesForTerritoryCell(territory.get(team), map, team);
        }
    }

    /** @return a copy of the list of spawning cells */
    public List<SpawningCell> getSpawningCells() { return new ArrayList<>(spawningCells); }
    /** @return a copy of the list of gameObjects*/
    public List<GameObject> getGameObjects() { return new ArrayList<>(gameObjects); }

    public List<Team> getTeams(){return this.teams;}

    public String getMapPath() {
        return mapPath;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getWidth() {
        return cells.length;
    }
    public int getHeight() {
        return cells[0].length;
    }

    @Override
    public GameMap clone() {
        try {
            GameMap clone = (GameMap) super.clone();

            clone.nbEquipes = nbEquipes;
            clone.mapPath = mapPath;
            clone.teams = new ArrayList<>(teams);
            clone.name = new String(name);

            clone.gameObjects = new ArrayList<>(gameObjects.size());
            for(GameObject gameObject : gameObjects) {
                clone.gameObjects.add(gameObject.copy());
            }

            clone.cells = new Cell[cells.length][cells[0].length];
            for (int x = 0; x < cells.length; x++) {
                for (int y = 0; y < cells[x].length; y++) {
                    clone.cells[x][y] = cells[x][y].copy();
                }
            }

            clone.spawningCells = new ArrayList<>(spawningCells.size());
            for(SpawningCell spawningCell : spawningCells) clone.spawningCells.add(spawningCell.copy());

            // Compute neighbors
            for(int x = 0; x < clone.cells.length; x++) {
                for(int y = 0; y < clone.cells[x].length; y++) {
                    Cell[] neighbor = getCellNeighbors(clone.cells, x, y).toArray(new Cell[0]);
                    clone.cells[x][y].setNeighbours(neighbor);
                }
            }

            runDistanceBaker(clone);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}