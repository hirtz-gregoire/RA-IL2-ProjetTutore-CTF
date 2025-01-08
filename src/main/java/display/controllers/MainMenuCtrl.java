package display.controllers;

import display.model.MainMenuModel;
import display.views.ViewType;

import java.io.IOException;

public class MainMenuCtrl extends Controller {

    public void switchToSimu() throws IOException {

        MainMenuModel model = (MainMenuModel) this.model;
        model.getGlobalModel().setCurrentViewType(ViewType.RunSimu);

        model.getGlobalModel().updateRacine();
    }

}
