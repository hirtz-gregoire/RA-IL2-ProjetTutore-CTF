package engine.map;

import display.model.MapEditorModel.CellType;
import engine.Vector2;
import engine.Team;
import engine.object.Flag;
import engine.object.GameObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Class representing the floor and the walls presents in the world. */
public class GameMap {

    /** A list containing lists of cells, representing the board */
    private List<List<Cell>> cells;
    private List<SpawningCell> spawningCells;
    private List<GameObject> gameObjects;
    private List<Team> teams;
    private int nbEquipes;

    private String mapPath;

    public GameMap(){
        cells = new ArrayList<>();
    }

    public GameMap(List<List<Cell>> cells) {
        this.cells = cells;
    }
    public GameMap(List<List<Cell>> cells, List<SpawningCell> spawningCells, List<GameObject> gameObjects, List<Team> teams, int nbEquipes, String path) {
        this.cells = cells;
        this.spawningCells = spawningCells;
        this.gameObjects = gameObjects;
        this.teams = teams;
        this.nbEquipes = nbEquipes;
        this.mapPath = path;
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

        int rows = Integer.parseInt(header[1].trim());
        int columns = Integer.parseInt(header[0].trim());

        List<List<Cell>> cells = new ArrayList<>();
        List<List<Character>> cellType = new ArrayList<>();

        // Skipping empty line
        reader.readLine();

        // Init lists of list
        for(int i = 0; i < rows; i++) {
            cellType.add(new ArrayList<>());
            cells.add(new ArrayList<>());
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
                cells.get(row).add(newCell);
            }
        }
        reader.close();
        return new GameMap(cells, spawningCells, gameObjects, teamsPresents, nbEquipes, file.getPath());
    }

    /** @return the number of team */
    public int getNbEquipes() {
        return nbEquipes;
    }
    /** @return a copy of the list of lists of cells contained in the GameMap */
    public List<List<Cell>> getCells() {
        return new ArrayList<>(cells);
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
        List<Cell> res = new ArrayList<>();
        for(int x = base_x; x < base_x + width; x++) {
            for(int y = base_y; y < base_y + height; y++) {
                var cell = getCellFromXY(x, y);
                if(cell != null) res.add(cell);
            }
        }
        return res;
    }
    public Cell getCellFromXY(int x, int y) {
        if(x < 0 || x >= cells.size()) return null;
        if(y < 0 || y >= cells.get(x).size()) return null;
        return cells.get(x).get(y);
    }
    /** @return a copy of the list of spawning cells */
    public List<SpawningCell> getSpawningCells() { return new ArrayList<>(spawningCells); }
    /** @return a copy of the list of gameObjects*/
    public List<GameObject> getGameObjects() { return new ArrayList<>(gameObjects); }

    public List<Team> getTeams(){return this.teams;}

    public String getMapPath() {
        return mapPath;
    }
}