package ia.ecj;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;
import ec.vector.DoubleVectorIndividual;
import ec.vector.FloatVectorIndividual;
import engine.Engine;
import engine.Vector2;
import engine.agent.Agent;
import engine.map.GameMap;
import ia.model.Model;
import ia.model.ModelEnum;
import ia.model.NeuralNetworks.ModelNeuralNetwork;
import ia.model.Random;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ECJ_CTFProblem extends Problem implements SimpleProblemForm {

    // We have to specify a default base
    public static final String P_ECJ_CTFPROBLEM = "ecj-ctf-problem";
    public Parameter defaultBase() { return new Parameter(P_ECJ_CTFPROBLEM); }
    public static final String P_SELECTED_MAP = "selected-map";
    public static final String P_SPEED = "agent-speed";
    public static final String P_NB_PLAYER = "nb-player";
    public static final String P_ROTATE_SPEED = "rotate-speed";


    @Override
    public void evaluate(EvolutionState evolutionState, Individual individual, int i, int i1) {

        String mapPath = evolutionState.parameters.getStringWithDefault(new Parameter(P_SELECTED_MAP),null, "NOT_FOUND");

        double agentSpeed = evolutionState.parameters.getDoubleWithDefault(new Parameter(P_SPEED),null, 1.0);
        double rotateSpeed = evolutionState.parameters.getDoubleWithDefault(new Parameter(P_ROTATE_SPEED),null, 180.0);
        int nbPlayer = evolutionState.parameters.getIntWithDefault(new Parameter(P_NB_PLAYER),null, 3);

        GameMap map;
        try {
            map = GameMap.loadFile(mapPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        int nbEquipes = map.getNbEquipes();

        List<Agent> agentList = new ArrayList<>();
        for(int j = 0; j< nbEquipes; j++){

            Model model;
            for(int k = 0; k<nbPlayer; k++){

                if(j%2==0) model = new Random();
                else {
                    model = new ModelNeuralNetwork();
                    ((ModelNeuralNetwork)model).getNeuralNetwork().insertWeights(((DoubleVectorIndividual)individual).genome);
                }


                agentList.add(new Agent(
                        new Vector2(0, 0),
                        0.35,
                        agentSpeed,
                        agentSpeed/2,
                        rotateSpeed,
                        map.getTeams().get(j),
                        Optional.empty(),
                        model,
                        10.0
                ));
            }
        }

        Engine engine = new Engine(nbEquipes,agentList,map,map.getGameObjects(),1,1);
        engine.run();

    }
}
