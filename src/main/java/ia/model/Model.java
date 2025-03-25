package ia.model;

import engine.Engine;
import engine.agent.Action;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;
import ia.perception.Perception;

import java.util.ArrayList;
import java.util.List;

/**
 * interface Models, basis of all decision-making models to be applied to the agent
 */
public abstract class Model {

    protected Agent myself;
    protected List<Perception> perceptions = new ArrayList<>();

    public List<Perception> getPerceptions() {
        return perceptions;
    }

    public void setPerceptions(List<Perception> perceptions) {
        this.perceptions = perceptions;
    }

    public void setMyself(Agent a) {
        myself = a;
        for (Perception p : perceptions) {
            p.setMy_agent(a);
        }
    }

    public Agent getMyself() {
        return myself;
    }
    /**
     * method which provides an action relative to the parameter
     * @param engine Engine
     * @param map GameMap
     * @param agents list of agents in simulation
     * @param objects list of GameObjet in simulation
     * @return Action(rotationRatio, speedRatio)
     */
    public abstract Action getAction(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects);

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < perceptions.size(); i++) {
            Perception p = perceptions.get(i);
            str.append(p.toString());
            if(i < perceptions.size() - 1){
                str.append("\n");
            }
        }
        return str.toString();
    }
}
