package display.controllers.RunSimu;

import display.controllers.Controller;
import display.model.RunSimuModel;
import display.views.RunSimu.EnumRunSimu;

public class ChoiceHumanController extends Controller {

    public void nextMenu() {
        RunSimuModel model = (RunSimuModel) this.model;
        model.setEnumRunSimu(EnumRunSimu.Main);

        model.update();
        model.getGlobalModel().updateRacine();
    }
}
