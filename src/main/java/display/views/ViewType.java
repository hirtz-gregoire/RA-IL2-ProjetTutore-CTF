package display.views;

import display.model.*;

import java.io.IOException;

public enum ViewType {
    MainMenu,
    RunSimu,
    MapEditor;

    public static View getViewInstance(ViewType type, GlobalModel globalMode) throws IOException {
        return switch (type) {
            case MainMenu -> ModelMVC.getInstance(MainMenuModel.class, globalMode).getView();
            case RunSimu -> ModelMVC.getInstance(RunSimuModel.class, globalMode).getView();
            case MapEditor -> ModelMVC.getInstance(MapEditorModel.class, globalMode).getView();
        };
    }
}
