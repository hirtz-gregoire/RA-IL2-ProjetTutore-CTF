package display.controllers;

import display.model.MainMenuModel;
import display.views.ViewType;

import java.io.IOException;

public class MainMenuCtrl extends Controller {

    public void switchToSimu() throws IOException {
        System.out.println("MainMenuCtrl.switchToSimu");

        MainMenuModel model = (MainMenuModel) this.model;
        model.getGlobalModel().setCurrentViewType(ViewType.RunSimu);
        System.out.println(model.getGlobalModel().getCurrentViewType());

        model.getGlobalModel().updateRacine();
    }

}
