package ia.perception;

import engine.Coordinate;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;

import java.util.List;
import org.apache.commons.math3.geometry.*;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class BoussoleAgent extends Perception {
    private Agent agent_suivi;
    public BoussoleAgent(Agent a,Agent suivi) {
        super(a);
        this.agent_suivi = suivi;
    }

    @Override
    public PerceptionValue getValue(GameMap map, List<Agent> agents, List<GameObject> go) {
        double prev_angle = getMy_agent().getAngular_position();

        return new PerceptionValue(PerceptionType.ENEMY,null);
    }
}
