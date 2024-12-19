package ia.model;

import engine.Engine;
import engine.Team;
import engine.agent.*;
import engine.map.GameMap;
import engine.object.GameObject;
import ia.perception.*;

import java.util.*;

public class DecisionTree extends Model {

    private NearestEnemyFlagCompass nefc;
    private TerritoryCompass tc;

    public DecisionTree(){

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
    public Action getAction(Engine e, GameMap map, List<Agent> agents, List<GameObject> objects) {
        double rot;
        double speed;
        PerceptionValue result;
        if (getMyself().getFlag().isPresent()){
            result = tc.getValue(map, agents, objects);
        }else{
            result = nefc.getValue(map, agents, objects);
        }
        rot = Math.clamp(result.vector().getFirst(),-1,1);
        //speed = Math.clamp(result.vector().getFirst(),-1,1);
        return new Action(rot,1);
    }

    public void setMyself(Agent a) {
        super.setMyself(a);
        nefc = new NearestEnemyFlagCompass(a);
        tc = new TerritoryCompass(a,a.getTeam());
    }
}
