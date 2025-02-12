package display.views.Learning;

import display.model.LearningModel;
import display.model.ModelMVC;
import display.views.View;
import ia.ecj.ECJTrainer;
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

//        try {
//            ModelNeuralNetwork modelNeuralNetwork = NNFileLoader.loadModel("ressources/models/test.ctf");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        Pane pane = (Pane)this.pane.lookup("#root");

        ECJTrainer ecj = new ECJTrainer();
        ecj.train(model);

        this.update();
    }

    @Override
    public void update() {
        super.update();

        LearningModel model = (LearningModel) modelMVC;

        // maj du tps cible selon valeur de model.saveTps
        Label tps = (Label)this.pane.lookup("#tps");
        tps.setText(String.valueOf(model.getSaveTps()));
    }
}
