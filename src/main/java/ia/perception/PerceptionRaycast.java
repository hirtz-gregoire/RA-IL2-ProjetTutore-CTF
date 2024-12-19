package ia.perception;

import engine.Coordinate;
import engine.Team;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.Flag;
import engine.object.GameObject;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.atan2;

public class PerceptionRaycast extends Perception {

    private double raySize;
    private int rayCount;
    private double viewAngle;

    /**
     * Construct a new raycaster
     * @param a the source agent
     * @param raySize The maximum length of rays
     * @param rayCount The number of rays to cast
     * @param viewAngle The field of view, note that if there is >= 3 rays, the 2 most extreme rays are always placed at the exterior. So putting a field of view of 360 will see 2 rays overlap behind the agent
     */
    public PerceptionRaycast(Agent a, double raySize, int rayCount, double viewAngle) {
        super(a);
        this.raySize = raySize;
        this.rayCount = rayCount;
        this.viewAngle = viewAngle;
    }

    /**
     * Return a list of all rays that hit something
     * @param map The map to fire rays in
     * @param agents The agents to fire rays at
     * @param go The objects to fire rays at
     * @return A list of all rays that hit something. the walls, map and objects are independants, meaning that if a ray hit a wall, an object and an agent, it will return 3 hits. Everything is even separated by team too wich can return even more hits
     */
    @Override
    public List<PerceptionValue> getValue(GameMap map, List<Agent> agents, List<GameObject> go) {
        if(rayCount <= 0) return Collections.emptyList();

        List<PerceptionValue> rayHits = new ArrayList<>();

        // Draw the ray cone
        double offset = (rayCount < 3) ? viewAngle / (rayCount + 1) : viewAngle / (rayCount - 1);
        int i = (rayCount < 3) ? 1 : 0;
        int drawnRays = 0;

        while (drawnRays < rayCount) {
            var currentOffset = i * offset - viewAngle/2;
            var currentHits = fireRay(currentOffset, map, agents, go);
            rayHits.addAll(currentHits);
            i++;
            drawnRays++;
        }

        return rayHits;
    }

    private List<PerceptionValue> fireRay(double angle, GameMap map, List<Agent> agents, List<GameObject> go) {
        List<PerceptionValue> rayHits = getAllRayHits(angle, map, agents, go);

        // Ray hit to angle
        return rayHits.stream()
                .map(hit -> {
                    double x = hit.vector().getFirst() - getMy_agent().getCoordinate().x();
                    double y = hit.vector().getLast() - getMy_agent().getCoordinate().y();
                    double distance = Math.sqrt((x * x) + (y * y));

                    //normalized x and y
                    double norm_x = x/distance;
                    double norm_y = y/distance;

                    //theta
                    double theta = Math.toDegrees(atan2(norm_y,norm_x));
                    if(theta < 0){
                        theta = 360 + theta;
                    }
                    theta = theta % 360;

                    return new PerceptionValue(
                            hit.type(),
                            List.of(theta, distance/getMy_agent().getSpeed())
                    );
                })
                .collect(Collectors.toList());
    }

