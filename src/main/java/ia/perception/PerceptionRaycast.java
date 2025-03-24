package ia.perception;

import engine.Vector2;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.Flag;
import engine.object.GameObject;

import java.util.*;

public class PerceptionRaycast extends Perception {
    public record RayHit(double angle, double size, double normal) {}

    private double[] raySizes;
    private int rayCount;
    private double viewAngle;
    public static int numberOfPerceptionsValuesNormalise = 5;

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
     */
    @Override
    public void updatePerceptionValues(GameMap map, List<Agent> agents, List<GameObject> go) {
        if(rayCount <= 0) return; //If there is no ray, this perception does nothing.

        List<PerceptionValue> rayHits = new ArrayList<>(rayCount);

        // Draw the ray cone
        double offset = (rayCount == 1) ? viewAngle / (rayCount + 1) : viewAngle / (rayCount - 1);
        int skip = (rayCount == 1) ? 1 : 0;
        int drawnRays = 0;

        for(int i = 0; i < rayCount; i++) {
            var currentOffset = (drawnRays + skip) * offset - viewAngle/2;
            var hit = fireRay(currentOffset, raySizes[drawnRays], map, agents, go);
            rayHits.add(hit);
            drawnRays++;
        }

        setPerceptionValues(rayHits);
    }

    /**
     * Fire a ray at a given angle and length FROM THE AGENT and return the closest hit
     * @param angle The angle to shoot the ray at
     * @param size The length of the ray
     * @param map The map to perform the ray on
     * @param agents The list of agent to shoot the ray at
     * @param go The list of objects to shoot the ray at
     * @return A ray hit containing the type, angle, normalized distance (from 0 to 1 where 1 is farthest) and normal of the hit
     */
    private PerceptionValue fireRay(double angle, double size, GameMap map, List<Agent> agents, List<GameObject> go) {
        // Angle
        double theta = angle;
        if(theta < 0){
            theta = 360 + theta;
        }
        theta = theta % 360;

        // Fire the rays from the agent POV
        PerceptionValue hit = computeHits(my_agent.getAngular_position() + theta, size, map, agents, go);
        if(hit == null) return new PerceptionValue(
                PerceptionType.EMPTY,
                List.of(theta, 1.0, 0.0)
        );

        // Normalize hit size
        double distance = hit.vector().get(1);
        if(distance > size) distance = size; // Clamp dist
        double distanceBySize = distance / size;

        // Project the normal angle to the agent POV
        var normal = hit.vector().getLast() - my_agent.getAngular_position();
        if(normal < 0) normal += 360;
        normal %= 360;

        return new PerceptionValue(
                hit.type(),
                List.of(theta, distanceBySize, normal)
        );
    }

