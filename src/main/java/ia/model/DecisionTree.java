package ia.model;

import engine.Engine;
import engine.Team;
import engine.agent.*;
import engine.map.GameMap;
import engine.object.GameObject;
import ia.perception.*;

import java.util.*;

public class DecisionTree extends Model {

    private static final double RANDOM_STRENGTH = 0.8;

    private boolean isAttacking;
    private boolean is_role_set;

    private NearestEnemyFlagCompass enemyFlagCompass;
    private NearestAllyFlagCompass allyFlagCompass;
    private TerritoryCompass territoryCompass;
    private PerceptionRaycast raycast;

    private Action previousAction;

    public DecisionTree(){
        setPerceptions(
                List.of(
                        new NearestEnemyFlagCompass(null,null, true),
                        new NearestAllyFlagCompass(null,null, false),
                        new TerritoryCompass(null, Team.NEUTRAL),
                        new PerceptionRaycast(myself, new double[] {1.4, 2, 1.4}, 3, 70)
                )
        );

        if(enemyFlagCompass == null) enemyFlagCompass = (NearestEnemyFlagCompass) perceptions.stream().filter(e -> e instanceof NearestEnemyFlagCompass).findFirst().orElse(null);
        if(allyFlagCompass == null) allyFlagCompass = (NearestAllyFlagCompass) perceptions.stream().filter(e -> e instanceof NearestAllyFlagCompass).findFirst().orElse(null);
        if(territoryCompass == null) territoryCompass = (TerritoryCompass) perceptions.stream().filter(e -> e instanceof TerritoryCompass).findFirst().orElse(null);
        if(raycast == null) raycast = (PerceptionRaycast) perceptions.stream().filter(e -> e instanceof PerceptionRaycast).findFirst().orElse(null);

        isAttacking = false;
        is_role_set = false;
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
    public Action getAction(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects) {
        Objects.requireNonNull(engine);
        Objects.requireNonNull(map);
        Objects.requireNonNull(agents);
        Objects.requireNonNull(objects);

        for(Perception perception : perceptions) {
            perception.updatePerceptionValues(map, agents, objects);
        }

        if(previousAction == null) previousAction = new Action(0, 0);

        if(!is_role_set) {
            isAttacking = engine.getRandom().nextBoolean();
            //isAttacking = myself.getTeam() == Team.BLUE;
            is_role_set = true;
        }

        if(isAttacking) return getAttackAction(engine, map, agents, objects);
        else return getDefenseAction(engine, map, agents, objects);
    }

    private int backTrackLeft;
    private Action getAttackAction(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects) {
        //System.out.println(backTrackLeft);
        if(--backTrackLeft > 0) return new Action(0, -1);

        // --------------------------------------------------  Perceptions
        PerceptionValue compassValue = null;
        PerceptionValue rayCastLeft = null;
        PerceptionValue rayCastMiddle = null;
        PerceptionValue rayCastRight = null;

        if(raycast != null) {
            var casts = raycast.getPerceptionValues();
            if(casts.size() <= 1) rayCastMiddle = casts.getFirst();
            else {
                rayCastLeft = casts.getFirst();
                rayCastRight = casts.getLast();
                if(casts.size() % 2 == 1) rayCastMiddle = casts.get(casts.size()/2);
            }
        }
        if(enemyFlagCompass != null) {
            compassValue = enemyFlagCompass.getPerceptionValues().getFirst();
        }
        if(territoryCompass != null && (myself.getFlag().isPresent() || (compassValue != null && compassValue.vector().getLast() == 0.0))) {
            compassValue = territoryCompass.getPerceptionValues().getFirst();
            if(compassValue.vector().get(1) * myself.getSpeed() < 1) compassValue = null;
        }

        // -------------------------------------------------- Actions
        var rotation = previousAction.rotationRatio();
        rotation += (engine.getRandom().nextDouble()-0.5) * RANDOM_STRENGTH;
        rotation = Math.max(-1, Math.min(1, rotation));
        double targetAngle = rotation * 180 + 180;

        //System.out.println("-----------------");
        //System.out.println("base " + targetAngle);

        if(compassValue != null) {
            //System.out.println("compassValue " + compassValue);
            targetAngle = compassValue.vector().getFirst();
            //System.out.println("compass " + targetAngle);
        }
        if(rayCastLeft != null && rayCastRight != null) {
            if(rayCastLeft.type() == PerceptionType.WALL && rayCastRight.type() == PerceptionType.WALL
            && rayCastLeft.vector().get(1) <= 0.6 && rayCastRight.vector().get(1) <= 0.6) {
                backTrackLeft = 100;
                return new Action(0, -1);
            }
            else if(rayCastLeft.type() == PerceptionType.WALL) targetAngle = rayCastLeft.vector().getLast() - 80;
            else if(rayCastRight.type() == PerceptionType.WALL) targetAngle = rayCastRight.vector().getLast() + 80;

            //System.out.println("wall cast " + targetAngle);
        }
        if(rayCastMiddle != null) {
            if(rayCastMiddle.type() == PerceptionType.ENEMY) {
                targetAngle = -rayCastMiddle.vector().getLast();
                //System.out.println("mid cast " + targetAngle);
            }
        }

        targetAngle %= 360;
        if(targetAngle < 0) targetAngle += 360;
        targetAngle -= 180;

        var action = new Action(-Math.clamp(targetAngle,-1,1), 1);
        previousAction = action;
        return action;
//        var rotateRatio = (1 - Math.abs(targetAngle) / 180) * -Math.signum(targetAngle);
//        var action = new Action(rotateRatio, 1);
//        previousAction = action;
//        return action;
    }

    private Action getDefenseAction(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects) {
        var rotation = previousAction.rotationRatio();
        rotation += (engine.getRandom().nextDouble()-0.5) * RANDOM_STRENGTH;
        rotation = Math.max(-1, Math.min(1, rotation));
        double targetAngle = rotation * 180 + 180;

        if(allyFlagCompass != null) {
            targetAngle = allyFlagCompass.getPerceptionValues().getFirst().vector().getFirst();
            targetAngle += 0.0000001f;
        }

        //var action = new Action(-Math.signum(targetAngle), 1);
        //previousAction = action;
        //return action;

        targetAngle %= 360;
        if(targetAngle < 0) targetAngle += 360;
        targetAngle -= 180;

        var rotateRatio = (1 - Math.abs(targetAngle) / 180) * -Math.signum(targetAngle);
        var action = new Action(Math.signum(rotateRatio), 1);
        previousAction = action;
        return action;

    }

    public void setMyself(Agent a) {
        super.setMyself(a);
        //getting perceptions
        List<Perception> list_perception = getPerceptions();
        NearestEnemyFlagCompass nefc = (NearestEnemyFlagCompass) list_perception.get(0);
        NearestAllyFlagCompass nfc = (NearestAllyFlagCompass) list_perception.get(1);
        TerritoryCompass tc = (TerritoryCompass) list_perception.get(2);
        //setting perceptions
        nefc.setObserved_team(a.getTeam());
        nfc.setObserved_team(a.getTeam());
        tc.setTerritory_observed(a.getTeam());
    }
}
