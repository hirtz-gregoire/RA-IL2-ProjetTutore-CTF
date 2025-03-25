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

import java.lang.reflect.Array;
import java.util.*;

public class ModelNeuralNetwork extends Model {
    protected double[] outputs;

    protected NeuralNetwork neuralNetwork;
    protected int numberOfInputs;

    public ModelNeuralNetwork(NeuralNetwork neuralNetwork, List<Perception> perceptions) {
        setPerceptions(perceptions);
        this.neuralNetwork = neuralNetwork;
        numberOfInputs = getNumberOfInputsMLP();
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
        numberOfInputs = getNumberOfInputsMLP();

        int[] layers = new int[] {numberOfInputs, 70, 40, 10, 2};
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
        outputs = neuralNetwork.compute(inputs);

        Random rand = new Random();

        double incertain = 0.3;
        outputs[0] = rand.nextDouble() > 0.5 ? outputs[0]+ incertain : outputs[0]- incertain;
        outputs[1] = rand.nextDouble() > 0.5 ? outputs[1]+ incertain : outputs[1]- incertain;
        outputs[0] = Math.clamp(outputs[0], -1, 1);
        outputs[1] = Math.clamp(outputs[1], -1, 1);

        return new Action(outputs[0], outputs[1]);
    }

    public double[] getAllPerceptionsValuesNormalise() {
        double[] values = new double[numberOfInputs];
        int index = 0;

        for (Perception p : perceptions) {
            double[] perceptionValues = p.getPerceptionsValuesNormalise();
            System.arraycopy(perceptionValues, 0, values, index, perceptionValues.length);
            index += perceptionValues.length;
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