    private List<PerceptionValue> getAllRayHits(double angle, GameMap map, List<Agent> agents, List<GameObject> go) {
        double thisAngle = my_agent.getAngular_position() + angle;
        double angleRadii = Math.toRadians(thisAngle);
        double dirX = Math.cos(angleRadii);
        double dirY = Math.sin(angleRadii);

        Coordinate rayEnd = new Coordinate(
                my_agent.getCoordinate().x() + dirX * raySize,
                my_agent.getCoordinate().y() + dirY * raySize
        );

        List<PerceptionValue> rayHits = new ArrayList<>();

        // Wall
        Coordinate mapCast = wallCast(my_agent.getCoordinate(), rayEnd, map);
        if (mapCast != null) {
            rayHits.add(new PerceptionValue(
                    PerceptionType.WALL,
                    List.of(mapCast.x(), mapCast.y())
            ));
        }

        // Agent
        List<PerceptionValue> agentCasts = agents.stream()
                .map(a -> {
                    var cast = circleCast(my_agent.getCoordinate(), rayEnd, a.getCoordinate(), a.getRadius());
                    if(cast == null) return null;
                    if(a.equals(my_agent)) return null;
                    return new PerceptionValue(
                            (my_agent.getTeam() == a.getTeam()) ? PerceptionType.ALLY : PerceptionType.ENEMY,
                            List.of(cast.x(), cast.y())
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        // Add closest agent
        Optional<PerceptionValue> closestAlly = agentCasts.stream()
                .filter(p -> p.type() == PerceptionType.ALLY)
                .min(Comparator.comparingDouble(p -> Math.sqrt(Math.pow(p.vector().getFirst() - my_agent.getCoordinate().x(), 2) + Math.pow(p.vector().getLast() - my_agent.getCoordinate().y(), 2))));

        Optional<PerceptionValue> closestEnemy = agentCasts.stream()
                .filter(p -> p.type() == PerceptionType.ENEMY)
                .min(Comparator.comparingDouble(p -> Math.sqrt(Math.pow(p.vector().getFirst() - my_agent.getCoordinate().x(), 2) + Math.pow(p.vector().getLast() - my_agent.getCoordinate().y(), 2))));
        closestAlly.ifPresent(rayHits::add);
        closestEnemy.ifPresent(rayHits::add);

        // Items
        List<PerceptionValue> objectsCasts = go.stream()
                .map(o -> {
                    var cast = circleCast(my_agent.getCoordinate(), rayEnd, o.getCoordinate(), o.getRadius());
                    if(cast == null) return null;
                    return new PerceptionValue(
                            switch (o) {
                                case Flag flag ->
                                        (my_agent.getTeam() == flag.getTeam()) ? PerceptionType.ALLY_FLAG : PerceptionType.ENEMY_FLAG;
                                default ->
                                    throw new UnsupportedOperationException("Other types than flag are not supported");
                            },
                            List.of(cast.x(), cast.y())
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        // Add closest item
        PerceptionValue closestItem = null;
        double smallestDistance = Double.MAX_VALUE;
        for(PerceptionValue item : objectsCasts) {
            var dist = Math.sqrt(Math.pow(item.vector().getFirst() - my_agent.getCoordinate().x(), 2) + Math.pow(item.vector().getLast() - my_agent.getCoordinate().y(), 2));
            if(dist < smallestDistance) {
                smallestDistance = dist;
                closestItem = item;
            }
        }
        if(closestItem != null) {
            rayHits.add(closestItem);
        }

        return rayHits;
    }

    private Coordinate circleCast(Coordinate start, Coordinate end, Coordinate circleCenter, double radius) {
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

        if (isRoot1Valid && !isRoot2Valid) {
            return new Coordinate(start.x() + t1 * dirX, start.y() + t1 * dirY);
        }
        else if (!isRoot1Valid && isRoot2Valid) {
            return new Coordinate(start.x() + t2 * dirX, start.y() + t2 * dirY);
        }
        else if (isRoot1Valid && isRoot2Valid) {
            var t = Math.min(t1, t2);
            return new Coordinate(start.x() + t * dirX, start.y() + t * dirY);
        }

        // No valid intersection points
        return null;
    }

    /**
     * An implementation of the DDA algorithm for wall collisions
     * @param start The start of the raycast
     * @param end The end of the raycast
     * @param map The map to check collision against
     * @return The collision coordinate or null if no collision
     */
    private Coordinate wallCast(Coordinate start, Coordinate end, GameMap map) {
        var dir_x = end.x() - start.x();
        var dir_y = end.y() - start.y();
        var dist = Math.sqrt(Math.pow(dir_x, 2) + Math.pow(dir_y, 2));
        dir_x /= dist;
        dir_y /= dist;

        //deltaDistX = sqrt(1 + (rayDirY * rayDirY) / (rayDirX * rayDirX))
        //deltaDistY = sqrt(1 + (rayDirX * rayDirX) / (rayDirY * rayDirY))
        var delta_x = Math.sqrt(1 + Math.pow(dir_y, 2) / Math.pow(dir_x, 2));
        var delta_y = Math.sqrt(1 + Math.pow(dir_x, 2) / Math.pow(dir_y, 2));

        int clamped_start_x = (int)Math.floor(start.x());
        int clamped_start_y = (int)Math.floor(start.y());
        int clamped_end_x = (int)Math.floor(end.x());
        int clamped_end_y = (int)Math.floor(end.y());

        double ray_start_x_frac = (dir_x > 0) ? (1 - (start.x() - clamped_start_x)) : (start.x() - clamped_start_x);
        double ray_start_y_frac = (dir_y > 0) ? (1 - (start.y() - clamped_start_y)) : (start.y() - clamped_start_y);
        double current_x_dist = ray_start_x_frac * delta_x;
        double current_y_dist = ray_start_y_frac * delta_y;
        int x = clamped_start_x;
        int y = clamped_start_y;

        int step_x = dir_x > 0 ? 1 : -1;
        int step_y = dir_y > 0 ? 1 : -1;

        while (x != clamped_end_x || y != clamped_end_y) {

            if(!map.getCellFromXY(x, y).isWalkable()) {
                // We extrapolate and say that the collision is in the middle of the wall
                return new Coordinate(x+0.5, y+0.5);
            }

            if(current_x_dist < current_y_dist) {
                x += step_x;
                current_x_dist += delta_x;
            }
            else {
                y += step_y;
                current_y_dist += delta_y;
            }
        }

        return null;
    }
}
