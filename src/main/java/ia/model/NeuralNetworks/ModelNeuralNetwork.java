package ia.model.NeuralNetworks;

import engine.Engine;
import engine.agent.Action;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;
import ia.model.Model;
import ia.model.NeuralNetworks.MLP.Hyperbolic;
import ia.model.NeuralNetworks.MLP.MLP;
import ia.perception.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModelNeuralNetwork extends Model {

    private NeuralNetwork neuralNetwork;

    public ModelNeuralNetwork(NeuralNetwork neuralNetwork, List<Perception> perceptions) {
        setPerceptions(perceptions);
        this.neuralNetwork = neuralNetwork;
    }

    public ModelNeuralNetwork() {
        setPerceptions(
                List.of(
                        new FlagCompass(myself,null, false),
                        new FlagCompass(myself,null, false),
                        new TerritoryCompass(myself, new Filter(Filter.TeamMode.ALLY, Filter.DistanceMode.NEAREST)),
                        new PerceptionRaycast(myself, new double[] {1.4, 1.4}, 2, 70),
                        new PerceptionRaycast(myself, 1.5, 8, 180)
                )
        );

        int[] layers = new int[] {getNumberOfInputsMLP(), 70, 40, 10, 2};
        neuralNetwork = new MLP(layers, new Hyperbolic());
    }

    @Override
    public Action getAction(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects) {
        Objects.requireNonNull(engine);
        Objects.requireNonNull(map);
        Objects.requireNonNull(agents);
        Objects.requireNonNull(objects);

        //Mettre à jour les perceptions
        for(Perception perception : perceptions) {
            perception.updatePerceptionValues(map, agents, objects);
        }

        //Récupération des perceptions (normalisées) à mettre dans les neurones d'entrées
        double[] inputs = getAllPerceptionsValuesNormalise();

        //Calcul du réseau
        double[] outputs = neuralNetwork.compute(inputs);

        return new Action(outputs[0], outputs[1]);
    }

    public double[] getAllPerceptionsValuesNormalise() {
        //Récupération de toutes les perceptions
        List<Double> perceptionsValuesNormalise = new ArrayList<>();
        for (Perception p : perceptions) {
            perceptionsValuesNormalise.addAll(p.getPerceptionsValuesNormalise());
        }
        //Conversion List<Double> en double[]
        double[] values = new double[perceptionsValuesNormalise.size()];
        for (int i = 0; i < perceptionsValuesNormalise.size(); i++) {
            values[i] = perceptionsValuesNormalise.get(i);
        }
        return values;
    }

    public int getNumberOfInputsMLP() {
        int count = 0;
        for (Perception p : perceptions) {
            count += p.getNumberOfPerceptionsValuesNormalise();
        }
        return count;
    }

    public NeuralNetwork getNeuralNetwork() {
        return this.neuralNetwork;
    }

    @Override
    public void setMyself(Agent a) {
        super.setMyself(a);
    }
}
