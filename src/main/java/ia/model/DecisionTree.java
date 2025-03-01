package ia.model;

import engine.Engine;
import engine.Vector2;
import engine.agent.*;
import engine.map.GameMap;
import engine.object.GameObject;
import ia.perception.*;

import java.util.*;

public class DecisionTree extends Model {

    private static final double RANDOM_STRENGTH = 0.7;
    private double currentRandomRotation = 0;

    private boolean isAttacking;
    private boolean is_role_set;

    private FlagCompass enemyFlagCompass;
    private FlagCompass allyFlagCompass;
    private TerritoryCompass territoryCompass;
    private PerceptionRaycast wallCaster;
    private PerceptionRaycast enemyCaster;

    private Action previousAction;

    public DecisionTree() {
        setPerceptions(
                List.of(
                        new FlagCompass(myself,new Filter(Filter.TeamMode.ENEMY, Filter.DistanceMode.NEAREST), true),
                        new FlagCompass(myself,new Filter(Filter.TeamMode.ALLY, Filter.DistanceMode.NEAREST), false),
                        new TerritoryCompass(myself, new Filter(Filter.TeamMode.ALLY, Filter.DistanceMode.NEAREST)),
                        new PerceptionRaycast(myself, new double[] {1.4, 1.4}, 2, 70),
                        new PerceptionRaycast(myself, 1.5, 8, 180)
                )
        );

        if(enemyFlagCompass == null) enemyFlagCompass = (FlagCompass) perceptions.stream().filter(e -> {
            if(e instanceof FlagCompass flagCompass) return flagCompass.getTeamMode() == Filter.TeamMode.ENEMY;
            return false;
        }).findFirst().orElse(null);
        if(allyFlagCompass == null) allyFlagCompass = (FlagCompass) perceptions.stream().filter(e -> {
            if(e instanceof FlagCompass flagCompass) return flagCompass.getTeamMode() == Filter.TeamMode.ALLY;
            return false;
        }).findFirst().orElse(null);
        if(territoryCompass == null) territoryCompass = (TerritoryCompass) perceptions.stream().filter(e -> e instanceof TerritoryCompass).findFirst().orElse(null);
        if(wallCaster == null) wallCaster = (PerceptionRaycast) perceptions.stream().filter(e -> e instanceof PerceptionRaycast).findFirst().orElse(null);
        if(enemyCaster == null) enemyCaster = (PerceptionRaycast) perceptions.stream().filter(e -> e instanceof PerceptionRaycast).skip(1).findFirst().orElse(null);

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
            is_role_set = true;
        }

