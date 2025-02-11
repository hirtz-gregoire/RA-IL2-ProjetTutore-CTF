package ia.evaluationFunctions;

import engine.Engine;
import engine.Team;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;

import java.util.List;

public abstract class EvaluationFunction {

    private final Team targetTeam;

    public EvaluationFunction(Team targetTeam) {
        this.targetTeam = targetTeam;
    }

    /**
     * Add a position to the running evaluation. Keep heavy calculations in result() as this is computed every frame.
     * @param engine The source engine used
     * @param map The map wich is beeing played on
     * @param agents All the agents of the game
     * @param objects All the objects of the game
     */
    public abstract void compute(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects);

    /**
     * Compute the final result of the evaluation, should reset variables
     * @return A double representing the final evaluation of the game
     */
    public abstract double result();
}
