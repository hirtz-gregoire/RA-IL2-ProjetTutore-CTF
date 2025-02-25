package display.views.Learning;

import display.model.LearningModel;
import display.model.ModelMVC;
import display.views.View;
import ia.ecj.ECJTrainer;
import ia.ecj.statistics.CTF_CMAES_Statistics;
import ia.ecj.statistics.Stats;
import ia.model.NeuralNetworks.ModelNeuralNetwork;
import ia.model.NeuralNetworks.NNFileLoader;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class Main extends View {

    //private final Engine engine;
    private Thread gameThread;

    public Main(ModelMVC modelMVC) throws IOException {
        super(modelMVC);

        this.pane = loadFxml("Learning/Main", this.modelMVC);

        LearningModel model = (LearningModel)this.modelMVC;

        //STATISTIQUES DE L'APPRENTISSAGE
        //Où se situe le pane du graphique des stats
        StackPane stackPaneGraphique =  (StackPane) this.pane.lookup("#graphique");
        Stats stats = new Stats(stackPaneGraphique);
        CTF_CMAES_Statistics.addListener(stats);

        ECJTrainer ecj = new ECJTrainer();
        ecj.train(model);

        this.update();
    }

    @Override
    public void update() {
        super.update();
    }
}
