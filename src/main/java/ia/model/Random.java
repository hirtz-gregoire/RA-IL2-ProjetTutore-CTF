package ia.model;

import engine.agent.Action;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;
import ia.perception.Perception;

import java.util.List;

public class Random implements Model{

    /**
     * method that gives completely random movements
     * @param map GameMap
     * @param agents list of agents in simulation
     * @param objects list of GameObjet in simulation
     * @return Action object with random values
     * @throws IllegalArgumentException
     * <br>- map==null
     * <br>- agents==null
     * <br>- objects==null
     */
    @Override
    public Action getAction(GameMap map, List<Agent> agents, List<GameObject> objects) {

        if (map == null)
            throw new IllegalArgumentException("map is null");
        if (agents == null)
            throw new IllegalArgumentException("agents is null");
        if (objects == null)
            throw new IllegalArgumentException("objects is null");

        int sens;

        // r = [0; 1] * {-1; 1}
        // s = [0; 1] * {-1; 1}
        sens = Math.random() < 0.5 ? 1 : -1;
        double r = Math.random() * sens;
        sens = Math.random() < 0.5 ? 1 : -1;
        double s = Math.random() * sens;

        return new Action(r, s);

    }
}
