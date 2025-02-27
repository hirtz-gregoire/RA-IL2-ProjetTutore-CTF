package ia.ecj.statistics;

import display.model.GlobalModel;
import display.model.LearningModel;
import display.views.ViewType;
import ec.Individual;
import ec.vector.DoubleVectorIndividual;
import engine.Files;
import ia.model.NeuralNetworks.TransferFonctionEnum;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Stats implements CTF_CMAES_StatListener {
    private List<LineChart.Series> seriesList = new ArrayList<>();
    private int numGeneration = 0;
    private double minY = Double.MAX_VALUE;
    private double maxY = -Double.MAX_VALUE;
    private NumberAxis xAxis = new NumberAxis();
    private NumberAxis yAxis = new NumberAxis();
    private LineChart chart;

    public Stats(StackPane stackPaneGraphique) {

        // Création des séries
        XYChart.Series<Number, Number> bestFitnessSerie = new XYChart.Series<>();
        bestFitnessSerie.setName("Best Fitness");
        seriesList.add(bestFitnessSerie);

        XYChart.Series<Number, Number> worstFitnessSerie = new XYChart.Series<>();
        worstFitnessSerie.setName("Worst Fitness");
        seriesList.add(worstFitnessSerie);

        XYChart.Series<Number, Number> averageFitnessSerie = new XYChart.Series<>();
        averageFitnessSerie.setName("Average Fitness");
        seriesList.add(averageFitnessSerie);

        // Configuration des axes
        xAxis.setLabel("Génération");
        yAxis.setLabel("Fitness");

        // Création du graphique
        chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Évolution de la fitness");
        chart.getData().addAll(seriesList);

        // Ajout du graphique à la StackPane
        stackPaneGraphique.getChildren().add(chart);

        try {
            LearningModel model = (LearningModel) ViewType.getViewInstance(ViewType.Learning, GlobalModel.getInstance());
            //Création du fichier ctf
            FileWriter writerCTF = new FileWriter("ressources/models/" + model.getNameModel() + ".ctf");
            //PERCEPTIONS
            if (model.isTerritoryCompass()) {
                writerCTF.write("ia.perception.TerritoryCompass;ALLY;NEAREST\n");
            }
            if (model.isNearestAllyFlagCompass()) {
                writerCTF.write("ia.perception.FlagCompass;ALLY;NEAREST;false\n");
            }
            if (model.isNearestEnnemyFlagCompass()) {
                writerCTF.write("ia.perception.FlagCompass;ENEMY;NEAREST;false\n");
            }
            for (List<Integer> raycast : model.getRaycasts()) {
                writerCTF.write("ia.perception.PerceptionRaycast");
                for (int i = 0; i < raycast.size(); i++) {
                    writerCTF.write(";" + raycast.get(i));
                }
                writerCTF.write("\n");
            }
            writerCTF.write("\nressources/models/" + model.getNameModel() + ".mlp");
            writerCTF.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void postEvaluationStatistics(CTF_CMAES_Statistics.Stats[] stats) {
        //Mise à jour du graphique
        Platform.runLater(() -> {
            double bestFitness = stats[0].bestOfGen().fitness.fitness();
            double worstFitness = stats[0].worstOfGen().fitness.fitness();
            double averageFitness = stats[0].averageGenFit();

            // Mise à jour des limites Y
            if (bestFitness > maxY) maxY = bestFitness;
            if (worstFitness < minY) minY = worstFitness;

            // Ajout des nouvelles données aux séries existantes
            seriesList.get(0).getData().add(new XYChart.Data<>(numGeneration, bestFitness));  // Best Fitness
            seriesList.get(1).getData().add(new XYChart.Data<>(numGeneration, worstFitness)); // Worst Fitness
            seriesList.get(2).getData().add(new XYChart.Data<>(numGeneration, averageFitness)); // Average Fitness

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

        //Sauvegarde du réseau toutes les 10 générations
        if (numGeneration % 10 == 0) {
            double[] weights = ((DoubleVectorIndividual) stats[0].bestOfGen()).genome;
            sauvegardeMLP(weights);
        }
    }

    @Override
    public void finalStatistics(Individual[] bestOfRun, Individual[] bestOfLastRun) {
        //Sauvegarde finale du modèle
        double[] weights = ((DoubleVectorIndividual) bestOfRun[0]).genome;
        sauvegardeMLP(weights);
        CTF_CMAES_Statistics.removeListener(this);
    }

    private void sauvegardeMLP(double[] weights) {
        String weightsString = Arrays.stream(weights)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(";"));
        try {
            LearningModel model = (LearningModel) ViewType.getViewInstance(ViewType.Learning, GlobalModel.getInstance());
            //Création du fichier mlp
            FileWriter writerMLP = new FileWriter("ressources/models/" + model.getNameModel() + ".mlp");
            writerMLP.write( TransferFonctionEnum.getTransferFonctionString(model.getTransferFunction()) + "\n");
            for (Integer nbNeuronsLayer : model.getLayersNeuralNetwork()) {
                writerMLP.write(nbNeuronsLayer + ";");
            }
            writerMLP.write("\n");
            writerMLP.write(weightsString);
            writerMLP.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
