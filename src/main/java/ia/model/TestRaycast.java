package ia.model;

import engine.Engine;
import engine.Vector2;
import engine.agent.Action;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;
import ia.perception.PerceptionRaycast;
import ia.perception.PerceptionType;

import java.util.List;

public class TestRaycast extends Model {

    double rotateRatio = 0;
    double rotationProba = 0.8;

    public TestRaycast() {
        perceptions.add(
                new PerceptionRaycast(myself, 5, 300, 360)
        );
    }

    /**
     * method that gives completely random movements
     *
     * @param engine The game engine
     * @param map     GameMap
     * @param agents  list of agents in simulation
     * @param objects list of GameObjet in simulation
     * @return Action object with random values
     * @throws IllegalArgumentException <br>- map==null
     *                                  <br>- agents==null
     *                                  <br>- objects==null
     */
    @Override
    public Action getAction(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects) {

        perceptions.getFirst().updatePerceptionValues(map, agents, objects);

        if (map == null)
            throw new IllegalArgumentException("map is null");
        if (agents == null)
            throw new IllegalArgumentException("agents is null");
        if (objects == null)
            throw new IllegalArgumentException("objects is null");

        rotateRatio += (engine.getRandom().nextDouble()-0.5) * rotationProba;
        rotateRatio = Math.max(-1, Math.min(1, rotateRatio));

        return new Action(rotateRatio, 1);
    }
}

