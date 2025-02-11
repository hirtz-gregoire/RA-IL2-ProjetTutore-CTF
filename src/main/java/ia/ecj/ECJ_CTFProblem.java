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


    @Override
    public void evaluate(EvolutionState evolutionState, Individual individual, int i, int i1) {

        String mapPath = evolutionState.parameters.getStringWithDefault(new Parameter(P_SELECTED_MAP),null, "NOT_FOUND");
        System.out.println(mapPath);
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
            if(j%2==0) model = new Random();
            else {
                model = new ModelNeuralNetwork();
                ((ModelNeuralNetwork)model).getNeuralNetwork().insertWeights(((DoubleVectorIndividual)individual).genome);
                System.out.println( ((ModelNeuralNetwork)model).getNeuralNetwork().getWeights().length);
            }

            agentList.add(new Agent(
                    new Vector2(0, 0),
                    0.35,
                    1,
                    0.5,
                    180,
                    map.getTeams().get(j),
                    Optional.empty(),
                    model,
                    10.0
            ));
        }

        Engine engine = new Engine(nbEquipes,agentList,map,map.getGameObjects(),1,1);
        engine.run();

    }
}
