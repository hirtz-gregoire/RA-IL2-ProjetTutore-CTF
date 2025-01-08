package display.model;

import display.views.MapEditor.EnumMapEditor;
import display.views.RunSimu.EnumRunSimu;
import ia.model.Model;

public class MapEditorModel extends ModelMVC {

    private EnumMapEditor actualMapEditorView = EnumMapEditor.Mode;

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
}
