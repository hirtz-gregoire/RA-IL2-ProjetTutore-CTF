package engine.agent;

import engine.Coordinate;
import engine.Team;
import ia.perception.Perception;
import engine.map.Map;
import engine.object.Flag;
import engine.object.Object;

import java.util.List;
import java.util.Optional;

public abstract class Agent {

    private Coordinate coordinate;
    private double radius;
    private int speed;
    private int backSpeed;
    private Team team;
    private Optional<Flag> flag;
    private List<Perception> perceptions;

    public abstract Action getAction(Map map, List<Agent> agents, List<Object> objects);

}
