package ia.model;

import engine.Engine;
import engine.Team;
import engine.agent.Action;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;
import ia.model.MLP.MLP;
import ia.model.MLP.Sigmoid;
import ia.model.MLP.TransferFunction;
import ia.perception.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NeuralNetwork extends Model {

    private MLP mlp;

    private NearestEnemyFlagCompass enemyFlagCompass;
    private NearestAllyFlagCompass allyFlagCompass;
    private TerritoryCompass territoryCompass;
    private PerceptionRaycast wallCaster;
    private PerceptionRaycast enemyCaster;

    double rotateRatio = 0;
    double rotationProba = 0.8;

    public NeuralNetwork() {
        int[] layers = new int[] {3, 5, 4, 2};
        double learningRate = 0.01;
        TransferFunction transferFunction = new Sigmoid();
        mlp = new MLP(layers, learningRate, transferFunction);
        setPerceptions(
                List.of(
                        new NearestEnemyFlagCompass(myself,null, true),
                        new NearestAllyFlagCompass(myself,null, false),
                        new TerritoryCompass(myself, Team.NEUTRAL),
                        new PerceptionRaycast(myself, new double[] {1.4, 1.4}, 2, 70),
                        new PerceptionRaycast(myself, 1.5, 8, 180)
                )
        );

        if(enemyFlagCompass == null) enemyFlagCompass = (NearestEnemyFlagCompass) perceptions.stream().filter(e -> e instanceof NearestEnemyFlagCompass).findFirst().orElse(null);
        if(allyFlagCompass == null) allyFlagCompass = (NearestAllyFlagCompass) perceptions.stream().filter(e -> e instanceof NearestAllyFlagCompass).findFirst().orElse(null);
        if(territoryCompass == null) territoryCompass = (TerritoryCompass) perceptions.stream().filter(e -> e instanceof TerritoryCompass).findFirst().orElse(null);
        if(wallCaster == null) wallCaster = (PerceptionRaycast) perceptions.stream().filter(e -> e instanceof PerceptionRaycast).findFirst().orElse(null);
        if(enemyCaster == null) enemyCaster = (PerceptionRaycast) perceptions.stream().filter(e -> e instanceof PerceptionRaycast).skip(1).findFirst().orElse(null);
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

        //Récupération des perceptions à mettre dans les neurones d'entrées (normalisés)
        List<Double> inputs = new ArrayList<>();

//        private NearestEnemyFlagCompass enemyFlagCompass;
//        private NearestAllyFlagCompass allyFlagCompass;
//        private TerritoryCompass territoryCompass;
//        private PerceptionRaycast wallCaster;
//        private PerceptionRaycast enemyCaster;

        System.out.println("PERCEPTIONS");
        System.out.println(wallCaster.getPerceptionValues());
        System.out.println(wallCaster.getPerceptionsValuesNormalise());

        //RANDOM
        rotateRatio += (engine.getRandom().nextDouble()-0.5) * rotationProba;
        rotateRatio = Math.max(-1, Math.min(1, rotateRatio));
        return new Action(rotateRatio, 1);
    }
}
