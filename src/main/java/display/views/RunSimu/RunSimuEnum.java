package display.views.RunSimu;

import display.model.ModelMVC;
import display.views.View;

import java.io.IOException;

public enum RunSimuEnum {
    Main,
    Menu;

    public static View getRunSimuEnum(RunSimuEnum runSimuEnum, ModelMVC modelMVC) throws IOException {
        switch (runSimuEnum) {
            case Main -> new RunSimuMain(modelMVC);
            case Menu -> new RunSimuMenu(modelMVC);
        }
    }
}
