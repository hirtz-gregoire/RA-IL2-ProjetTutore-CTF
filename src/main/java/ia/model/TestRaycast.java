package ia.model;

import engine.Engine;
import engine.agent.Action;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;
import ia.perception.Perception;
import ia.perception.PerceptionRaycast;

import java.util.List;

public class TestRaycast extends Model {

    double rotatRatio = 0;

    public TestRaycast() {
        perceptions.add(
                new PerceptionRaycast(myself, 3, 2, 60)
        );
    }

    /**
     * method that gives completely random movements
     *
     * @param engine
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

        if (map == null)
            throw new IllegalArgumentException("map is null");
        if (agents == null)
            throw new IllegalArgumentException("agents is null");
        if (objects == null)
            throw new IllegalArgumentException("objects is null");

        var rayHits = perceptions.getFirst().getValue(map, agents, objects);

        rotatRatio += (engine.getRandom().nextDouble()-0.5) * 0.8;
        rotatRatio = Math.max(-1, Math.min(1, rotatRatio));

        return new Action(rotatRatio, 1);

    }

    @Override
    public Action getAction(GameMap map, List<Agent> agents, List<GameObject> objects) {
        return null;
    }
}

