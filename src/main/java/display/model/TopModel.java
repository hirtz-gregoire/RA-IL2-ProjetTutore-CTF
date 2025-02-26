package display.model;

import display.views.MainMenu;
import display.views.Top;

import java.io.IOException;

public class TopModel extends ModelMVC {

    public TopModel(GlobalModel globalModel) throws IOException {
        super(globalModel);
        this.view = new Top(this);
    }
}
