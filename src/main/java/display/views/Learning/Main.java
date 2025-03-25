package display.views.Learning;

import display.model.LearningModel;
import display.model.ModelMVC;
import display.views.View;
import ia.ecj.ECJTrainer;
import ia.ecj.statistics.*;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.RangeSlider;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends View {
    public Main(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("Learning/Main", this.modelMVC);

        LearningModel model = (LearningModel)this.modelMVC;

        //Gestion des 3 graphiques

        //FITNESS

        StackPane stackPaneGraphiqueFitness =  (StackPane) this.pane.lookup("#graphiqueFitness");

        RangeSlider rangeSliderFitness = (RangeSlider) this.pane.lookup("#rangeSliderFitness"); rangeSliderFitness.setMax(model.getNumberOfGenerations());

        NumberAxis xAxisFitness = new NumberAxis(); xAxisFitness.setLabel("Génération"); xAxisFitness.setAutoRanging(false);
        model.setXAxisFitness(xAxisFitness);
        NumberAxis yAxisFitness = new NumberAxis(); yAxisFitness.setLabel("Fitness"); yAxisFitness.setAutoRanging(false); yAxisFitness.setLowerBound(0); yAxisFitness.setUpperBound(0);
        model.setYAxisFitness(yAxisFitness);

        XYChart.Series<Number, Number> bestFitnessSerie = new XYChart.Series<>(); bestFitnessSerie.setName("Best Fitness");
        model.setBestFitnessSerie(bestFitnessSerie);
        XYChart.Series<Number, Number> worstFitnessSerie = new XYChart.Series<>(); worstFitnessSerie.setName("Worst Fitness");
        model.setWorstFitnessSerie(worstFitnessSerie);
        XYChart.Series<Number, Number> averageFitnessSerie = new XYChart.Series<>(); averageFitnessSerie.setName("Average Fitness");
        model.setAverageFitnessSerie(averageFitnessSerie);

        LineChart<Number, Number> chartFitness = new LineChart<>(xAxisFitness, yAxisFitness); chartFitness.setTitle("Évolution de Fitness"); chartFitness.getData().addAll(bestFitnessSerie, worstFitnessSerie, averageFitnessSerie);

        stackPaneGraphiqueFitness.getChildren().add(chartFitness);

        StatisticsFitness statisticsFitness = new StatisticsFitness(bestFitnessSerie, worstFitnessSerie, averageFitnessSerie, xAxisFitness, yAxisFitness);

        CTF_CMAES_Statistics.addListener(statisticsFitness);

        //SIGMA

        StackPane stackPaneGraphiqueSigma =  (StackPane) this.pane.lookup("#graphiqueSigma");

        RangeSlider rangeSliderSigma = (RangeSlider) this.pane.lookup("#rangeSliderSigma"); rangeSliderSigma.setMax(model.getNumberOfGenerations()); rangeSliderSigma.setHighValue(100);

        NumberAxis xAxisSigma = new NumberAxis(); xAxisSigma.setLabel("Génération"); xAxisSigma.setAutoRanging(false);
        model.setXAxisSigma(xAxisSigma);
        NumberAxis yAxisSigma = new NumberAxis(); yAxisSigma.setLabel("Sigma"); yAxisSigma.setAutoRanging(false); yAxisSigma.setLowerBound(1); yAxisSigma.setUpperBound(1);
        model.setYAxisSigma(yAxisSigma);

        XYChart.Series<Number, Number> sigmaSerie = new XYChart.Series<>(); sigmaSerie.setName("Sigma");
        model.setSigmaSerie(sigmaSerie);

        LineChart<Number, Number> chartSigma = new LineChart<>(xAxisSigma, yAxisSigma); chartSigma.setTitle("Évolution de Sigma"); chartSigma.getData().add(sigmaSerie);

        stackPaneGraphiqueSigma.getChildren().add(chartSigma);

        StatisticsSigma statisticsSigma = new StatisticsSigma(sigmaSerie, xAxisSigma, yAxisSigma);

        CTF_CMAES_Statistics.addListener(statisticsSigma);

        //CONDITION NUMBER

        StackPane stackPaneGraphiqueConditionNumber =  (StackPane) this.pane.lookup("#graphiqueConditionNumber");

        RangeSlider rangeSliderConditionNumber = (RangeSlider) this.pane.lookup("#rangeSliderConditionNumber"); rangeSliderConditionNumber.setMax(model.getNumberOfGenerations()); rangeSliderConditionNumber.setHighValue(100);

        NumberAxis xAxisConditionNumber = new NumberAxis(); xAxisConditionNumber.setLabel("Génération"); xAxisConditionNumber.setAutoRanging(false);
        model.setXAxisConditionNumber(xAxisConditionNumber);
        NumberAxis yAxisConditionNumber = new NumberAxis(); yAxisConditionNumber.setLabel("Condition Number Valeur"); yAxisConditionNumber.setAutoRanging(false); yAxisConditionNumber.setLowerBound(1); yAxisConditionNumber.setUpperBound(1);
        model.setYAxisConditionNumber(yAxisConditionNumber);

        XYChart.Series<Number, Number> conditionNumberSerie = new XYChart.Series<>(); conditionNumberSerie.setName("Condition Number");
        model.setConditionNumberSerie(conditionNumberSerie);

        LineChart<Number, Number> chartConditionNumber = new LineChart<>(xAxisConditionNumber, yAxisConditionNumber);  chartConditionNumber.setTitle("Évolution de Condition Number"); chartConditionNumber.getData().add(conditionNumberSerie);

        stackPaneGraphiqueConditionNumber.getChildren().add(chartConditionNumber);

        StatisticsConditionNumber statisticsConditionNumber = new StatisticsConditionNumber(conditionNumberSerie, xAxisConditionNumber, yAxisConditionNumber);

        CTF_CMAES_Statistics.addListener(statisticsConditionNumber);

        //Classe qui sauvegarde le modèle
        StatisticsSaveModel statisticsSaveModel = new StatisticsSaveModel();
        CTF_CMAES_Statistics.addListener(statisticsSaveModel);

        ECJTrainer ecj = new ECJTrainer();
        ecj.train(model);

        this.update();
    }

    @Override
    public void update() {
        super.update();
    }
}
