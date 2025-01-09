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

    NearestFlagCompass flagCompass;
    TerritoryCompass territoryCompass;
    PerceptionRaycast raycast;

    public DecisionTree(){
        setPerceptions(
                List.of(
                        new NearestFlagCompass(null,null),
                        new TerritoryCompass(null, Team.NEUTRAL),
                        new PerceptionRaycast(myself, new double[] {1.5, 2, 1.5}, 3, 60)
                )
        );

        if(flagCompass == null) flagCompass = (NearestFlagCompass) perceptions.stream().filter(e -> e instanceof NearestFlagCompass).findFirst().orElse(null);
        if(territoryCompass == null) territoryCompass = (TerritoryCompass) perceptions.stream().filter(e -> e instanceof TerritoryCompass).findFirst().orElse(null);
        if(raycast == null) raycast = (PerceptionRaycast) perceptions.stream().filter(e -> e instanceof PerceptionRaycast).findFirst().orElse(null);

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
        Objects.requireNonNull(e);
        Objects.requireNonNull(map);
        Objects.requireNonNull(agents);
        Objects.requireNonNull(objects);

        if(!is_role_set) {
            isAttacking = e.getRandom().nextBoolean();
            is_role_set = true;
        }

        if(isAttacking) return getAttackAction(e, map, agents, objects);
        else return getDefenseAction(e, map, agents, objects);

        /*
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
        double rot = -Math.clamp(value,-1,1);
        return new Action(rot,1);
         */
    }

    private Action getAttackAction(Engine e, GameMap map, List<Agent> agents, List<GameObject> objects) {
        // --------------------------------------------------  Perceptions
        PerceptionValue compassValue = null;
        PerceptionValue rayCastLeft = null;
        PerceptionValue rayCastMiddle = null;
        PerceptionValue rayCastRight = null;

        if(raycast != null) {
            var casts = raycast.getValue(map, agents, objects);
            if(casts.size() <= 1) rayCastMiddle = casts.getFirst();
            else {
                rayCastLeft = casts.getFirst();
                rayCastRight = casts.getLast();
                if(casts.size() % 2 == 1) rayCastMiddle = casts.get(casts.size()/2);
            }
        }
        if(flagCompass != null) {
            compassValue = flagCompass.getValue(map, agents, objects).getFirst();
        }
        if(territoryCompass != null || myself.getFlag().isPresent() || (compassValue != null && compassValue.vector().getLast() == 0.0)) {
            compassValue = territoryCompass.getValue(map, agents, objects).getFirst();
        }

        // -------------------------------------------------- Actions
        double targetAngle = 0;

        if(compassValue != null) {
            targetAngle = compassValue.vector().getFirst();
        }
        if(rayCastLeft != null && rayCastRight != null) {
            if(rayCastLeft.type() == PerceptionType.WALL && rayCastRight.type() == PerceptionType.WALL) {
                return new Action(1, -1);
            }
            else if(rayCastLeft.type() == PerceptionType.WALL) targetAngle = rayCastLeft.vector().getLast() - 85;
            else if(rayCastRight.type() == PerceptionType.WALL) targetAngle = rayCastRight.vector().getLast() + 85;
        }
        if(rayCastMiddle != null) {
            if(rayCastMiddle.type() == PerceptionType.ENEMY) {
                targetAngle = rayCastMiddle.vector().getLast();
            }
        }

        targetAngle %= 360;
        if(targetAngle < 0) targetAngle += 360;
        targetAngle -= 180;

        var rotateRatio = (1 - Math.abs(targetAngle)/180) * -Math.signum(targetAngle);
        return new Action(rotateRatio, 1);
    }

    private Action getDefenseAction(Engine e, GameMap map, List<Agent> agents, List<GameObject> objects) {
        return new Action(0, 0);
    }

    public void setMyself(Agent a) {
        super.setMyself(a);
        //getting perceptions
        List<Perception> list_perception = getPerceptions();
        NearestFlagCompass nfc = (NearestFlagCompass) list_perception.get(0);
        TerritoryCompass tc = (TerritoryCompass) list_perception.get(1);
        //setting perceptions
        nfc.setObserved_team(a.getTeam());
        tc.setTerritory_observed(a.getTeam());
    }
}
