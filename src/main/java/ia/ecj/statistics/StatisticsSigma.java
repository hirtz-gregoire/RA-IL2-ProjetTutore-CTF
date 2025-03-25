package ia.ecj.statistics;

import ec.Individual;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class StatisticsSigma implements CTF_CMAES_StatListener {
    private XYChart.Series<Number, Number> serie;
    NumberAxis xAxis;
    NumberAxis yAxis;
    private int numGeneration = 0;

    public StatisticsSigma(XYChart.Series<Number, Number> serie, NumberAxis xAxis, NumberAxis yAxis) {
        this.serie = serie;
        this.xAxis = xAxis;
        this.yAxis = yAxis;
    }

    @Override
    public void postEvaluationStatistics(CTF_CMAES_Statistics.Stats[] stats) {
        Platform.runLater(() -> {
            double sigma = stats[0].sigma();
            serie.getData().add(new XYChart.Data<>(numGeneration, sigma));
            if (xAxis.getUpperBound() > numGeneration) {
                if (sigma > yAxis.getUpperBound()) yAxis.setUpperBound(sigma);
                if (sigma < yAxis.getLowerBound()) yAxis.setLowerBound(sigma);
            }
        });
        numGeneration++;
    }

    @Override
    public void finalStatistics(Individual[] bestOfRun, Individual[] bestOfLastRun) {
        CTF_CMAES_Statistics.removeListener(this);
    }
}
