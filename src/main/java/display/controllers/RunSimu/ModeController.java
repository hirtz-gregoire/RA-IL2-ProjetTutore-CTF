package display.controllers.RunSimu;

import display.controllers.Controller;
import display.model.RunSimuModel;
import display.views.RunSimu.EnumRunSimu;

public class ModeController extends Controller {

    public void newGame(){
        RunSimuModel model = (RunSimuModel) this.model;
        model.setEnumRunSimu(EnumRunSimu.ChoiceMap);

        model.update();
        model.getGlobalModel().updateRacine();
    }

    public void loadGame(){
        RunSimuModel model = (RunSimuModel) this.model;
        model.setEnumRunSimu(EnumRunSimu.LoadGame);

        model.update();
        model.getGlobalModel().updateRacine();
    }

}
