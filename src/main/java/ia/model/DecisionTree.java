package ia.model;

import engine.Engine;
import engine.Team;
import engine.agent.*;
import engine.map.GameMap;
import engine.object.GameObject;
import ia.perception.*;

import java.util.*;

public class DecisionTree extends Model {

    private boolean isAttacking;
    private boolean is_role_set;


    public DecisionTree(){
        List<Perception> l_per = new ArrayList<>();
        var nfc = new NearestFlagCompass(null,null);
        var tc = new TerritoryCompass(null,Team.NEUTRAL);
        var nac = new NearestAgentCompass(null,null);
        l_per.add(nfc);
        l_per.add(tc);
        l_per.add(nac);
        setPerceptions(l_per);
        isAttacking = true;
        is_role_set = true;
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
        if(!is_role_set){
            isAttacking = e.getRandom().nextBoolean();
            is_role_set = true;
        }
        ArrayList<Perception> list_perception = (ArrayList<Perception>) getPerceptions();
        PerceptionValue result;
        if (isAttacking) {
            result = list_perception.get(0).getValue(map, agents, objects).getFirst();
            if (getMyself().getFlag().isPresent() || result.vector().getLast() == 0.0 ) {
                result = list_perception.get(1).getValue(map, agents, objects).getFirst();
            }
        }else{
            result = list_perception.get(2).getValue(map, agents, objects).getFirst();
        }
        var value = result.vector().getFirst() - 180;
        value = (180 - Math.abs(value)) * Math.signum(value);
        rot = -Math.clamp(value,-1,1);

        return new Action(rot,1);
    }

    public void setMyself(Agent a) {
        super.setMyself(a);
        //getting perceptions
        ArrayList<Perception> list_perception = (ArrayList<Perception>) getPerceptions();
        NearestFlagCompass nfc = (NearestFlagCompass) list_perception.get(0);
        TerritoryCompass tc = (TerritoryCompass) list_perception.get(1);
        NearestAgentCompass nac = (NearestAgentCompass) list_perception.get(2);
        //setting perceptions
        nfc.setObserved_team(a.getTeam());
        tc.setTerritory_observed(a.getTeam());
        nac.setObserved_team(a.getTeam());
        //senfing them back
        ArrayList<Perception> output = new ArrayList<>();
        output.add(nfc);
        output.add(tc);
        output.add(nac);
        setPerceptions(output);
    }
}
