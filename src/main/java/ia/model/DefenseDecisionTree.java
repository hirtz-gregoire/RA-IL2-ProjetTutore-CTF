package ia.model;

import engine.Engine;
import engine.agent.Action;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;
import ia.perception.*;

import java.util.List;
import java.util.Objects;

public class DefenseDecisionTree extends Model {
    private static final double RANDOM_STRENGTH = 0.7;
    private double currentRandomRotation = 0;

    private FlagCompass enemyFlagCompass;
    private FlagCompass allyFlagCompass;
    private TerritoryCompass territoryCompass;
    private PerceptionRaycast wallCaster;
    private PerceptionRaycast enemyCaster;

    private Action previousAction;

    public DefenseDecisionTree() {
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

        currentRandomRotation += (engine.getRandom().nextDouble()-0.5) * RANDOM_STRENGTH;
        currentRandomRotation = Math.clamp(currentRandomRotation, -1, 1);
        double targetAngle = currentRandomRotation * 180 + 180;

        if(enemyFlagCompass != null) {
            var compassValue = enemyFlagCompass.getPerceptionValues().getFirst();
            double compassAngle = compassValue.vector().getFirst();
            targetAngle = targetAngle * 0.85 + compassAngle * 0.15;
        }

        if(allyFlagCompass != null) {
            var compassValue = allyFlagCompass.getPerceptionValues().getFirst();
            if(compassValue.vector().get(1) > engine.getFlagSafeZoneRadius() + 2) {
                double compassAngle = compassValue.vector().getFirst();
                double signedAngle = targetAngle - compassAngle;

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
