package display.views.MapEditor;

import display.model.ModelMVC;
import display.views.View;

import java.io.IOException;

public enum EnumMapEditor {
    Mode,
    MapCreator,
    ChoiceMap,
    MapModify;

    public static View getMapEditorEnum(EnumMapEditor enumMapEditor, ModelMVC modelMVC) throws IOException {
        return switch (enumMapEditor) {
            case Mode -> new Mode(modelMVC);
            case MapCreator -> new MapCreator(modelMVC);
            case ChoiceMap -> new ChoiceMap(modelMVC);
            case MapModify -> new MapModify(modelMVC);
        };
    }
}
