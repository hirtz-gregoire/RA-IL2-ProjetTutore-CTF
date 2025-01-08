package ia.model;

import engine.Engine;
import engine.agent.Action;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;
import ia.perception.Perception;
import ia.perception.PerceptionRaycast;
import ia.perception.PerceptionType;

import java.util.List;
import java.util.stream.Collectors;

public class TestRaycast extends Model {

    double rotateRatio = 0;

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
        var left = rayHits.getFirst();
        //var middle = rayHits.get(1);
        var right = rayHits.getLast();

        //System.out.println(rayHits);

        double targetAngle = -4200;

        if(left.type() == PerceptionType.WALL && right.type() == PerceptionType.WALL) {
            // Average of the two
            double delta = right.vector().getLast() - left.vector().getLast();
            delta = delta - 360.0 * Math.floor((delta + 180.0) / 360.0);
            delta *= right.vector().get(1) / (right.vector().get(1)+left.vector().get(1));
            //System.out.println(right.vector().get(1) / (right.vector().get(1)+left.vector().get(1)));
            targetAngle = right.vector().getLast() + delta;
            targetAngle = (targetAngle + 360.0) % 360.0;
        }
        else if(left.type() == PerceptionType.WALL) targetAngle = left.vector().getLast() - 90;
        else if(right.type() == PerceptionType.WALL) targetAngle = right.vector().getLast() + 90;

        if(targetAngle == -4200) {
            rotateRatio += (engine.getRandom().nextDouble()-0.5) * 0.8;
            rotateRatio = Math.max(-1, Math.min(1, rotateRatio));

            return new Action(rotateRatio, 1);
        }

        targetAngle %= 360;
        if(targetAngle < 0) targetAngle += 360;

        //System.out.println("target angle: " + targetAngle);
        targetAngle -= 180;

        rotateRatio = (1 - Math.abs(targetAngle)/180) * -Math.signum(targetAngle);
        return new Action(rotateRatio, 1);
    }
}

