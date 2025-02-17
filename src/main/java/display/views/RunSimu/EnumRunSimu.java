package display.views.RunSimu;

import display.model.ModelMVC;
import display.views.View;

import java.io.IOException;

public enum EnumRunSimu {
    Main,
    Mode,
    Config,
    ChoiceMap,
    LoadGame;

    public static View getRunSimuEnum(EnumRunSimu anEnumRunSimu, ModelMVC modelMVC) throws IOException {
        return switch (anEnumRunSimu) {
            case Main -> new Main(modelMVC);
            case Mode -> new Mode(modelMVC);
            case Config -> new Config(modelMVC);
            case ChoiceMap -> new ChoiceMap(modelMVC);
            case LoadGame -> new LoadGame(modelMVC);
        };
    }
}
