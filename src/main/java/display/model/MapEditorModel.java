package display.model;

import display.views.MapEditor.EnumMapEditor;
import engine.map.EditorMap;
import engine.map.GameMap;

import java.io.File;
import java.io.IOException;
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
}