    /**
     * Actually compute the requested raycast, checking hits against everything
     * @param angle The angle to shoot the ray at
     * @param size The length of the ray
     * @param map The map to perform the ray on
     * @param agents The list of agent to shoot the ray at
     * @param go The list of objects to shoot the ray at
     * @return A ray hit containing the type, angle, normalized distance and normal of the hit, RETURN NULL IF NO HIT FOUND
     */
    private PerceptionValue computeHits(double angle, double size, GameMap map, List<Agent> agents, List<GameObject> go) {
        PerceptionValue rayHit = null;

        // Wall
        RayHit mapCast = wallCast(my_agent.getCoordinate(), angle, size, map);
        if (mapCast != null) {
            rayHit = new PerceptionValue(
                    PerceptionType.WALL,
                    List.of(angle, mapCast.size, mapCast.normal)
            );
            size = mapCast.size; // To find the closest object, we slowly reduce the length of the ray
        }

        // Agent
        var myCoord = my_agent.getCoordinate();
        var angleVector = Vector2.fromAngle(angle);

        for (Agent agent : agents) {
            if (!agent.isInGame()) continue;
            if (agent.equals(my_agent)) continue;

            // Angle check
            var coord = agent.getCoordinate();
            if (coord.subtract(myCoord).dot(angleVector) <= 0) continue;

            // Bounding box check
            var agentRadius = agent.getRadius();
            if((coord.x() - (myCoord.x() - size - agentRadius)) < 0) continue;
            if(((myCoord.x() + size + agentRadius) - coord.x()) < 0) continue;
            if((coord.y() - (myCoord.y() - size - agentRadius)) < 0) continue;
            if(((myCoord.y() + size + agentRadius) - coord.y()) < 0) continue;

            var hit = circleCast(myCoord, angle, size, coord, agentRadius);
            if (hit == null) continue;

            rayHit = new PerceptionValue(
                    (my_agent.getTeam() == agent.getTeam()) ? PerceptionType.ALLY : PerceptionType.ENEMY,
                    Arrays.asList(angle, hit.size, hit.normal)
            );
            size = hit.size; // To find the closest object, we slowly reduce the length of the ray
        }

        // Items
        for (GameObject object : go) {
            if(object instanceof Flag flag && flag.getHolded()) continue;

            // Angle check
            var coord = object.getCoordinate();
            if (coord.subtract(myCoord).dot(angleVector) <= 0) continue;

            // Bounding box check
            var objectRadius = object.getRadius();
            if((coord.x() - (myCoord.x() - size - objectRadius)) < 0) continue;
            if(((myCoord.x() + size + objectRadius) - coord.x()) < 0) continue;
            if((coord.y() - (myCoord.y() - size - objectRadius)) < 0) continue;
            if(((myCoord.y() + size + objectRadius) - coord.y()) < 0) continue;

            var hit = circleCast(my_agent.getCoordinate(), angle, size, coord, objectRadius);
            if (hit == null) continue;

            rayHit = new PerceptionValue(
                    switch (object) {
                        case Flag flag ->
                                (my_agent.getTeam() == flag.getTeam()) ? PerceptionType.ALLY_FLAG : PerceptionType.ENEMY_FLAG;
                        default ->
                                throw new UnsupportedOperationException("Other types than flag are not supported");
                    },
                    List.of(angle, hit.size, hit.normal)
            );
            size = hit.size; // To find the closest object, we slowly reduce the length of the ray
        }

        return rayHit;
    }

