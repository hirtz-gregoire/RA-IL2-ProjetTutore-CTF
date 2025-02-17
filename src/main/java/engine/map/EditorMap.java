package engine.map;

import display.model.MapEditorModel.CellType;
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
public class EditorMap {

    /** A list containing lists of cells, representing the board */
    private String name;
    private int height;
    private int width;
    private int nbTeam;
    private int[][] mapTeam;
    private CellType[][] mapCellType;

    public EditorMap(){
        height = 5;
        width = 5;
        nbTeam = 2;
    }

    public EditorMap(String name, int height, int width, int nbTeam, int[][] mapTeam, CellType[][] mapCellType) {
        this.name = name;
        this.height = height;
        this.width = width;
        this.nbTeam = nbTeam;
        this.mapTeam = mapTeam;
        this.mapCellType = mapCellType;
    }

    public static void saveFile(String fileName, int height, int width, int[][] mapTeam, CellType[][] mapCellType) throws IOException {
        PrintWriter writer = new PrintWriter("ressources/maps/"+fileName+".txt", StandardCharsets.UTF_8);
        writer.print(height+"; "+width+"\n\n");
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                switch(mapCellType[row][col]){
                    case CellType.WALL -> writer.print("#");
                    case CellType.EMPTY -> writer.print(".");
                    case CellType.FLAG -> writer.print("@");
                    case CellType.SPAWN -> writer.print("O");
                }
            }
            writer.print("\n");
        }
        writer.print("\n");
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                writer.print(mapTeam[row][col]);
            }
            writer.print("\n");
        }
        writer.close();
    }

    /**
     * Load a file from the specified path and build a new GameMap with
     * @param fileName The path of the file to load
     * @return A new GameMap containing the world describe in the given file
     * @throws IOException when fileName is empty or null, or when the file does not exist
     */
    public static EditorMap loadFile(String fileName) throws IOException {
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
    public static EditorMap loadFile(File file) throws IOException {
        if(file == null || !file.exists()) {
            throw new IOException("File does not exist: " + file);
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String[] header = reader.readLine().split(";");

        int nbRow = Integer.parseInt(header[1].trim());
        int nbCol = Integer.parseInt(header[0].trim());

        int[][] mapTeam = new int[nbRow][nbCol];
        CellType[][] mapCellType = new CellType[nbRow][nbCol];

        // Skipping empty line
        reader.readLine();

        // Read first tab with cell type
        for(int row = 0; row < nbRow; row++) {
            String line = reader.readLine();
            for(int col = 0; col < nbCol; col++) {
                char chr = line.charAt(col);
                switch(chr){
                    case '.' -> mapCellType[row][col] = CellType.EMPTY;
                    case '#' -> mapCellType[row][col] = CellType.WALL;
                    case '@' -> mapCellType[row][col] = CellType.FLAG;
                    case 'O' -> mapCellType[row][col] = CellType.SPAWN;
                }
            }
        }

        // Skipping empty line
        reader.readLine();

        // Read second tab with cell team
        int nbTeam = 0;
        List<Integer> teamsPresents = new ArrayList<>();
        for(int row = 0; row < nbRow; row++) {
            String line = reader.readLine();
            for(int col = 0; col < nbCol; col++) {
                int numTeam = Integer.parseInt(String.valueOf(line.charAt(col)));
                if (!teamsPresents.contains(numTeam)) {
                    teamsPresents.add(numTeam);
                    nbTeam++;
                }
                mapTeam[row][col] = numTeam;
            }
        }
        reader.close();
        return new EditorMap(file.getName().replace(".txt",""), nbRow, nbCol, nbTeam-1, mapTeam, mapCellType);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public int getNbTeam() {
        return nbTeam;
    }
    public void setNbTeam(int nbTeam) {
        this.nbTeam = nbTeam;
    }
    public int[][] getMapTeam() {
        return mapTeam;
    }
    public void setMapTeam(int[][] mapTeam) {
        this.mapTeam = mapTeam;
    }
    public CellType[][] getMapCellType() {
        return mapCellType;
    }
    public void setMapCellType(CellType[][] mapCellType) {
        this.mapCellType = mapCellType;
    }
    public int getCellTeam(int row, int col) {
        return mapTeam[row][col];
    }
    public void setCellTeam(int row, int col, int cellValue) {
        mapTeam[row][col] = cellValue;
    }
    public CellType getCellType(int row, int col) {
        return mapCellType[row][col];
    }
    public void setCellType(int row, int col, CellType cellType) {
        mapCellType[row][col] = cellType;
    }
}