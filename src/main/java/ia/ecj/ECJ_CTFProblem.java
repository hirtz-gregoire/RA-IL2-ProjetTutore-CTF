package ia.ecj;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;
import ec.vector.DoubleVectorIndividual;
import engine.Engine;
import engine.Team;
import engine.Vector2;
import engine.agent.Agent;
import engine.map.GameMap;
import ia.evaluationFunctions.DistanceEval;
import ia.model.*;
import ia.model.NeuralNetworks.MLP.MLP;
import ia.model.NeuralNetworks.MLP.TransferFunction;
import ia.model.NeuralNetworks.ModelNeuralNetwork;
import ia.model.NeuralNetworks.NNFileLoader;
import ia.perception.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.Random;

public class ECJ_CTFProblem extends Problem implements SimpleProblemForm {

    // We have to specify a default base
    public static final String P_ECJ_CTFPROBLEM = "ecj-ctf-problem";
    public Parameter defaultBase() { return new Parameter(P_ECJ_CTFPROBLEM); }
    public static final String P_PARAMS = "params";
    private GameMap[] gameMap;
    TransferFunction transferFunction;
    int[] layersSize;

    double agentSpeed;
    double rotateSpeed;
    int nbPlayer;
    int respawnTime;

    List<ModelEnum> modelsTeams;
    List<String> modelsNNTeams;

    List<Perception> perceptions;

    @Override
    public void setup(EvolutionState state, Parameter base) {
        super.setup(state, base);

        ECJParams params = getEcjParams(state.parameters.getString(new Parameter(P_PARAMS), null));

        try {
            gameMap = new GameMap[3];
            System.out.println(params.mapPath());
            gameMap[0] = GameMap.loadFile(params.mapPath());
            gameMap[1] = GameMap.loadFile("ressources/maps/dust.txt");
            gameMap[2] = GameMap.loadFile("ressources/maps/FranceV2.txt");
            gameMap[3] = GameMap.loadFile("ressources/maps/bigben.txt");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        transferFunction = params.transferFunction();

        int layerNumber = params.layers().size();

        layersSize = new int[layerNumber];
        for(int numLayer = 0; numLayer < layerNumber; numLayer++) {
            int layer = params.layers().get(numLayer);
            layersSize[numLayer] = layer;
        }

        agentSpeed = params.playerSpeed();
        rotateSpeed = params.rotateSpeed();
        nbPlayer = params.nbPlayer();
        respawnTime = params.respawnTime();

        modelsTeams = params.modelsTeams();
        modelsNNTeams = params.modelsNNTeams();
        perceptions = params.perceptions();
    }

    @Override
    public void evaluate(EvolutionState evolutionState, Individual individual, int i, int i1) {
        double result = 0;
        for(int j = 0; j < gameMap.length; j++) {
            double fitness = evalMap(evolutionState, individual, i, i1, gameMap[j]);
            result += fitness;
        }
        result /= gameMap.length;
        ((SimpleFitness)(individual.fitness)).setFitness(evolutionState, result,false);
    }

    private double evalMap(EvolutionState evolutionState, Individual individual, int i, int i1, GameMap map) {
        int nbEquipes = map.getNbEquipes();

        // TODO : get the team of the NN and put it inside the eval function instead of the default "blue"
        DistanceEval fitness = new DistanceEval(Team.BLUE);
        Random rand = new Random();
        double result = 0;
        int nbGames = 7;
        int nbModel = 4;
        for(int model = 0; model < nbModel; model++) {
            List<Agent> agentList = generateAgentList((DoubleVectorIndividual) individual,map,nbEquipes,model);
            double modelScore = 0;
            for(int n=0 ;n< nbGames ;n++) {
                GameMap currentMap = map.clone();

                for (Agent agent : agentList) {
                    agent.setInGame(false);
                    agent.setFlag(Optional.empty());
                }

                Engine engine = new Engine(nbEquipes,agentList,currentMap, new ArrayList<>(currentMap.getGameObjects()), fitness, respawnTime,1,rand.nextLong(),60000);
                engine.setRunAsFastAsPossible(true);
                modelScore += engine.run();
            }
            modelScore /= nbGames;
            result += modelScore;
        }
        result = result / nbModel;

        return result;
    }

    private List<Agent> generateAgentList(DoubleVectorIndividual individual, GameMap map, int nbEquipes, int nbModel) {
        List<Agent> agentList = new ArrayList<>();
        for(int numTeam = 0; numTeam< nbEquipes; numTeam++){
            Model model;
            for(int numPlayer = 0; numPlayer<nbPlayer; numPlayer++){
                //Première équipe = Réseau à entraîner
                model = selectModel(individual, numTeam, perceptions, layersSize, modelsNNTeams, modelsTeams, transferFunction, nbModel);
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
        return agentList;
    }

    private static ECJParams getEcjParams(String serializedClass) {
        ECJParams params;
        try {
            byte[] data = Base64.getDecoder().decode(serializedClass);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            params = (ECJParams) ois.readObject();
            ois.close();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la désérialisation de ECJParams", e);
        }
        return params;
    }

    private static Model selectModel(DoubleVectorIndividual individual, int numTeam, List<Perception> perceptions, int[] layers, List<String> modelsNNTeams, List<ModelEnum> modelsTeams, TransferFunction transferFunction, int nbModel) {
        Model model;
        if(numTeam==0) {
            List<Perception> perceptionsClones = new ArrayList<>();
            for(Perception perception : perceptions) {
                perceptionsClones.add(perception.clone());
            }
            model = new ModelNeuralNetwork(new MLP(layers,transferFunction),perceptionsClones);
            ((ModelNeuralNetwork)model).getNeuralNetwork().insertWeights(individual.genome);

        }
        //Equipes suivantes choisit
        else {
            model = new ia.model.Random();
            if(nbModel == 1) model = new AttackDecisionTree();
            if(nbModel == 2) model = new DefenseDecisionTree();
            if(nbModel == 3) model = new DecisionTree();
        }
        return model;
    }
}
