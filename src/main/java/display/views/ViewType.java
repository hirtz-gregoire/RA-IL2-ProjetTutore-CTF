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
        switch (type) {
            case MainMenu:
                return new MainMenu(ModelMVC.getInstance(MainMenuModel.class, globalMode));
            case RunSimu:
                return new RunSimu(ModelMVC.getInstance(RunSimuModel.class, globalMode));
            default:
                throw new IllegalArgumentException("Unknown view type: " + type);

        }
    }
}
