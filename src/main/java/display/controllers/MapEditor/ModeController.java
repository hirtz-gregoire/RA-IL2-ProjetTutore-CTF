package display.controllers.MapEditor;

import display.controllers.Controller;
import display.model.MapEditorModel;
import display.views.MapEditor.EnumMapEditor;
import display.views.RunSimu.EnumRunSimu;

public class ModeController extends Controller {

    public void newMap(){
        MapEditorModel model = (MapEditorModel) this.model;
        model.setActualMapEditorView(EnumMapEditor.MapParameters);

        model.update();
        model.getGlobalModel().updateRacine();
    }

    public void loadMap(){
        MapEditorModel model = (MapEditorModel) this.model;
        model.setActualMapEditorView(EnumMapEditor.ChoiceMap);

        model.update();
        model.getGlobalModel().updateRacine();
    }

}
