package ia.ecj.statistics;

import display.model.GlobalModel;
import display.model.LearningModel;
import display.views.ViewType;
import ec.Individual;
import ec.vector.DoubleVectorIndividual;
import ia.model.NeuralNetworks.TransferFonctionEnum;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Stats3 implements CTF_CMAES_StatListener {
    private List<XYChart.Series<Number, Number>> seriesList = new java.util.ArrayList<>();
    private int numGeneration = 0;
    private double minY = Double.MAX_VALUE;
    private double maxY = -Double.MAX_VALUE;
    private NumberAxis xAxis = new NumberAxis();
    private NumberAxis yAxis = new NumberAxis();
    private LineChart<Number, Number> chart;

    public Stats3(StackPane stackPaneGraphique) {
        // Création des séries

        XYChart.Series<Number, Number> conditionNumberSerie = new XYChart.Series<>();
        conditionNumberSerie.setName("Condition Number");
        seriesList.add(conditionNumberSerie);

        // Configuration des axes
        xAxis.setLabel("Génération");
        yAxis.setLabel("Valeur");

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
            double sigma = stats[0].sigma();
            double conditionNumber = stats[0].conditionNumber();

            // Mise à jour des limites Y
            if (sigma > maxY) maxY = sigma;
            if (conditionNumber > maxY) maxY = conditionNumber;
            if (sigma < minY) minY = sigma;
            if (conditionNumber < minY) minY = conditionNumber;

            // Ajout des nouvelles données aux séries existantes
            seriesList.get(0).getData().add(new XYChart.Data<>(numGeneration, conditionNumber));

            // Mise à jour des axes sans recréer un graphique
            xAxis.setAutoRanging(false);
            xAxis.setLowerBound(1);
            xAxis.setUpperBound(numGeneration);
            xAxis.setTickUnit(1);

            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(minY);
            yAxis.setUpperBound(maxY);
            yAxis.setTickUnit((maxY - minY) / 10);
        });

        numGeneration++;

        // Sauvegarde du réseau toutes les 10 générations
        if (numGeneration % 10 == 0) {
            double[] weights = ((DoubleVectorIndividual) stats[0].bestOfGen()).genome;
            sauvegardeMLP(weights);
        }
    }

    @Override
    public void finalStatistics(Individual[] bestOfRun, Individual[] bestOfLastRun) {
        double[] weights = ((DoubleVectorIndividual) bestOfRun[0]).genome;
        sauvegardeMLP(weights);
        CTF_CMAES_Statistics.removeListener(this);
    }

    private void sauvegardeMLP(double[] weights) {
    }
}