    /**
     * Solve the circle equation to get circle raycast hits
     * @param start The start of the raycast
     * @param angle The angle of the ray
     * @param size The length of the ray
     * @param circleCenter The center of the circle to check the ray against
     * @param radius The radius of the circle to check the ray against
     * @return The collision coordinate or null if no collision
     */
    public static RayHit circleCast(Vector2 start, double angle, double size, Vector2 circleCenter, double radius) {
        // a = D^2
        // b = 2D.(O-C)
        // c = |O-C|^2 - R^2

        // O = start
        // C = circleCenter
        // D
        double angleRadii = Math.toRadians(angle);
        double dirX = Math.cos(angleRadii) * size;
        double dirY = Math.sin(angleRadii) * size;

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

        double t = 0;
        if (isRoot1Valid && !isRoot2Valid) {
            t = t1;
        }
        else if (!isRoot1Valid && isRoot2Valid) {
            t = t2;
        }
        else if (isRoot1Valid && isRoot2Valid) {
            t = Math.min(t1, t2);
        }

        // No valid intersection points
        if(!isRoot1Valid && !isRoot2Valid) return null;

        // Normal calculation
        double norm_x = (start.x() + t * dirX) - circleCenter.x();
        double norm_y = (start.y() + t * dirY) - circleCenter.y();
        double length = Math.sqrt(Math.pow(norm_x, 2) + Math.pow(norm_y, 2));
        norm_x /= length;
        norm_y /= length;

        double normal = -Math.toDegrees(Math.atan2(norm_y, norm_x));
        if(normal < 0) normal += 360;

        return new RayHit(
                angle,
                t*size,
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
     * @param angle The angle of the ray
     * @param size The length of the ray
     * @param map The map to check collision against
     * @return The collision coordinate or null if no collision
     */
    public static RayHit wallCast(Vector2 start, double angle, double size, GameMap map) {
        double angleRadii = Math.toRadians(angle);
        double dirX = Math.cos(angleRadii);
        double dirY = Math.sin(angleRadii);
        var norm_dir = new Vector2(dirX, dirY).normalized();

        var delta_x = Math.sqrt(1 + Math.pow(norm_dir.y(), 2) / Math.pow(norm_dir.x(), 2));
        var delta_y = Math.sqrt(1 + Math.pow(norm_dir.x(), 2) / Math.pow(norm_dir.y(), 2));

        int x = (int)Math.floor(start.x());
        int y = (int)Math.floor(start.y());

        double ray_start_x_frac = (norm_dir.x() > 0) ? (1 - (start.x() - x)) : (start.x() - x);
        double ray_start_y_frac = (norm_dir.y() > 0) ? (1 - (start.y() - y)) : (start.y() - y);
        double current_x_dist = ray_start_x_frac * delta_x;
        double current_y_dist = ray_start_y_frac * delta_y;

        boolean isPerfectlyDiagonalCast = Math.abs(current_x_dist - current_y_dist) < 1e-6;

        int step_x = norm_dir.x() > 0 ? 1 : -1;
        int step_y = norm_dir.y() > 0 ? 1 : -1;

        double t = 0; // Distance traveled in the ray
        WallCastNormalDir currentDir = WallCastNormalDir.NONE;
        while (t < size) {
            var cell = map.getCellFromXY(x, y);

            if(cell == null || !cell.isWalkable()) {
                // Compute the intersection point
                return new RayHit(
                        angle,
                        t,
                        switch (currentDir) {
                            case UP -> 90;
                            case RIGHT -> 180;
                            case DOWN -> 270;
                            case LEFT, NONE -> 0;
                        }
                );
            }

            // When the ray is moving diagonally, it could miss walls
            if (isPerfectlyDiagonalCast) {
                x += step_x;
                y += step_y;
                t = current_x_dist;
                current_x_dist += delta_x;
                current_y_dist += delta_y;

                // Check both walls so that the ray does not go through the gap
                if(t < size) {
                    currentDir = (step_x > 0) ? WallCastNormalDir.RIGHT : WallCastNormalDir.LEFT;
                    cell = map.getCellFromXY(x, y - step_y);
                    if(cell == null || !cell.isWalkable()) {
                        // Compute the intersection point
                        return new RayHit(
                                angle,
                                t,
                                switch (currentDir) {
                                    case RIGHT -> 180;
                                    case LEFT -> 0;
                                    default -> throw new IllegalStateException("Unexpected value: " + currentDir);
                                }
                        );
                    }

                    currentDir = (step_y > 0) ? WallCastNormalDir.DOWN : WallCastNormalDir.UP;
                    cell = map.getCellFromXY(x - step_x, y);
                    if(cell == null || !cell.isWalkable()) {
                        // Compute the intersection point
                        return new RayHit(
                                angle,
                                t,
                                switch (currentDir) {
                                    case UP -> 90;
                                    case DOWN -> 270;
                                    default -> throw new IllegalStateException("Unexpected value: " + currentDir);
                                }
                        );
                    }
                }
            }
            else if(current_x_dist < current_y_dist) {
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
    public double[] getPerceptionsValuesNormalise() {
        List<PerceptionValue> perceptionsValues = getPerceptionValues();
        List<Double> perceptionsValuesNormalise = new ArrayList<>();
        for (PerceptionValue perceptionValue : perceptionsValues) {
            double isWall = perceptionValue.type() == PerceptionType.WALL ? 1 : 0;
            double allyOrEnemy = perceptionValue.type() == PerceptionType.ALLY ? 1
                    : perceptionValue.type()==PerceptionType.ENEMY ? -1 : 0;

            double allyFlagOrEnemy = perceptionValue.type() == PerceptionType.ALLY_FLAG ? 1
                    : perceptionValue.type() == PerceptionType.ENEMY_FLAG ? -1 : 0;

            // Hit type
            perceptionsValuesNormalise.add(isWall);
            perceptionsValuesNormalise.add(allyOrEnemy);
            perceptionsValuesNormalise.add(allyFlagOrEnemy);
            // Object distance
            perceptionsValuesNormalise.add(perceptionValue.vector().get(1));
            // Normal angle
            var radiiAngle = perceptionValue.vector().get(2);
            perceptionsValuesNormalise.add(normaliseIn180ToMinus180(radiiAngle));

        }

        double[] res = new double[perceptionsValuesNormalise.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = perceptionsValuesNormalise.get(i);
        }
        return res;
    }

    @Override
    public int getNumberOfPerceptionsValuesNormalise() {
        int nbRays = rayCount;
        return numberOfPerceptionsValuesNormalise * nbRays;
    }
}