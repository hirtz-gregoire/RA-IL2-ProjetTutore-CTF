package ia.model;

import engine.Engine;
import engine.Team;
import engine.agent.*;
import engine.map.GameMap;
import engine.object.GameObject;
import ia.perception.*;

import java.util.*;

public class DecisionTree extends Model {


    private NearestFlagCompass nfc;
    private TerritoryCompass tc;

    public DecisionTree(){
        nfc = new NearestFlagCompass(null,null);
        tc = new TerritoryCompass(null,Team.NEUTRAL);
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
        PerceptionValue result = nfc.getValue(map, agents, objects).getFirst();
        if (getMyself().getFlag().isPresent() || result.vector().getLast() == 0.0 ) {
            result = tc.getValue(map, agents, objects).getFirst();
        }
        rot = Math.clamp(result.vector().getFirst(),-1,1);
        //speed = Math.clamp(result.vector().getFirst(),-1,1);
        return new Action(rot,1);
    }

    public void setMyself(Agent a) {
        super.setMyself(a);
        nfc.setMy_agent(a);
        nfc.setObserved_team(a.getTeam());
        tc.setMy_agent(a);
        tc.setTerritory_observed(a.getTeam());
    }
}
