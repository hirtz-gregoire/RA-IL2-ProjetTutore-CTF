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
    private List<XYChart.Series<Number, Number>> seriesList = new java.util.ArrayList<>();
    private int numGeneration = 0;
    private double minY = Double.MAX_VALUE;
    private double maxY = -Double.MAX_VALUE;
    private NumberAxis xAxis = new NumberAxis();
    private NumberAxis yAxis = new NumberAxis();
    private LineChart<Number, Number> chart;

    public StatisticsConditionNumber(StackPane stackPaneGraphique, NumberAxis xAxis) {
        // Création des séries

        XYChart.Series<Number, Number> conditionNumberSerie = new XYChart.Series<>();
        conditionNumberSerie.setName("Condition Number");
        seriesList.add(conditionNumberSerie);

        // Configuration des axes
        yAxis.setLabel("Condition Number Valeur");

        // Création du graphique
        chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Évolution de Condition Number");
        chart.getData().addAll(seriesList);

        // Ajout du graphique à la StackPane
        stackPaneGraphique.getChildren().add(chart);
    }

    @Override
    public void postEvaluationStatistics(CTF_CMAES_Statistics.Stats[] stats) {
        Platform.runLater(() -> {
            double conditionNumber = stats[0].conditionNumber();

            // Mise à jour des limites Y
            if (conditionNumber > maxY) maxY = conditionNumber;
            if (conditionNumber < minY) minY = conditionNumber;

            // Ajout des nouvelles données aux séries existantes
            seriesList.get(0).getData().add(new XYChart.Data<>(numGeneration, conditionNumber));

            // Mise à jour des axes sans recréer un graphique
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(minY);
            yAxis.setUpperBound(maxY);
            yAxis.setTickUnit((maxY - minY) / 10);
        });

        numGeneration++;
    }

    @Override
    public void finalStatistics(Individual[] bestOfRun, Individual[] bestOfLastRun) {
        CTF_CMAES_Statistics.removeListener(this);
    }
}
