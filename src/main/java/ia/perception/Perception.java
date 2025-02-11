package ia.perception;

import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;

import java.util.ArrayList;
import java.util.List;

public abstract class Perception {
    protected Agent my_agent;
    final int maxAngle = 360;
    private List<PerceptionValue> perceptionValues = new ArrayList<>();

    public Perception(Agent a){
        my_agent = a;
    }

    /** update PerceptionValues based on the current state of the world, should be used once every turn */
    public abstract void updatePerceptionValues(GameMap map, List<Agent> agents, List<GameObject> gameObjects);

    public Agent getMy_agent() {
        return my_agent;
    }
    public void setMy_agent(Agent my_agent) {
        this.my_agent = my_agent;
    }

    public List<PerceptionValue> getPerceptionValues() {
        return new ArrayList<>(perceptionValues);
    }

    public abstract List<Double> getPerceptionsValuesNormalise();
    public void setPerceptionValues(List<PerceptionValue> perceptionValues) {
        this.perceptionValues = new ArrayList<>(perceptionValues);
    }

    abstract public int getNumberOfPerceptionsValuesNormalise();
}
