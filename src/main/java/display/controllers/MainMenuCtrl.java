package display.controllers;

import display.model.MainMenuModel;
import display.views.View;
import display.views.ViewType;

import java.io.IOException;

public class MainMenuCtrl extends Controller {

    public void switchToSimu() {

        MainMenuModel model = (MainMenuModel) this.model;
        model.getGlobalModel().setCurrentViewType(ViewType.RunSimu);

        model.getGlobalModel().updateRacine();
    }

    public void switchToMapEditor() {
        MainMenuModel model = (MainMenuModel) this.model;
        model.getGlobalModel().setCurrentViewType(ViewType.MapEditor);
        model.getGlobalModel().updateRacine();
    }

    public void switchToLearning() {
        MainMenuModel model = (MainMenuModel) this.model;
        model.getGlobalModel().setCurrentViewType(ViewType.Learning);
        model.getGlobalModel().updateRacine();
    }

}
