package display.views.Learning;

import display.model.LearningModel;
import display.model.ModelMVC;
import display.views.View;
import ia.ecj.ECJTrainer;
import ia.ecj.statistics.*;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.RangeSlider;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends View {
    public Main(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("Learning/Main", this.modelMVC);

        LearningModel model = (LearningModel)this.modelMVC;

        //Où se situe le pane du graphique des stats
        StackPane stackPaneGraphiqueFitness =  (StackPane) this.pane.lookup("#graphique1");
        StackPane stackPaneGraphiqueSigma =  (StackPane) this.pane.lookup("#graphique2");
        StackPane stackPaneGraphiqueConditionNumber =  (StackPane) this.pane.lookup("#graphique3");

        //RangeSlider pour contrôler l'affichage
        RangeSlider rangeSlider = (RangeSlider) this.pane.lookup("#rangeSlider");
        rangeSlider.setMin(1);
        rangeSlider.setMax(model.getNumberOfGenerations());
        rangeSlider.adjustLowValue(1);
        rangeSlider.adjustHighValue((double) model.getNumberOfGenerations() /10);

        //Un Axis X par graphique
        NumberAxis xAxis1 = new NumberAxis(); NumberAxis xAxis2 = new NumberAxis(); NumberAxis xAxis3 = new NumberAxis();
        xAxis1.setLabel("Génération"); xAxis2.setLabel("Sigma"); xAxis3.setLabel("Condition Number");
        xAxis1.setAutoRanging(false); xAxis2.setAutoRanging(false); xAxis3.setAutoRanging(false);
        List<NumberAxis> listXAxis = new ArrayList<>();
        listXAxis.add(xAxis1); listXAxis.add(xAxis2); listXAxis.add(xAxis3);
        model.setListXAxis(listXAxis);

        //STATISTIQUES DE L'APPRENTISSAGE
        StatisticsFitness statisticsFitness = new StatisticsFitness(stackPaneGraphiqueFitness, xAxis1);
        StatisticsSigma statisticsSigma = new StatisticsSigma(stackPaneGraphiqueSigma, xAxis2);
        StatisticsConditionNumber statisticsConditionNumber = new StatisticsConditionNumber(stackPaneGraphiqueConditionNumber, xAxis3);
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
