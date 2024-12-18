package ia.model;

import engine.Engine;
import engine.agent.Action;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;

import java.util.List;

/**
 * interface Models, basis of all decision-making models to be applied to the agent
 */
public interface Model {

    /**
     * method which provides an action relative to the parameter
     *
     * @param engine
     * @param map     GameMap
     * @param agents  list of agents in simulation
     * @param objects list of GameObjet in simulation
     * @return Action(rotationRatio, speedRatio)
     */
    Action getAction(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects);
}
