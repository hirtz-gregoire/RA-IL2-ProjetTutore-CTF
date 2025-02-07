package ia.model;

import engine.Engine;
import engine.Team;
import engine.agent.Action;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;
import ia.model.MLP.Hyperbolic;
import ia.model.MLP.MLP;
import ia.model.MLP.Sigmoid;
import ia.model.MLP.TransferFunction;
import ia.perception.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NeuralNetwork extends Model {

    private MLP mlp;

    public NeuralNetwork() {
        setPerceptions(
                List.of(
                        new NearestEnemyFlagCompass(null,null, false),
                        new NearestAllyFlagCompass(null,null, false),
                        new TerritoryCompass(null, Team.NEUTRAL),
                        new PerceptionRaycast(myself, new double[] {1.4, 1.4}, 2, 70),
                        new PerceptionRaycast(myself, 1.5, 8, 180)
                )
        );

        System.out.println(getNumberOfInputsMLP());
        int[] layers = new int[] {getNumberOfInputsMLP(), 70, 40, 10, 2};
        double learningRate = 0.01;
        TransferFunction transferFunction = new Hyperbolic();
        mlp = new MLP(layers, learningRate, transferFunction);
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

        System.out.println(inputs.length);

        //Calcul du réseau
        double[] outputs = mlp.execute(inputs);

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
}
