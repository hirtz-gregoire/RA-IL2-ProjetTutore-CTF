package engine.map;

import engine.Team;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.invoke.SwitchPoint;
import java.util.ArrayList;
import java.util.List;

public class GameMap {

    private List<List<Cell>> cells;

    public GameMap(){
        cells = new ArrayList<>();
    }

    public GameMap(List<List<Cell>> cells) {
        this.cells = cells;
    }


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

    public static GameMap loadFile(File file) throws IOException {
        if(file == null || !file.exists()) {
            throw new IOException("File does not exist: " + file);
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String[] header = reader.readLine().split(";");

        int rows = Integer.parseInt(header[0].trim());
        int columns = Integer.parseInt(header[1].trim());

        // Init lists
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

        for(int i = 0; i < rows; i++) {
            cells.add(new ArrayList<>());
            String line = reader.readLine();
            for(int j = 0; j < columns; j++) {
                Team team = Team.charToTeam(line.charAt(j));

                Cell newCell;
                switch (cellType.get(i).get(j)){
                    case '#'-> newCell = new Wall(team);
                    case 'O'-> newCell = new SpawningCell(team);
                    default -> newCell = new Ground(team);
                }
                cells.get(i).add(newCell);
            }
        }

        return new GameMap(cells);
    }

    public List<List<Cell>> getCells() {
        return new ArrayList<>(cells);
    }
}