package ia.ecj;

import ec.*;
import ec.eda.cmaes.CMAESInitializer;
import ec.util.Parameter;
import ec.vector.*;
import ia.model.NeuralNetworks.ModelNeuralNetwork;
import ia.model.NeuralNetworks.NNFileLoader;

import java.io.*;

public class MLPInitializer extends CMAESInitializer {
    private static final String P_GENOME_FILE = "init.genome-file";

    @Override
    public Population initialPopulation(EvolutionState state, int thread) {
        Population pop = super.initialPopulation(state, thread);

        // Récupérer le nom du fichier depuis le fichier .params
        Parameter genomeFileParam = new Parameter(P_GENOME_FILE);
        String genomeFileName = state.parameters.getString(genomeFileParam, null);

        if (genomeFileName != null && !genomeFileName.trim().isEmpty()) {
            File genomeFile = new File(genomeFileName);
            if (genomeFile.exists()) {
                state.output.message("Loading initial genome from " + genomeFileName);
                ModelNeuralNetwork mlp = null;
                try{
                    mlp = NNFileLoader.loadModel(genomeFileName);
                }catch (Exception e){
                    System.out.println("Error loading initial genome from " + genomeFileName);
                }

                assert mlp != null;
                double[] genome = mlp.getNeuralNetwork().getWeights();

                if (genome != null) {
                    // Appliquer le génome au premier individu
                    DoubleVectorIndividual firstInd = (DoubleVectorIndividual) pop.subpops.get(0).individuals.get(0);
                    firstInd.genome = genome;
                    firstInd.evaluated = false;
                }
            } else {
                state.output.warning("Genome file not found: " + genomeFileName + ", using default initialization.");
            }
        } else {
            state.output.message("No genome file specified, using default initialization.");
        }

        return pop;
    }
}
