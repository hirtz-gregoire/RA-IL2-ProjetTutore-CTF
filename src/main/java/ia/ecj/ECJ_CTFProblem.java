package ia.ecj;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;
import ec.vector.DoubleVectorIndividual;
import ec.vector.FloatVectorIndividual;
import engine.Engine;
import engine.Team;
import engine.Vector2;
import engine.agent.Agent;
import engine.map.GameMap;
import ia.evaluationFunctions.DistanceEval;
import ia.model.Model;
import ia.model.ModelEnum;
import ia.model.NeuralNetworks.MLP.MLP;
import ia.model.NeuralNetworks.MLP.Sigmoid;
import ia.model.NeuralNetworks.ModelNeuralNetwork;
import ia.model.Random;
import ia.perception.Perception;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ECJ_CTFProblem extends Problem implements SimpleProblemForm {

    // We have to specify a default base
    public static final String P_ECJ_CTFPROBLEM = "ecj-ctf-problem";
    public Parameter defaultBase() { return new Parameter(P_ECJ_CTFPROBLEM); }
    public static final String P_PARAMS = "params";



    @Override
    public void evaluate(EvolutionState evolutionState, Individual individual, int i, int i1) {

        Class serializedClass = evolutionState.parameters.getClassForParameter(new Parameter(P_PARAMS), null, ECJParams.class);
        ECJParams params;
        try {
            params = (ECJParams) serializedClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String mapPath = params.mapPath();

        double agentSpeed = params.playerSpeed();
        double rotateSpeed = params.rotateSpeed();
        int nbPlayer = params.nbPlayer();
        int respawnTime = params.respawnTime();

        int layerNumber = params.layers().size();
        int[] layers = new int[layerNumber];
        for(int numLayer = 0; numLayer < layerNumber; numLayer++) {
            int layer = params.layers().get(numLayer);
            layers[numLayer] = layer;
        }

        int perceptionCount = params.perceptions().size();
        List<Perception> perceptions = params.perceptions();

        GameMap map;
        try {
            map = GameMap.loadFile(mapPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int nbEquipes = map.getNbEquipes();

        List<Agent> agentList = new ArrayList<>();
        for(int numTeam = 0; numTeam< nbEquipes; numTeam++){

            Model model;
            for(int k = 0; k<nbPlayer; k++){

                if(numTeam%2==0) model = new Random();
                else {
                    List<Perception> perceptionsClones = new ArrayList<>();
                    for(Perception perception : perceptions)
                    {
                        perceptionsClones.add(perception.clone());
                        System.out.println(perception);
                    }
                    model = new ModelNeuralNetwork(new MLP(layers,new Sigmoid()),perceptionsClones);
                    ((ModelNeuralNetwork)model).getNeuralNetwork().insertWeights(((DoubleVectorIndividual)individual).genome);
                }

                agentList.add(new Agent(
                        new Vector2(0, 0),
                        0.35,
                        agentSpeed,
                        agentSpeed/2,
                        rotateSpeed,
                        map.getTeams().get(numTeam),
                        Optional.empty(),
                        model,
                        10.0
                ));
            }
        }

        // TODO : get the team of the NN and put it inside the eval function instead of the default "blue"
        Engine engine = new Engine(nbEquipes,agentList,map, map.getGameObjects(), new DistanceEval(Team.BLUE), respawnTime,1);
        engine.run();
    }
}
