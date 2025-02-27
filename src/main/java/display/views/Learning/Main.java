package display.views.Learning;

import display.model.LearningModel;
import display.model.ModelMVC;
import display.views.View;
import ia.ecj.ECJTrainer;
import ia.ecj.statistics.*;
import javafx.scene.layout.StackPane;

import java.io.*;

public class Main extends View {

    //private final Engine engine;
    private Thread gameThread;

    public Main(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("Learning/Main", this.modelMVC);

        LearningModel model = (LearningModel)this.modelMVC;

        //Où se situe le pane du graphique des stats
        StackPane stackPaneGraphiqueFitness =  (StackPane) this.pane.lookup("#graphique1");
        StackPane stackPaneGraphiqueSigma =  (StackPane) this.pane.lookup("#graphique2");
        StackPane stackPaneGraphiqueConditionNumber =  (StackPane) this.pane.lookup("#graphique3");

        //STATISTIQUES DE L'APPRENTISSAGE
        StatisticsFitness statisticsFitness = new StatisticsFitness(stackPaneGraphiqueFitness);
        StatisticsSigma statisticsSigma = new StatisticsSigma(stackPaneGraphiqueSigma);
        StatisticsConditionNumber statisticsConditionNumber = new StatisticsConditionNumber(stackPaneGraphiqueConditionNumber);
        //Classe qui sauvegarde le modèle
        StatisticsSaveModel statisticsSaveModel = new StatisticsSaveModel();

        CTF_CMAES_Statistics.addListener(statisticsFitness);
        CTF_CMAES_Statistics.addListener(statisticsSigma);
        CTF_CMAES_Statistics.addListener(statisticsConditionNumber);
        CTF_CMAES_Statistics.addListener(statisticsSaveModel);

        ECJTrainer ecj = new ECJTrainer();
        ecj.train(model);

        this.update();
    }

    @Override
    public void update() {
        super.update();
    }
}
