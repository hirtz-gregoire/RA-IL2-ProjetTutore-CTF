package engine.map;

import engine.Coordinate;
import engine.Team;
import engine.object.Flag;
import engine.object.GameObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.invoke.SwitchPoint;
import java.util.ArrayList;
import java.util.List;

/** Class representing the floor and the walls presents in the world. */
public class GameMap {

    /** A list containing lists of cells, representing the board */
    private List<List<Cell>> cells;
    private List<SpawningCell> spawningCells;
    private List<GameObject> gameObjects;

    public GameMap(){
        cells = new ArrayList<>();
    }

    public GameMap(List<List<Cell>> cells) {
        this.cells = cells;
    }
    public GameMap(List<List<Cell>> cells, List<SpawningCell> spawningCells, List<GameObject> gameObjects) {
        this.cells = cells;
        this.spawningCells = spawningCells;
        this.gameObjects = gameObjects;
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

        int rows = Integer.parseInt(header[0].trim());
        int columns = Integer.parseInt(header[1].trim());

        List<List<Cell>> cells = new ArrayList<>();
        List<List<Character>> cellType = new ArrayList<>();

        // Skipping empty line
        reader.readLine();

        for(int i = 0; i < rows; i++) {
            cellType.add(new ArrayList<>());
            String line = reader.readLine();
            for(int j = 0; j < columns; j++) {
                cellType.get(i).add(line.charAt(j));
            }
        }

        // Skipping empty line
        reader.readLine();
        List<SpawningCell> spawningCells = new ArrayList<>();
        List<GameObject> gameObjects = new ArrayList<>();

        for(int i = 0; i < rows; i++) {
            cells.add(new ArrayList<>());
            String line = reader.readLine();
            for(int j = 0; j < columns; j++) {
                Team team = Team.charToTeam(line.charAt(j));
                Cell newCell;
                switch (cellType.get(i).get(j)){
                    case '#'-> newCell = new Wall(new Coordinate(i,j), team);
                    case 'O'-> {
                        newCell = new SpawningCell(new Coordinate(i,j), team);
                        spawningCells.add((SpawningCell) newCell);
                    }
                    case '@' -> {
                        gameObjects.add(new Flag(new Coordinate(i,j), team));
                        newCell = new Ground(new Coordinate(i,j), team);
                    }
                    default -> newCell = new Ground(new Coordinate(i,j), team);
                }
                cells.get(i).add(newCell);
            }
        }
        reader.close();
        return new GameMap(cells, spawningCells,gameObjects);
    }

    /** @return a copy of the list of lists of cells contained in the GameMap */
    public List<List<Cell>> getCells() {
        return new ArrayList<>(cells);
    }
    /** @return a copy of the list of spawning cells */
    public List<SpawningCell> getSpawningCells() { return new ArrayList<>(spawningCells); }
    /** @return a copy of the list of gameObjects*/
    public List<GameObject> getGameObjects() { return new ArrayList<>(gameObjects); }
}