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
import ia.model.Model;
import ia.model.ModelEnum;
import ia.model.NeuralNetworks.MLP.Hyperbolic;
import ia.model.NeuralNetworks.MLP.MLP;
import ia.model.NeuralNetworks.ModelNeuralNetwork;
import ia.model.NeuralNetworks.NNFileLoader;
import ia.perception.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

public class ECJ_CTFProblem extends Problem implements SimpleProblemForm {

    // We have to specify a default base
    public static final String P_ECJ_CTFPROBLEM = "ecj-ctf-problem";
    public Parameter defaultBase() { return new Parameter(P_ECJ_CTFPROBLEM); }
    public static final String P_PARAMS = "params";

    @Override
    public void evaluate(EvolutionState evolutionState, Individual individual, int i, int i1) {

        String serializedClass = evolutionState.parameters.getString(new Parameter(P_PARAMS), null);
        ECJParams params;
        try {
            byte[] data = Base64.getDecoder().decode(serializedClass);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            params = (ECJParams) ois.readObject();
            ois.close();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la désérialisation de ECJParams", e);
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

        List<Perception> perceptions = params.perceptions();

        List<ModelEnum> modelsTeams = params.modelsTeams();
        List<String> modelsNNTeams = params.modelsNNTeams();

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
            for(int numPlayer = 0; numPlayer<nbPlayer; numPlayer++){
                //Première équipe = Réseau à entraîner
                model = selectModel((DoubleVectorIndividual) individual, numTeam, perceptions, layers, modelsNNTeams, modelsTeams, Team.BLUE);
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
        DistanceEval fitness = new DistanceEval(Team.BLUE);
        Random rand = new Random();
        double result = 0;
        int nbGames = 10;
        for(int n=0 ;n< nbGames ;n++){
            Engine engine = new Engine(nbEquipes,agentList,map, new ArrayList<>(map.getGameObjects()), fitness, respawnTime,1,rand.nextLong(),5000 + (evolutionState.generation*1000));
            engine.setRunAsFastAsPossible(true);
            result += engine.run();
            for (Agent agent : agentList) {
                agent.setInGame(false);
                agent.setFlag(Optional.empty());
            }
        }
        result = result / nbGames;

        ((SimpleFitness)(individual.fitness)).setFitness(evolutionState,result,false);
    }

    private static Model selectModel(DoubleVectorIndividual individual, int numTeam, List<Perception> perceptions, int[] layers, List<String> modelsNNTeams, List<ModelEnum> modelsTeams, Team teamToObserve) {
        Model model;
        if(numTeam==0) {
            List<Perception> perceptionsClones = new ArrayList<>();
            for(Perception perception : perceptions) {
                if(perception instanceof TerritoryCompass) ((TerritoryCompass)perception).setTerritory_observed(teamToObserve);
                else if(perception instanceof NearestEnemyFlagCompass) ((NearestEnemyFlagCompass)perception).setObserved_team(teamToObserve);
                else if(perception instanceof NearestAllyFlagCompass) ((NearestAllyFlagCompass)perception).setObserved_team(teamToObserve);
                perceptionsClones.add(perception.clone());
            }
            model = new ModelNeuralNetwork(new MLP(layers,new Hyperbolic()),perceptionsClones);
            ((ModelNeuralNetwork)model).getNeuralNetwork().insertWeights(individual.genome);

        }
        //Equipes suivantes choisit
        else {
            //S'il y a un model de NN choisit
            if (modelsNNTeams.get(numTeam-1) != null) {
                try {
                    model = NNFileLoader.loadModel(modelsNNTeams.get(numTeam -1));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                model = ModelEnum.getClass(modelsTeams.get(numTeam -1));
            }
        }
        return model;
    }
}
