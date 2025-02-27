package display.controllers.Learning;

import display.controllers.Controller;
import display.model.LearningModel;
import display.model.ModelMVC;
import display.views.Learning.EnumLearning;
import display.views.ViewType;

public class MainController extends Controller {

    public void buttonExit(){
        LearningModel model = (LearningModel) this.model;

        ModelMVC.clearInstance(LearningModel.class);

        model.setEnumLearning(EnumLearning.ChoiceMap);
        model.getGlobalModel().setCurrentViewType(ViewType.MainMenu);

        model.update();
        model.getGlobalModel().updateRacine();
    }
}
