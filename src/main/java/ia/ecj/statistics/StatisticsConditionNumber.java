package ia.ecj.statistics;

import ec.Individual;
import ec.vector.DoubleVectorIndividual;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;

import java.util.List;

public class StatisticsConditionNumber implements CTF_CMAES_StatListener {
    private XYChart.Series<Number, Number> serie;
    NumberAxis xAxis;
    NumberAxis yAxis;
    private int numGeneration = 0;

    public StatisticsConditionNumber(XYChart.Series<Number, Number> serie, NumberAxis xAxis, NumberAxis yAxis) {
        this.serie = serie;
        this.xAxis = xAxis;
        this.yAxis = yAxis;
    }

    @Override
    public void postEvaluationStatistics(CTF_CMAES_Statistics.Stats[] stats) {
        Platform.runLater(() -> {
            double conditionNumber = stats[0].conditionNumber();
            serie.getData().add(new XYChart.Data<>(numGeneration, conditionNumber));
            if (xAxis.getUpperBound() > numGeneration) {
                if (conditionNumber > yAxis.getUpperBound()) yAxis.setUpperBound(conditionNumber);
                if (conditionNumber < yAxis.getLowerBound()) yAxis.setLowerBound(conditionNumber);
            }
        });

        numGeneration++;
    }

    @Override
    public void finalStatistics(Individual[] bestOfRun, Individual[] bestOfLastRun) {
        CTF_CMAES_Statistics.removeListener(this);
    }
}
