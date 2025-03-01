package display.controllers.Learning;

import display.controllers.Controller;
import display.model.LearningModel;
import display.model.ModelMVC;
import display.views.Learning.EnumLearning;
import display.views.ViewType;
import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import org.controlsfx.control.RangeSlider;

import java.util.List;

public class MainController extends Controller {

    @FXML
    private RangeSlider rangeSlider;

    public void adjustXAxis() {
        LearningModel model = (LearningModel) this.model;
        List<NumberAxis> listXAxis = model.getListXAxis();
        for (NumberAxis axis : listXAxis) {
            axis.setLowerBound((int) rangeSlider.getLowValue());
            axis.setUpperBound((int) rangeSlider.getHighValue());
        }
    }

    public void buttonExit(){
        LearningModel model = (LearningModel) this.model;

        ModelMVC.clearInstance(LearningModel.class);

        model.setEnumLearning(EnumLearning.ChoiceMap);
        model.getGlobalModel().setCurrentViewType(ViewType.MainMenu);

        model.update();
        model.getGlobalModel().updateRacine();
    }
}
