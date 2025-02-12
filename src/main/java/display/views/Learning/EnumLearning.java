package display.views.Learning;

import display.model.ModelMVC;
import display.views.View;

import java.io.IOException;

public enum EnumLearning {
    Main,
    ChoiceParameters,
    ChoiceMap;

    public static View getLearningEnum(EnumLearning anEnumLearning, ModelMVC modelMVC) throws IOException {
        return switch (anEnumLearning) {
            case Main -> new Main(modelMVC);
            case ChoiceParameters -> new ChoiceParameters(modelMVC);
            case ChoiceMap -> new ChoiceMap(modelMVC);
        };
    }
}
