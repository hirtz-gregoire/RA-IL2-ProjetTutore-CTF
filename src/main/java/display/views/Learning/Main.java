package display.views.Learning;

import display.model.LearningModel;
import display.model.ModelMVC;
import display.views.View;
import ia.ecj.ECJTrainer;
import ia.ecj.statistics.CTF_CMAES_Statistics;
import ia.ecj.statistics.Stats;
import ia.model.NeuralNetworks.ModelNeuralNetwork;
import ia.model.NeuralNetworks.NNFileLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class Main extends View {

    //private final Engine engine;
    private Thread gameThread;

    public Main(ModelMVC modelMVC) throws IOException {
        super(modelMVC);

        this.pane = loadFxml("Learning/Main", this.modelMVC);

        LearningModel model = (LearningModel)this.modelMVC;

        //STATISTIQUES DE L'apprentissage
        //Ou se situe le pane du graphique des stats
        Stats stats = new Stats();

        CTF_CMAES_Statistics.addListener(stats);

        ECJTrainer ecj = new ECJTrainer();
        ecj.train(model);

        this.update();
    }

    @Override
    public void update() {
        super.update();

        LearningModel model = (LearningModel) modelMVC;
    }
}
