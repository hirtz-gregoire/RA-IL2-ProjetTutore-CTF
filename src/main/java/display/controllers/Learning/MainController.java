package display.controllers.Learning;

import display.controllers.Controller;
import display.model.LearningModel;
import display.model.ModelMVC;
import display.views.Learning.EnumLearning;
import display.views.ViewType;
import engine.Files;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.*;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class MainController extends Controller {

    @FXML
    private Label labelSauvegarde;
    @FXML
    private StackPane graphique;

    public void buttonExit(){
        LearningModel model = (LearningModel) this.model;

        ModelMVC.clearInstance(LearningModel.class);

        model.setEnumLearning(EnumLearning.ChoiceMap);
        model.getGlobalModel().setCurrentViewType(ViewType.MainMenu);

        model.update();
        model.getGlobalModel().updateRacine();
    }

    public void buttonGraphique() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(Files.getFileLearning()));

            // Création des séries
            final List<LineChart.Series> seriesList = new LinkedList<>();
            double minY = Double.MAX_VALUE;
            double maxY = -Double.MAX_VALUE;
            int numGeneration = 0;
            for (int n = 0 ; n < 1 ; n++) {
                final LineChart.Series series  = new LineChart.Series<>();
                series.setName(String.format("Fitness", n));

                String line;
                while ((line = reader.readLine()) != null) {
                    //Si on est à la ligne fitness
                    if (line.startsWith("Fitness")) {
                        double fitness = Double.parseDouble(line.replace("Fitness: ", ""));
                        if (fitness < minY) minY = fitness;
                        if (fitness > maxY) maxY = fitness;
                        final LineChart.Data data = new LineChart.Data(numGeneration, fitness);
                        series.getData().add(data);
                        numGeneration++;
                    }
                }
                reader.close();
                seriesList.add(series);
            }
            final NumberAxis xAxis = new NumberAxis(0, numGeneration-1, 1);
            xAxis.setLabel("Génération");
            final NumberAxis yAxis = new NumberAxis(minY, maxY, 1);
            yAxis.setLabel("Fitness");

            // Création du graphique.
            final LineChart chart = new LineChart(xAxis, yAxis);
            chart.setTitle("Evolution fitness en fonction des générations");
            chart.getData().setAll(seriesList);

            graphique.getChildren().clear();
            graphique.getChildren().add(chart);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void buttonSaveNN() {
        LearningModel model = (LearningModel) this.model;

        try {
            //Récupération des poids
            Scanner scanner = new Scanner(Files.getFileLearning());
            String line = null;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
            }
            scanner.close();
            String weights = line.replace(" ", ";");

            //Création du fichier ctf
            FileWriter writerCTF = new FileWriter("ressources/models/"+ model.getNameModel() +".ctf");
            if (model.isTerritoryCompass()) {
                writerCTF.write("ia.perception.TerritoryCompass;BLUE\n");
            }
            if (model.isNearestAllyFlagCompass()) {
                writerCTF.write("ia.perception.NearestAllyFlag;BLUE;false\n");
            }
            if (model.isNearestEnnemyFlagCompass()) {
                writerCTF.write("ia.perception.NearestEnnemyFlag;BLUE\n");
            }
            for (List<Integer> raycast : model.getRaycasts()) {
                writerCTF.write("ia.perception.PerceptionRaycast");
                for (int i = 0; i < raycast.size(); i++) {
                    writerCTF.write(";"+raycast.get(i));
                }
                writerCTF.write("\n");
            }
            writerCTF.write("\nressources/models/"+ model.getNameModel() +".mlp");
            writerCTF.close();

            //Création du fichier mlp
            FileWriter writerMLP = new FileWriter("ressources/models/"+ model.getNameModel() +".mlp");
            writerMLP.write(model.getTransferFunction().toString()+"\n");
            for (Integer nbNeuronsLayer : model.getLayersNeuralNetwork()) {
                writerMLP.write(nbNeuronsLayer+";");
            }
            writerMLP.write("\n");
            writerMLP.write(weights);
            writerMLP.close();

            labelSauvegarde.setText("Modèle sauvegardé !");

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
