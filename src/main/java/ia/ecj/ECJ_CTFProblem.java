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
import ia.evaluationFunctions.AllyDistanceEval;
import ia.evaluationFunctions.EvaluationFunction;
import ia.model.*;
import ia.model.NeuralNetworks.MLP.MLP;
import ia.model.NeuralNetworks.MLP.TransferFunction;
import ia.model.NeuralNetworks.ModelNeuralNetwork;
import ia.model.NeuralNetworks.ModelRecurentNeuralNetwork;
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
    int memorySize;

    // Random seed handling
    // We keep the same seed across the entire gen to make sure that all individuals are evaluated equally
    int[] seedAges;
    Long[] seeds;

    // Pour les équipes adverses : indices 0 = équipe 1, 1 = équipe 2, etc.
    List<List<ModelEnum>> modelsTeams;
    List<List<String>> modelsNNTeams;

    List<Perception> perceptions;

    @Override
    public void setup(EvolutionState state, Parameter base) {
        super.setup(state, base);

        ECJParams params = getEcjParams(state.parameters.getString(new Parameter(P_PARAMS), null));

        try {
            List<String> mapPaths = params.mapPath();
            gameMap = new GameMap[mapPaths.size()];
            for (int i = 0; i < mapPaths.size(); i++) {
                gameMap[i] = GameMap.loadFile(mapPaths.get(i));
                System.out.println("Chargement de la map : " + mapPaths.get(i));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        transferFunction = params.transferFunction();

        int layerNumber = params.layers().size();
        layersSize = new int[layerNumber];
        for (int numLayer = 0; numLayer < layerNumber; numLayer++) {
            layersSize[numLayer] = params.layers().get(numLayer);
        }

        agentSpeed = params.playerSpeed();
        rotateSpeed = params.rotateSpeed();
        nbPlayer = params.nbPlayer();
        respawnTime = params.respawnTime();

        modelsTeams = params.modelsTeams();
        modelsNNTeams = params.modelsNNTeams();
        perceptions = params.perceptions();

        memorySize = params.memorySize();
        seedAges = new int[state.evalthreads];
        seeds = new Long[state.evalthreads];
    }

    @Override
    public void evaluate(EvolutionState evolutionState, Individual individual, int subpop, int threadNum) {
        // Random seed handling
        // We keep the same seed across the entire gen to make sure that all individuals are evaluated equally
        if(seedAges[threadNum] < evolutionState.generation || seeds[threadNum] <= 0) {
            seedAges[threadNum] = evolutionState.generation;
            seeds[threadNum] = evolutionState.random[threadNum].nextLong();
        }

        double result = 0;
        for (int j = 0; j < gameMap.length; j++) {
            double fitness = evalMap(evolutionState, individual, subpop, threadNum, gameMap[j], j);
            if (evolutionState.generation % 50 == 0) {
                System.out.println("Individu " + subpop + " - Fitness sur map " + j + " : " + fitness);
            }
            result += fitness;
        }
        result /= gameMap.length;
        ((SimpleFitness)(individual.fitness)).setFitness(evolutionState, result, false);
    }

    private double evalMap(EvolutionState evolutionState, Individual individual, int subpop, int threadNum, GameMap map, int mapIndex) {
        int nbEquipes = map.getNbEquipes();
        EvaluationFunction fitness = new AllyDistanceEval(Team.BLUE);
        double result = 0;
        int nbGames = 10;

        // Génère une liste plate d'agents pour toutes les équipes
        List<Agent> agentList = generateAgentList((DoubleVectorIndividual) individual, map, nbEquipes, memorySize);

        for (int n = 0; n < nbGames; n++) {
            GameMap currentMap = map.clone();
            for (Agent agent : agentList) {
                agent.setInGame(false);
                agent.setFlag(Optional.empty());
            }
            Engine engine = new Engine(nbEquipes, agentList, currentMap, new ArrayList<>(currentMap.getGameObjects()),
                    fitness, respawnTime, 1, seeds[threadNum], 80000);
            engine.setRunAsFastAsPossible(true);
            result += engine.run();
        }
        return result / nbGames;
    }

    private List<Agent> generateAgentList(DoubleVectorIndividual individual, GameMap map, int nbEquipes, int memorySize) {
        List<Agent> agentList = new ArrayList<>();
        for (int numTeam = 0; numTeam < nbEquipes; numTeam++) {
            // Sélection du modèle pour l'équipe numTeam via la méthode mise à jour
            Model model = selectModel(individual, numTeam, perceptions, layersSize,
                    modelsNNTeams, modelsTeams, transferFunction, memorySize);
            for (int numPlayer = 0; numPlayer < nbPlayer; numPlayer++) {
                agentList.add(new Agent(
                        new Vector2(0, 0),
                        0.35,
                        agentSpeed,
                        agentSpeed / 2,
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

    private static Model selectModel(DoubleVectorIndividual individual, int numTeam, List<Perception> perceptions, int[] layers,
                                     List<List<String>> modelsNNTeams, List<List<ModelEnum>> modelsTeams,
                                     TransferFunction transferFunction, int memorySize) {
        Model model;
        if (numTeam == 0) { // Équipe en apprentissage
            System.out.println("Team 0 (Training): Using ModelRecurentNeuralNetwork with layers "
                    + java.util.Arrays.toString(layers)
                    + ", memorySize " + memorySize
                    + ", transfer function " + transferFunction);
            List<Perception> perceptionsClones = new ArrayList<>();
            for (Perception perception : perceptions) {
                perceptionsClones.add(perception.clone());
            }
            model = new ModelRecurentNeuralNetwork(new MLP(layers, transferFunction), perceptionsClones, memorySize);
            ((ModelNeuralNetwork) model).getNeuralNetwork().insertWeights(individual.genome);
        } else { // Équipes adverses
            // Les listes de modèles pour les adversaires commencent à l'index 0, qui correspond à l'équipe 1
            List<ModelEnum> availableModels = modelsTeams.size() >= numTeam ? modelsTeams.get(numTeam - 1)
                    : List.of(ModelEnum.Random);
            ModelEnum chosenModel = availableModels.get(new Random().nextInt(availableModels.size()));

            switch (chosenModel) {
                case DecisionTree -> model = new DecisionTree();
                case DefenseDecisionTree -> model = new DefenseDecisionTree();
                case AttackDecisionTree -> model = new AttackDecisionTree();
                case NeuralNetwork -> {
                    try {
                        List<String> availableNNs = modelsNNTeams.size() >= numTeam ? modelsNNTeams.get(numTeam - 1)
                                : List.of();
                        if (!availableNNs.isEmpty()) {
                            String chosenPath = availableNNs.get(new Random().nextInt(availableNNs.size()));
                            model = NNFileLoader.loadModel(chosenPath);
                        } else {
                            model = new ia.model.Random();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        model = new ia.model.Random();
                    }
                }
                case Random -> model = new ia.model.Random();
                default -> model = new ia.model.Random();
            }
        }
        return model;
    }
}
