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

    public TestRaycast() {
        perceptions.add(
                new PerceptionRaycast(myself, 5, 150, 260)
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

        if (map == null)
            throw new IllegalArgumentException("map is null");
        if (agents == null)
            throw new IllegalArgumentException("agents is null");
        if (objects == null)
            throw new IllegalArgumentException("objects is null");

        perceptions.getFirst().updatePerceptionValues(map, agents, objects);
        var rayHits = perceptions.getFirst().getPerceptionValues();
        var left = rayHits.getFirst();
        //var middle = rayHits.get(1);
        var right = rayHits.getLast();

        double targetAngle;

        if(left.type() == PerceptionType.WALL && right.type() == PerceptionType.WALL) {
            // Average of the two normals
            return new Action(-rotateRatio, -1);
        }
        else if(left.type() == PerceptionType.WALL) targetAngle = left.vector().getLast() - 85;
        else if(right.type() == PerceptionType.WALL) targetAngle = right.vector().getLast() + 85;
        else {
            rotateRatio += (engine.getRandom().nextDouble()-0.5) * 0.8;
            rotateRatio = Math.max(-1, Math.min(1, rotateRatio));

            return new Action(rotateRatio, 1);
        }

        targetAngle %= 360;
        if(targetAngle < 0) targetAngle += 360;

        targetAngle -= 180;
        return new Action(-Math.clamp(targetAngle,-1,1), 1);
    }
}

