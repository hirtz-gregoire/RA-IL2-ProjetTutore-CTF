package display.model;

import display.views.MapEditor.EnumMapEditor;

public class MapEditorModel extends ModelMVC {

    private EnumMapEditor actualMapEditorView = EnumMapEditor.Mode;

    private int heightMap = 5;
    private int widthMap = 5;
    private int nbTeam = 2;

    private final int TAILLE_CASE = 32;

    private String mapName;
    private int[][] mapTeam;
    private CellType[][] mapCellType;
    private CellType selectedCellType = CellType.VIDE;
    public enum CellType {
        MUR, VIDE, FLAG, SPAWN;
    }

    protected MapEditorModel(GlobalModel globalModel) {
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
    public int getWidthMap() {
        return widthMap;
    }
    public void setWidthMap(int widthMap) {
        this.widthMap = widthMap;
    }
    public int getNbTeam() {
        return nbTeam;
    }
    public void setNbTeam(int nbTeam) {
        this.nbTeam = nbTeam;
    }
    public int getHeightMap() {
        return heightMap;
    }
    public void setHeightMap(int heightMap) {
        this.heightMap = heightMap;
    }
    public CellType getSelectedCellType() {
        return selectedCellType;
    }
    public void setSelectedCellType(CellType selectedCellType) {
        this.selectedCellType = selectedCellType;
    }
    public int getTAILLE_CASE() {
        return TAILLE_CASE;
    }
    public void setMapTeam(int[][] mapTeam) {
        this.mapTeam = mapTeam;
    }
    public int getCellTeam(int row, int col) {
        return mapTeam[row][col];
    }
    public void setCellTeam(int row, int col, int cellValue) {
        mapTeam[row][col] = cellValue;
    }
    public void setMapCellType(CellType[][] mapCellType) {
        this.mapCellType = mapCellType;
    }
    public CellType getCellType(int row, int col) {
        return mapCellType[row][col];
    }
    public void setCellType(int row, int col, CellType cellType) {
        mapCellType[row][col] = cellType;
    }
    public int[][] getMapTeam() {
        return mapTeam;
    }
    public CellType[][] getMapCellType() {
        return mapCellType;
    }
    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
}
