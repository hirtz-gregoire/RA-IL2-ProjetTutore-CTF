package ia.ecj.statistics;

import display.SongPlayer;
import display.model.GlobalModel;
import display.model.LearningModel;
import display.views.ViewType;
import ec.Individual;
import ec.vector.DoubleVectorIndividual;
import ia.model.NeuralNetworks.TransferFonctionEnum;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StatisticsSaveModel implements CTF_CMAES_StatListener {
    private int numGeneration = 0;

    public StatisticsSaveModel() {
        //Sauvegarde du fichier CTF
        try {
            LearningModel model = (LearningModel) ViewType.getViewInstance(ViewType.Learning, GlobalModel.getInstance());
            //Création du fichier ctf
            FileWriter writerCTF = new FileWriter("ressources/models/" + model.getNameModel() + ".ctf");
            //PERCEPTIONS
            if (model.isNearestAllyFlagCompass()) {
                writerCTF.write("ia.perception.FlagCompass;ALLY;NEAREST;false\n");
            }
            if (model.isNearestEnnemyFlagCompass()) {
                writerCTF.write("ia.perception.FlagCompass;ENEMY;NEAREST;false\n");
            }
            if (model.isTerritoryCompass()) {
                writerCTF.write("ia.perception.TerritoryCompass;ALLY;NEAREST\n");
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
        numGeneration++;

        //Sauvegarde du réseau toutes les 10 générations
        if (numGeneration % 10 == 0) {
            SongPlayer.playSong("enter_zelda");
            double[] weights = ((DoubleVectorIndividual) stats[0].bestOfGen()).genome;
            sauvegardeMLP(weights);
        }
    }

    @Override
    public void finalStatistics(Individual[] bestOfRun, Individual[] bestOfLastRun) {
        SongPlayer.playSong("fin_apprentissage");

        //Sauvegarde finale du modèle
        double[] weights = ((DoubleVectorIndividual) bestOfRun[0]).genome;
        sauvegardeMLP(weights);
        CTF_CMAES_Statistics.removeListener(this);
    }

    private void sauvegardeMLP(double[] weights) {
        //Sauvegarde du fichier MLP avec les poids
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
