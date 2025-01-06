package display.views;

import display.controllers.MainMenuCtrl;
import display.controllers.RunSimuCtrl;
import display.model.ModelMVC;

import java.io.IOException;

public enum ViewType {
    MainMenu,
    RunSimu;

    public static View getViewInstance(ViewType type, ModelMVC modelMVC) throws IOException {
        switch (type) {
            case MainMenu:
                return new MainMenu(modelMVC, new MainMenuCtrl());
            case RunSimu:
                return new RunSimu(modelMVC, new RunSimuCtrl());
            default:
                throw new IllegalArgumentException("Unknown view type: " + type);
        }
    }
}
