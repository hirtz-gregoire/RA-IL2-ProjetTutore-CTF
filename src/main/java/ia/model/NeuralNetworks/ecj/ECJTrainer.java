package ia.model.NeuralNetworks.ecj;

import display.model.LearningModel;
import ec.Evolve;

import java.util.ArrayList;
import java.util.List;

public class ECJTrainer {
    public void train(LearningModel model){
        List<String> args = new ArrayList<>();
        args.addAll(List.of( "-file", "ressources/params/params.params" ));

        int nbWeight = 0;
        List<Integer> layersNeuralNetwork = model.getLayersNeuralNetwork();
        for (int numLayer = 1; numLayer < layersNeuralNetwork.size(); numLayer++) {
            nbWeight += layersNeuralNetwork.get(numLayer) * (layersNeuralNetwork.get(numLayer - 1) + 1);
        }

        args.addAll(List.of(
                "-pop.subpop.0.species.genome-size", String.valueOf(nbWeight),
                "-selectedMap",model.getMap().getMapPath()
        ));

        Evolve.main(args.toArray(new String[0]));
    }
}
