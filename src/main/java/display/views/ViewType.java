package display.views;

import display.model.ModelMVC;
import javafx.fxml.FXMLLoader;

public enum ViewType {
    MainMenu,
    Test;

    public static ViewMVC createView(ModelMVC model, ViewType type) {
        switch (type) {
            case MainMenu:
                return new MainMenuView(MainMenu, model);
            default:
                throw new IllegalArgumentException("Unknown view type: " + type);
        }
    }

    public static FXMLLoader loadFxml(ViewType viewType) {
        return new FXMLLoader(ViewType.class.getResource("/display/views/fxml/" +viewType.name()+".fxml"));
    }
}
