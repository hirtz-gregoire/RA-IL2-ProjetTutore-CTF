package display.controllers.Learning;

import display.controllers.Controller;
import display.model.LearningModel;
import display.model.ModelMVC;
import display.views.Learning.EnumLearning;
import display.views.ViewType;
import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.controlsfx.control.RangeSlider;

import java.util.List;

public class MainController extends Controller {

    @FXML
    private RangeSlider rangeSliderFitness;
    @FXML
    private RangeSlider rangeSliderSigma;
    @FXML
    private RangeSlider rangeSliderConditionNumber;

    public void adjustXAxisFitness() {
        LearningModel model = (LearningModel) this.model;

        NumberAxis xAxisFitness = model.getXAxisFitness();
        int minGeneration = (int) rangeSliderFitness.getLowValue();
        int maxGeneration = (int) rangeSliderFitness.getHighValue();
        xAxisFitness.setLowerBound(minGeneration);
        xAxisFitness.setUpperBound(maxGeneration);
        XYChart.Series<Number, Number> bestFitnessSerie = model.getBestFitnessSerie();
        XYChart.Series<Number, Number> worstFitnessSerie = model.getWorstFitnessSerie();
        XYChart.Series<Number, Number> averageFitnessSerie = model.getAverageFitnessSerie();
        //Recherche du min et max
        double max = Double.MIN_VALUE;
        for (XYChart.Data<Number, Number> data : bestFitnessSerie.getData().subList(minGeneration, Math.min(bestFitnessSerie.getData().size()-1, maxGeneration))) {
            if (data.getYValue().doubleValue() > max) max = data.getYValue().doubleValue();
        }
        double min = Double.MAX_VALUE;
        for (XYChart.Data<Number, Number> data : worstFitnessSerie.getData().subList(minGeneration, Math.min(worstFitnessSerie.getData().size()-1, maxGeneration))) {
            if (data.getYValue().doubleValue() < min) min = data.getYValue().doubleValue();
        }
        NumberAxis yAxisFitness = model.getYAxisFitness();
        yAxisFitness.setLowerBound(min);
        yAxisFitness.setUpperBound(max);
        yAxisFitness.setTickUnit((max - min) / 10);
    }

    public void adjustXAxisSigma() {
        LearningModel model = (LearningModel) this.model;

        NumberAxis xAxisSigma = model.getXAxisSigma();
        int minGeneration = (int) rangeSliderSigma.getLowValue();
        int maxGeneration = (int) rangeSliderSigma.getHighValue();
        xAxisSigma.setLowerBound(minGeneration);
        xAxisSigma.setUpperBound(maxGeneration);
        XYChart.Series<Number, Number> sigmaSerie = model.getSigmaSerie();
        //Recherche du min et max
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (XYChart.Data<Number, Number> data : sigmaSerie.getData().subList(minGeneration, Math.min(sigmaSerie.getData().size()-1, maxGeneration))) {
            if (data.getYValue().doubleValue() > max) max = data.getYValue().doubleValue();
            if (data.getYValue().doubleValue() < min) min = data.getYValue().doubleValue();
        }
        NumberAxis yAxisSigma = model.getYAxisSigma();
        yAxisSigma.setLowerBound(min);
        yAxisSigma.setUpperBound(max);
        yAxisSigma.setTickUnit((max - min) / 10);
    }

    public void adjustXAxisConditionNumber() {
        LearningModel model = (LearningModel) this.model;

        NumberAxis xAxisConditionNumber = model.getXAxisConditionNumber();
        int minGeneration = (int) rangeSliderConditionNumber.getLowValue();
        int maxGeneration = (int) rangeSliderConditionNumber.getHighValue();
        xAxisConditionNumber.setLowerBound(minGeneration);
        xAxisConditionNumber.setUpperBound(maxGeneration);
        XYChart.Series<Number, Number> conditionNumberSerie = model.getConditionNumberSerie();
        //Recherche du min et max
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (XYChart.Data<Number, Number> data : conditionNumberSerie.getData().subList(minGeneration, Math.min(conditionNumberSerie.getData().size()-1, maxGeneration))) {
            if (data.getYValue().doubleValue() > max) max = data.getYValue().doubleValue();
            if (data.getYValue().doubleValue() < min) min = data.getYValue().doubleValue();
        }
        NumberAxis yAxisConditionNumber = model.getYAxisConditionNumber();
        yAxisConditionNumber.setLowerBound(min);
        yAxisConditionNumber.setUpperBound(max);
        yAxisConditionNumber.setTickUnit((max - min) / 10);
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
