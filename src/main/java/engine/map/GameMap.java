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

    private GameMap(List<List<Cell>> cells) {
        this.cells = cells;
    }


    public static GameMap loadFile(String fileName) throws IOException {
        if(fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }
        File file = new File(fileName);
        if(!file.exists()) {
            throw new IllegalArgumentException("File does not exist: " + fileName);
        }
        return loadFile(file);
    }

    public static GameMap loadFile(File file) throws IOException {
        if(file == null || !file.exists()) {
            throw new IllegalArgumentException("File does not exist: " + file);
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String[] header = reader.readLine().split(";");

        int columns = Integer.parseInt(header[0]);
        int rows = Integer.parseInt(header[1]);

        // Init lists
        List<List<Cell>> cells = new ArrayList<>(rows);
        List<List<Character>> cellType = new ArrayList<>(rows);

        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                cellType.get(i).add((char) reader.read());
            }
        }

        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                char teamChar = (char) reader.read();
                Team team;
                switch (teamChar){
                    case '1'-> team = Team.BLUE;
                    case '2'-> team = Team.PINK;
                    default -> team = Team.NEUTRAL;
                }

                Cell newCell;
                switch (cellType.get(i).get(j)){
                    case '#'-> newCell = new Wall();
                    case '0'-> newCell = new SpawningCell(team);
                    default -> newCell = new Ground(team);
                }
                cells.get(i).add(newCell);
            }
        }

        return new GameMap(cells);
    }

    public Cell getCell(int i, int j) {
        return cells.get(i).get(j);
    }
}