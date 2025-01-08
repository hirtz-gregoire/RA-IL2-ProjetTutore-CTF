package display.model;

import display.views.MapEditor.EnumMapEditor;
import display.views.RunSimu.EnumRunSimu;
import ia.model.Model;

public class MapEditorModel extends ModelMVC {

    private EnumMapEditor actualMapEditorView = EnumMapEditor.Mode;

    private int heightMap;
    private int widthMap;
    private int nbTeam;

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
}
