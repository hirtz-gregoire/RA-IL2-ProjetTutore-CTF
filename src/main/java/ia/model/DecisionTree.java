package ia.model;

import engine.Engine;
import engine.Team;
import engine.agent.*;
import engine.map.GameMap;
import engine.object.GameObject;
import ia.perception.*;

import java.util.*;
import java.util.Random;

public class DecisionTree extends Model {

    private boolean isAttacking;

    public DecisionTree(){
        List<Perception> l_per = new ArrayList<>();
        var nfc = new NearestFlagCompass(null,null);
        var tc = new TerritoryCompass(null,Team.NEUTRAL);
        var nac = new NearestAgentCompass(null,null);
        l_per.add(nfc);
        l_per.add(tc);
        l_per.add(nac);
        setPerceptions(l_per);
        Random r = new Random();
        isAttacking = r.nextBoolean();
        //System.out.println(isAttacking);
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
        ArrayList<Perception> list_perception = (ArrayList<Perception>) getPerceptions();
        PerceptionValue result;
        //if (isAttacking) {
        list_perception.get(0).updatePerceptionValues(map, agents, objects);
        result = list_perception.get(0).getPerceptionValues().getFirst();
            if (getMyself().getFlag().isPresent() || result.vector().getLast() == 0.0 ) {
                list_perception.get(1).updatePerceptionValues(map, agents, objects);
                result = list_perception.get(1).getPerceptionValues().getFirst();
            }
        //}else{
        //    result = list_perception.get(2).getValue(map, agents, objects).getFirst();
        //}
        rot = Math.clamp(result.vector().getFirst(),-1,1);
        //speed = Math.clamp(result.vector().getFirst(),-1,1);
        return new Action(rot,1);
    }

    public void setMyself(Agent a) {
        super.setMyself(a);
        //getting perceptions
        ArrayList<Perception> list_perception = (ArrayList<Perception>) getPerceptions();
        NearestFlagCompass nfc = (NearestFlagCompass) list_perception.get(0);
        TerritoryCompass tc = (TerritoryCompass) list_perception.get(1);
        //setting perceptions
        nfc.setObserved_team(a.getTeam());
        tc.setMy_agent(a);
        tc.setTerritory_observed(a.getTeam());
        //senfing them back
        ArrayList<Perception> output = new ArrayList<>();
        output.add(nfc);
        output.add(tc);
        output.add(list_perception.get(2));
        setPerceptions(output);
    }
}
