package ia.model;

import engine.Team;
import engine.agent.*;
import engine.map.GameMap;
import engine.object.GameObject;
import ia.perception.*;

import java.util.List;

public class DecisionTree implements Model{

    //TODO

    public DecisionTree(Agent a){
        //TODO
    }

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
        return null; //TODO
    }
}