        if(isAttacking) return getAttackAction(engine, map, agents, objects);
        else return getDefenseAction(engine, map, agents, objects);
    }

    private int backTrackLeft;
    private Action getAttackAction(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects) {
        if(--backTrackLeft > 0) return new Action(0, -1);

        // --------------------------------------------------  Perceptions
        PerceptionValue compassValue = null;
        PerceptionValue rayCastLeft = null;
        List<PerceptionValue> rayCastMiddle = null;
        PerceptionValue rayCastRight = null;

        boolean isInHomeLand = false;

        if(wallCaster != null) {
            var casts = wallCaster.getPerceptionValues();
            if(casts.size() >= 2) {
                rayCastLeft = casts.getFirst();
                rayCastRight = casts.getLast();
            }
        }
        if(enemyCaster != null) {
            rayCastMiddle = enemyCaster.getPerceptionValues();
        }
        if(enemyFlagCompass != null) {
            compassValue = enemyFlagCompass.getPerceptionValues().getFirst();
        }
        if(territoryCompass != null) {
            isInHomeLand = territoryCompass.getPerceptionValues().getFirst().vector().get(1) == 0;

            if(myself.getFlag().isPresent() || (compassValue != null && compassValue.vector().getLast() == 1.0)) {
                compassValue = territoryCompass.getPerceptionValues().getFirst();
                if(isInHomeLand) compassValue = null;
            }
        }

        // -------------------------------------------------- Actions
        double targetAngle = 0;

        if(compassValue != null) {
            targetAngle = compassValue.vector().getFirst();
        }

        boolean flee = false;
        if(rayCastMiddle != null && myself.getFlag().isEmpty()) {
            PerceptionValue hitCast = null;
            for(PerceptionValue cast : rayCastMiddle) {
                if(cast.type() == PerceptionType.ENEMY && (hitCast == null || cast.vector().get(1) < hitCast.vector().get(1))) {
                    hitCast = cast;
                }
            }
            if(hitCast != null) {
                if(isInHomeLand) {
                    targetAngle = hitCast.vector().getFirst() + 0.000001;
                    flee = true;
                }
                else {
                    targetAngle = -hitCast.vector().getFirst() + 0.000001;
                    flee = true;
                }
            }
        }

        if(rayCastLeft != null && rayCastRight != null && !flee) {
            if(rayCastLeft.type() == PerceptionType.WALL && rayCastRight.type() == PerceptionType.WALL
                    && rayCastLeft.vector().get(1) <= 0.6 && rayCastRight.vector().get(1) <= 0.6) {
                backTrackLeft = 85;
                return new Action(0, -1);
            }
            else if(rayCastLeft.type() == PerceptionType.WALL) targetAngle = rayCastLeft.vector().getLast() - 80;
            else if(rayCastRight.type() == PerceptionType.WALL) targetAngle = rayCastRight.vector().getLast() + 80;
        }

        targetAngle %= 360;
        if(targetAngle < 0) targetAngle += 360;
        targetAngle -= 180;

        var action = new Action(-Math.clamp(targetAngle,-1,1), 1);
        previousAction = action;
        return action;
    }

    private Action getDefenseAction(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects) {
        currentRandomRotation += (engine.getRandom().nextDouble()-0.5) * RANDOM_STRENGTH;
        currentRandomRotation = Math.clamp(currentRandomRotation, -1, 1);
        double targetAngle = currentRandomRotation * 180 + 180;

        if(enemyFlagCompass != null) {
            var compassValue = enemyFlagCompass.getPerceptionValues().getFirst();
            double compassAngle = compassValue.vector().getFirst();
            targetAngle = targetAngle * 0.65 + compassAngle * 0.25;
        }

        if(allyFlagCompass != null) {
            var compassValue = allyFlagCompass.getPerceptionValues().getFirst();
            if(compassValue.vector().get(1) > engine.getFlagSafeZoneRadius() + 2) {
                double compassAngle = compassValue.vector().getFirst();
                double signedAngle = Vector2.fromAngle(targetAngle).signedAngle(Vector2.fromAngle(compassAngle));

                double clampValue = 90;
                signedAngle = Math.clamp(signedAngle, -clampValue, clampValue);
                targetAngle =(targetAngle - signedAngle + 360) % 360;
            }
        }

        if(enemyCaster != null) {
            PerceptionValue hitCast = null;
            for(PerceptionValue cast : enemyCaster.getPerceptionValues()) {
                if(cast.type() == PerceptionType.ENEMY && (hitCast == null || cast.vector().get(1) < hitCast.vector().get(1))) {
                    hitCast = cast;
                }
            }
            if(hitCast != null) {
                targetAngle = hitCast.vector().getFirst() + 0.000001;
            }
        }

        if(allyFlagCompass != null) {
            var compassValue = allyFlagCompass.getPerceptionValues().getFirst();
            if(compassValue.vector().getLast() == 1) {
                targetAngle = compassValue.vector().getFirst() + 0.000001;
            }
        }

        //var action = new Action(-Math.signum(targetAngle), 1);
        //previousAction = action;
        //return action;

        targetAngle %= 360;
        if(targetAngle < 0) targetAngle += 360;
        targetAngle -= 180;

        var action = new Action(-Math.clamp(targetAngle,-1,1), 1);
        previousAction = action;
        return action;
    }

    public void setMyself(Agent a) {
        super.setMyself(a);
        //setting perceptions
        if(enemyFlagCompass != null) enemyFlagCompass.setTeamMode(Filter.TeamMode.ENEMY);
        if(allyFlagCompass != null) allyFlagCompass.setTeamMode(Filter.TeamMode.ALLY);
        if(territoryCompass != null) territoryCompass.setTeamMode(Filter.TeamMode.ALLY);
    }
}
