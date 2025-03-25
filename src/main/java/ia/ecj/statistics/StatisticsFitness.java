package ia.ecj.statistics;

import ec.Individual;
import javafx.application.Platform;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class StatisticsFitness implements CTF_CMAES_StatListener {
    private XYChart.Series<Number, Number> bestSerie;
    private XYChart.Series<Number, Number> worstSerie;
    private XYChart.Series<Number, Number> averageSerie;
    NumberAxis xAxis;
    NumberAxis yAxis;
    private int numGeneration = 0;

    public StatisticsFitness(XYChart.Series<Number, Number> bestSerie, XYChart.Series<Number, Number> worstSerie, XYChart.Series<Number, Number> averageSerie, NumberAxis xAxis, NumberAxis yAxis) {
        this.bestSerie = bestSerie;
        this.worstSerie = worstSerie;
        this.averageSerie = averageSerie;
        this.xAxis = xAxis;
        this.yAxis = yAxis;
    }

    @Override
    public void postEvaluationStatistics(CTF_CMAES_Statistics.Stats[] stats) {
        //Mise à jour du graphique
        Platform.runLater(() -> {
            double bestFitness = stats[0].bestOfGen().fitness.fitness();
            double worstFitness = stats[0].worstOfGen().fitness.fitness();
            double averageFitness = stats[0].averageGenFit();

            bestSerie.getData().add(new XYChart.Data<>(numGeneration, bestFitness));
            worstSerie.getData().add(new XYChart.Data<>(numGeneration, worstFitness));
            averageSerie.getData().add(new XYChart.Data<>(numGeneration, averageFitness));

            if (xAxis.getUpperBound() > numGeneration) {
                if (bestFitness > yAxis.getUpperBound()) yAxis.setUpperBound(bestFitness);
                if (worstFitness < yAxis.getLowerBound()) yAxis.setLowerBound(worstFitness);
            }
        });

        numGeneration++;
    }

    @Override
    public void finalStatistics(Individual[] bestOfRun, Individual[] bestOfLastRun) {
        CTF_CMAES_Statistics.removeListener(this);
    }
}
