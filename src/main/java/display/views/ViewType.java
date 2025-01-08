package display.views;

import display.model.GlobalModel;
import display.model.MainMenuModel;
import display.model.ModelMVC;
import display.model.RunSimuModel;

import java.io.IOException;

public enum ViewType {
    MainMenu,
    RunSimu;

    public static View getViewInstance(ViewType type, GlobalModel globalMode) throws IOException {
        return switch (type) {
            case MainMenu -> ModelMVC.getInstance(MainMenuModel.class, globalMode).getView();
            case RunSimu -> ModelMVC.getInstance(RunSimuModel.class, globalMode).getView();
        };
    }
}
