package ia.perception;

import engine.Vector2;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.Flag;
import engine.object.GameObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PerceptionRaycast extends Perception {
    private record RayHit(Vector2 hit, double normal) {}

    private double[] raySizes;
    private int rayCount;
    private double viewAngle;

    /**
     * Construct a new raycaster
     * @param a The source agent
     * @param raySize The maximum length of rays
     * @param rayCount The number of rays to cast
     * @param viewAngle The field of view, note that if there is >= 3 rays, the 2 most extreme rays are always placed at the exterior. So putting a field of view of 360 will see 2 rays overlap behind the agent
     */
    public PerceptionRaycast(Agent a, double raySize, int rayCount, double viewAngle) {
        super(a);

        var raySizes = new double[rayCount];
        Arrays.fill(raySizes, raySize);

        this.raySizes = raySizes;
        this.rayCount = rayCount;
        this.viewAngle = viewAngle;
    }

    /**
     * Construct a new raycaster
     * @param a The source agent
     * @param raySizes The maximum length of each rays
     * @param rayCount The number of rays to cast
     * @param viewAngle The field of view, note that if there is >= 3 rays, the 2 most extreme rays are always placed at the exterior. So putting a field of view of 360 will see 2 rays overlap behind the agent
     */
    public PerceptionRaycast(Agent a, double[] raySizes, int rayCount, double viewAngle) {
        super(a);

        Objects.requireNonNull(raySizes);
        if(raySizes.length != rayCount) throw new IllegalArgumentException("Raysize array ("+raySizes.length+") is different from raycount ("+rayCount+")");

        this.raySizes = raySizes;
        this.rayCount = rayCount;
        this.viewAngle = viewAngle;
    }

    public double[] getRaySize() {
        return raySizes;
    }

    public int getRayCount() {
        return rayCount;
    }

    public double getViewAngle() {
        return viewAngle;
    }

    /**
     * Return a list of all rays that hit something
     * @param map The map to fire rays in
     * @param agents The agents to fire rays at
     * @param go The objects to fire rays at
     * @return A list of all rays that hit something. the walls, map and objects are independants, meaning that if a ray hit a wall, an object and an agent, it will return 3 hits. Everything is even separated by team too wich can return even more hits
     */
    @Override
    public void updatePerceptionValues(GameMap map, List<Agent> agents, List<GameObject> go) {
        if(rayCount <= 0) return; //If there is no ray, this perception does nothing.

        List<PerceptionValue> rayHits = new ArrayList<>();

        // Draw the ray cone
        double offset = (rayCount < 3) ? viewAngle / (rayCount + 1) : viewAngle / (rayCount - 1);
        int i = (rayCount < 3) ? 1 : 0;
        int drawnRays = 0;

        while (drawnRays < rayCount) {
            var currentOffset = i * offset - viewAngle/2;
            var hit = fireRay(currentOffset, raySizes[drawnRays], map, agents, go);
            rayHits.add(hit);
            i++;
            drawnRays++;
        }

        setPerceptionValues(rayHits);
    }

    private PerceptionValue fireRay(double angle, double size, GameMap map, List<Agent> agents, List<GameObject> go) {
        List<PerceptionValue> rayHits = getAllRayHits(angle, size, map, agents, go);

        // Angle
        double theta = angle;
        if(theta < 0){
            theta = 360 + theta;
        }
        theta = theta % 360;

        // Ray hit to angle & default value
        double finalTheta = theta; // <--- This is only to please the compiler
        return rayHits.stream()
                .map(hit -> {
                    double x = hit.vector().getFirst() - getMy_agent().getCoordinate().x();
                    double y = hit.vector().get(1) - getMy_agent().getCoordinate().y();
                    double distance = Math.sqrt((x * x) + (y * y));
                    if(distance > size) distance = size;

                    // Project the normal angle to the agent POV
                    var normal = hit.vector().getLast() - my_agent.getAngular_position();
                    if(normal < 0) normal += 360;
                    normal %= 360;
                    //var normal = hit.vector().getLast();

                    return new PerceptionValue(
                            hit.type(),
                            List.of(finalTheta, distance/ size, normal)
                    );
                })
                .min(Comparator.comparingDouble(hit -> hit.vector().get(1)))
                .orElse(new PerceptionValue(
                        PerceptionType.EMPTY,
                        List.of(finalTheta, 1.0, 0.0)
                ));
    }

    private List<PerceptionValue> getAllRayHits(double angle, double size, GameMap map, List<Agent> agents, List<GameObject> go) {
        double thisAngle = my_agent.getAngular_position() + angle;
        double angleRadii = Math.toRadians(thisAngle);
        double dirX = Math.cos(angleRadii);
        double dirY = Math.sin(angleRadii);

        Vector2 rayEnd = new Vector2(
                my_agent.getCoordinate().x() + dirX * size,
                my_agent.getCoordinate().y() + dirY * size
        );

        List<PerceptionValue> rayHits = new ArrayList<>();

        // Wall
        RayHit mapCast = wallCast(my_agent.getCoordinate(), rayEnd, map);
        if (mapCast != null) {
            rayHits.add(new PerceptionValue(
                    PerceptionType.WALL,
                    List.of(mapCast.hit.x(), mapCast.hit.y(), mapCast.normal)
            ));
        }

        // Agent
        List<PerceptionValue> agentCasts = agents.stream()
                .filter(Agent::isInGame)
                .map(a -> {
                    var hit = circleCast(my_agent.getCoordinate(), rayEnd, a.getCoordinate(), a.getRadius());
                    if(hit == null) return null;
                    if(a.equals(my_agent)) return null;
                    return new PerceptionValue(
                            (my_agent.getTeam() == a.getTeam()) ? PerceptionType.ALLY : PerceptionType.ENEMY,
                            List.of(hit.hit.x(), hit.hit.y(), hit.normal)
                    );
                })
                .filter(Objects::nonNull)
                .toList();
        rayHits.addAll(agentCasts);

        // Items
        List<PerceptionValue> objectsCasts = go.stream()
                .map(o -> {
                    var hit = circleCast(my_agent.getCoordinate(), rayEnd, o.getCoordinate(), o.getRadius());
                    if(hit == null) return null;
                    if(o instanceof Flag flag && flag.getHolded()) return null;
                    return new PerceptionValue(
                            switch (o) {
                                case Flag flag ->
                                        (my_agent.getTeam() == flag.getTeam()) ? PerceptionType.ALLY_FLAG : PerceptionType.ENEMY_FLAG;
                                default ->
                                        throw new UnsupportedOperationException("Other types than flag are not supported");
                            },
                            List.of(hit.hit.x(), hit.hit.y(), hit.normal)
                    );
                })
                .filter(Objects::nonNull)
                .toList();
        rayHits.addAll(objectsCasts);
        return rayHits;
    }

    private RayHit circleCast(Vector2 start, Vector2 end, Vector2 circleCenter, double radius) {
        // a = D^2
        // b = 2D.(O-C)
        // c = |O-C|^2 - R^2

        // O = start
        // C = circleCenter
        // D
        double dirX = end.x() - start.x();
        double dirY = end.y() - start.y();

        // 0 - C
        double centerToStartX = start.x() - circleCenter.x();
        double centerToStartY = start.y() - circleCenter.y();
        double centerToStartSize = Math.sqrt(Math.pow(centerToStartX, 2) + Math.pow(centerToStartY, 2));

        // a = D^2
        double a = dirX * dirX + dirY * dirY;
        // b = 2D.(O-C)
        double b = ((2*dirX) * centerToStartX + (2*dirY) * centerToStartY);
        // c = |O-C|^2 - R^2
        double c = Math.pow(centerToStartSize, 2) - Math.pow(radius, 2);

        // Solve
        double discriminant = Math.pow(b, 2) - 4 * a * c;

        if (discriminant < 0) {
            // No intersection
            return null;
        }

        // roots
        // (-b +- sqrt(discriminant)) / 2a
        double sqrtDiscriminant = Math.sqrt(discriminant);
        double t1 = (-b - sqrtDiscriminant) / (2 * a);
        double t2 = (-b + sqrtDiscriminant) / (2 * a);

        // 0 is start of segment, 1 is end of segment
        var isRoot1Valid = (t1 >= 0 && t1 <= 1);
        var isRoot2Valid = (t2 >= 0 && t2 <= 1);

        Vector2 hit = null;

        if (isRoot1Valid && !isRoot2Valid) {
            hit = new Vector2(start.x() + t1 * dirX, start.y() + t1 * dirY);
        }
        else if (!isRoot1Valid && isRoot2Valid) {
            hit = new Vector2(start.x() + t2 * dirX, start.y() + t2 * dirY);
        }
        else if (isRoot1Valid && isRoot2Valid) {
            var t = Math.min(t1, t2);
            hit = new Vector2(start.x() + t * dirX, start.y() + t * dirY);
        }

        // No valid intersection points
        if(hit == null) return null;

        // Normal calculation
        double norm_x = hit.x() - circleCenter.x();
        double norm_y = hit.y() - circleCenter.y();
        double length = Math.sqrt(Math.pow(norm_x, 2) + Math.pow(norm_y, 2));
        norm_x /= length;
        norm_y /= length;

        double normal = -Math.toDegrees(Math.atan2(norm_y, norm_x));
        if(normal < 0) normal += 360;

        return new RayHit(
                hit,
                normal
        );
    }


    private enum WallCastNormalDir {
        NONE,
        UP,
        RIGHT,
        DOWN,
        LEFT
    }

    /**
     * An implementation of the DDA algorithm for wall collisions
     * @param start The start of the raycast
     * @param end The end of the raycast
     * @param map The map to check collision against
     * @return The collision coordinate or null if no collision
     */
    private RayHit wallCast(Vector2 start, Vector2 end, GameMap map) {
        var dir = end.subtract(start);
        var norm_dir = dir.normalized();

        //deltaDistX = sqrt(1 + (rayDirY * rayDirY) / (rayDirX * rayDirX))
        //deltaDistY = sqrt(1 + (rayDirX * rayDirX) / (rayDirY * rayDirY))
        var delta_x = Math.sqrt(1 + Math.pow(norm_dir.y(), 2) / Math.pow(norm_dir.x(), 2));
        var delta_y = Math.sqrt(1 + Math.pow(norm_dir.x(), 2) / Math.pow(norm_dir.y(), 2));

        int x = (int)Math.floor(start.x());
        int y = (int)Math.floor(start.y());

        double ray_start_x_frac = (norm_dir.x() > 0) ? (1 - (start.x() - x)) : (start.x() - x);
        double ray_start_y_frac = (norm_dir.y() > 0) ? (1 - (start.y() - y)) : (start.y() - y);
        double current_x_dist = ray_start_x_frac * delta_x;
        double current_y_dist = ray_start_y_frac * delta_y;

        int step_x = norm_dir.x() > 0 ? 1 : -1;
        int step_y = norm_dir.y() > 0 ? 1 : -1;

        double t = 0; // Distance traveled in the ray
        double length = dir.length();
        WallCastNormalDir currentDir = WallCastNormalDir.NONE;
        while (t < length) {
            var cell = map.getCellFromXY(x, y);

            if(cell == null || !cell.isWalkable()) {
                // Compute the intersection point
                double intersection_x = start.x() + t * norm_dir.x();
                double intersection_y = start.y() + t * norm_dir.y();

                return new RayHit(
                        new Vector2(intersection_x, intersection_y),
                        switch (currentDir) {
                            case UP -> 90;
                            case RIGHT -> 180;
                            case DOWN -> 270;
                            case LEFT, NONE -> 0;
                        }
                );
            }

            if(current_x_dist < current_y_dist) {
                x += step_x;
                t = current_x_dist;
                current_x_dist += delta_x;
                currentDir = (step_x > 0) ? WallCastNormalDir.RIGHT : WallCastNormalDir.LEFT;
            }
            else {
                y += step_y;
                t = current_y_dist;
                current_y_dist += delta_y;
                currentDir = (step_y > 0) ? WallCastNormalDir.DOWN : WallCastNormalDir.UP;
            }
        }

        return null;
    }

    public double[] getRaySizes() {
        return raySizes;
    }

    public void setRaySize(double raySize) {
        var raySizes = new double[rayCount];
        Arrays.fill(raySizes, raySize);
        this.raySizes = raySizes;
    }

    public void setRaySizes(double[] raySizes) {
        Objects.requireNonNull(raySizes);
        if(raySizes.length != rayCount) throw new IllegalArgumentException("Raysize array ("+raySizes.length+") is different from raycount ("+rayCount+")");

        this.raySizes = raySizes;
    }


    public void setRayCount(int rayCount) {
        this.rayCount = rayCount;
    }

    public void setViewAngle(double viewAngle) {
        this.viewAngle = viewAngle;
    }

    @Override
    public List<Double> getPerceptionsValuesNormalise() {
        List<PerceptionValue> perceptionsValues = getPerceptionValues();
        List<Double> perceptionsValuesNormalise = new ArrayList<>();
        for (PerceptionValue perceptionValue : perceptionsValues) {
            for (int i = 0; i < PerceptionType.values().length; i++) {
                perceptionsValuesNormalise.add(0.0);
            }
            perceptionsValuesNormalise.set(PerceptionType.values(), 1.0);
        }
        perceptionsValuesNormalise.add(perceptionsValues.get(0)/maxAngle);
        if (perceptionsValues.get(1) > maxDistanceVision)
            perceptionsValuesNormalise.add(0.0);
        else
            perceptionsValuesNormalise.add(perceptionsValues.get(1)/maxDistanceVision);
        //Drapeau pris ou pas (0 ou 1) pas besoin de normaliser
        perceptionsValuesNormalise.add(perceptionsValues.get(2));
        return perceptionsValuesNormalise;
    }

    @Override
    public int getNumberOfPerceptions() {
        return getPerceptionValues().getFirst().vector().size();
    }
}