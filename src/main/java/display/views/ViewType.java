package display.views;

import display.model.*;

import java.io.IOException;

public enum ViewType {
    Top,
    MainMenu,
    RunSimu,
    Learning,
    MapEditor;

    public static ModelMVC getViewInstance(ViewType type, GlobalModel globalMode) throws IOException {
        return switch (type) {
            case Top -> ModelMVC.getInstance(TopModel.class, globalMode);
            case MainMenu -> ModelMVC.getInstance(MainMenuModel.class, globalMode);
            case RunSimu -> ModelMVC.getInstance(RunSimuModel.class, globalMode);
            case Learning -> ModelMVC.getInstance(LearningModel.class, globalMode);
            case MapEditor -> ModelMVC.getInstance(MapEditorModel.class, globalMode);
        };
    }
}
