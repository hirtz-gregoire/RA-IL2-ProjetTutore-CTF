package display.model;

import display.views.MainMenu;

import java.io.IOException;

public class MainMenuModel extends ModelMVC {

    public MainMenuModel(GlobalModel globalModel) throws IOException {
        super(globalModel);
        this.view = new MainMenu(this);
    }
}
