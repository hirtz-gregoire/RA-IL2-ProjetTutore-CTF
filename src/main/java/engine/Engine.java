package engine;

import display.Display;
import engine.agent.Action;
import engine.agent.Agent;
import engine.map.Cell;
import engine.map.GameMap;
import engine.map.SpawningCell;
import engine.object.Flag;
import engine.object.GameObject;
import javafx.application.Platform;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Engine {
    private final List<Agent> agents;
    private final GameMap map;
    private final List<GameObject> objects;
    private final Display display;
    private GameClock clock;
    private int respawnTime;
    private double flagSafeZoneRadius;
    private boolean runAsFastAsPossible = false;
    private final AtomicBoolean isRendering = new AtomicBoolean(false);
    private final Map<Team, Boolean> isTeamAlive = new HashMap<>();
    private final Map<Team, Integer> points = new HashMap<>();

    public final int DEFAULT_TPS = 60;

    private double tps = DEFAULT_TPS;
    private int actualTps = 0;
    private double lastTpsUpdate = 0;

    /**
     * Create an engine with a display
     * @param agents List of agents to simulate, automatically spawned at the right position
     * @param map The map to play on
     * @param objects List of objects to play with, like flags, their position is not automatic
     * @param display The display to use to display the game (can be null for no display)
     * @param respawnTime The desired respawn time (in seconds)
     */
    public Engine(List<Agent> agents, GameMap map, List<GameObject> objects, Display display, double respawnTime, double flagSafeZoneRadius) {
        this.agents = agents;
        this.map = map;
        this.objects = objects;
        this.display = display;
        this.respawnTime = (int)Math.floor(respawnTime * DEFAULT_TPS);
        this.flagSafeZoneRadius = flagSafeZoneRadius;
    }

    /**
     * Create an engine without a display
     * @param agents List of agents to simulate, automatically spawned at the right position
     * @param map The map to play on
     * @param objects List of objects to play with, like flags, their position is not automatic
     * @param respawnTime The desired respawn time (in seconds)
     */
    public Engine(List<Agent> agents, GameMap map, List<GameObject> objects, double respawnTime, double flagSafeZoneRadius) {
        this.agents = agents;
        this.map = map;
        this.objects = objects;
        this.display = null;
        this.respawnTime = (int)Math.floor(respawnTime * DEFAULT_TPS);
        this.flagSafeZoneRadius = flagSafeZoneRadius;
        runAsFastAsPossible = true;
    }

    /**
     * Start the game
     */
    public void run() {
        // Set up the status of teams
        for(Agent agent : agents) {
            isTeamAlive.put(agent.getTeam(), true);
            points.put(agent.getTeam(), 0);
        }

        clock = new GameClock();
        double prevUpdate = -1;
        int updateCount = 0;
        lastTpsUpdate = 0;

        while (true) {
            double time = clock.millis();
            // We only work in turns to ease the game-saving process
            if(tps <= 0) continue;
            if(!runAsFastAsPossible && time - prevUpdate < 1000.0 / tps) continue;

            // Update the TPS estimation every 30 updates
            if(updateCount == 100) {
                var delta = time - lastTpsUpdate;
                actualTps = (int)(100.0 / (delta/1000.0));

                updateCount = 0;
                lastTpsUpdate = time;
            }

            prevUpdate = clock.millis();
            updateCount++;
            next();

            if(isGameFinished()) break;
        }
    }

    /**
     * Compute the next turn of simulation
     */
    public void next() {
        // Spawn agents
        spawnAgents();

        // Actions
        var actions = fetchActions();
        var agentsToUpdate = new LinkedList<>(actions.keySet());
        Collections.shuffle(agentsToUpdate);

        while (!agentsToUpdate.isEmpty()) {
            var agent = agentsToUpdate.removeFirst();
            var action = actions.get(agent);
            executeAction(agent, action);
        }

        // Check if we have a display and if the display is available
        if(display != null && !runAsFastAsPossible) {
            if (isRendering.compareAndSet(false, true)) {
                Platform.runLater(() -> {
                    display.update(this, map, agents, objects);
                    isRendering.set(false);
                });
            }
        }
    }

    /**
     * Method to update the status of teams : a team with no flag should not be able to play
     */
    private void updateAliveTeams() {
        isTeamAlive.replaceAll((t, v) -> false);

        for(GameObject object : objects) {
            if(object instanceof Flag flag) {
                isTeamAlive.put(flag.getTeam(), true);
            }
        }

        for(Agent agent : agents) {
            if(isTeamAlive.get(agent.getTeam())) continue;
            agent.setInGame(false);
        }
    }

    /**
     * Method that say if the game is finished or not
     * @return true if game is finished (a team has captured all enemy flags)
     */
    private boolean isGameFinished() {
        Team t = null;
        boolean firstFlag = true;
        for(GameObject ob : this.objects){
            if (ob instanceof Flag fActual) {
                if(firstFlag){
                    t = fActual.getTeam();
                    firstFlag = false;
                }else{
                    if(t != fActual.getTeam()){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Method for spawning agents
     */
    private void spawnAgents() {
        // Prepare all the spawning cells, we don't want multiple units spawning on
        // the same cell
        var spawningCells = map.getSpawningCells();
        Collections.shuffle(spawningCells);
        Map<SpawningCell, Boolean> spawningCellsUsage = new HashMap<>();
        for(var spawningCell : spawningCells) {
            spawningCellsUsage.put(spawningCell, false);
        }

        for(Agent agent : agents) {
            if(agent.isInGame()) continue;
            if(!isTeamAlive.get(agent.getTeam())) continue;

            agent.setRespawnTimer(agent.getRespawnTimer() - 1);

            if(agent.getRespawnTimer() > 0) continue;

            int i = 0;
            boolean spawned = false;
            while(i < spawningCells.size() && !spawned) {
                if(!spawningCellsUsage.get(spawningCells.get(i)) && spawningCells.get(i).getTeam() == agent.getTeam()) {
                    agent.setCoordinate(new Coordinate(spawningCells.get(i).getCoordinate().x()+0.5, spawningCells.get(i).getCoordinate().y()+0.5));
                    agent.setInGame(true);
                    spawningCellsUsage.put(spawningCells.get(i), true);
                    spawned = true;
                }
                i++;
            }
        }
    }

    /**
     * Method that get actions of all agents
     * @return a Map with an Agent associated with an Action
     */
    private Map<Agent, Action> fetchActions() {
        return this.agents.stream()
                .parallel()
                .filter(Agent::isInGame)
                .collect(Collectors.toMap(
                        agent -> agent,
                        agent -> agent.getAction(this.map,this.agents,this.objects)
                ));
    }

    /**
     * Execute the action of an agent
     * @param agent The agent we want to execute the action to
     * @param action The action we want to agent to perform
     */
    private void executeAction(Agent agent, Action action) {
        //Calculate Actual angle in degrees based on Previous angle and actual Action
        double rotationSpeed = agent.getRotateSpeed() / DEFAULT_TPS; // The rotation speed is given in degree per seconds
        double prev_angle = agent.getAngular_position();
        double new_angle = (prev_angle + (action.getRotationRatio() * rotationSpeed)) % 360;
        if (new_angle < 0) {
            new_angle += 360;
        }

        agent.setAngular_position(new_angle);
        double angle_in_radians = Math.toRadians(new_angle);

        //calculate new position of the Agent
        double speed = action.getSpeedRatio() * ((action.getSpeedRatio() >= 0) ? agent.getSpeed() : agent.getBackSpeed());
        speed /= DEFAULT_TPS; // The rotation speed is given in meter per seconds
        double dx = speed * Math.cos(angle_in_radians);
        double dy = speed * Math.sin(angle_in_radians);

        Coordinate currentCoordinate = agent.getCoordinate();
        double x_t = currentCoordinate.x() + dx;
        double y_t = currentCoordinate.y() + dy;
        agent.setCoordinate(new Coordinate(x_t,y_t));
        collisions(agent);

        // Destroy the flag and give a point when the flag is captured
        computeFlagCapture(agent);
    }

    /**
     * Check if the agent have brought the flag to his territory
     * @param agent an agent that may have captured a flag
     */
    private void computeFlagCapture(Agent agent) {
        if(agent.getFlag().isEmpty()) return;

        boolean onOwnTerritory = map.getCells()
                .get((int)Math.floor(agent.getCoordinate().x()))
                .get((int)Math.floor(agent.getCoordinate().y()))
                .getTeam() == agent.getTeam();

        if(!onOwnTerritory) return;

        points.put(agent.getTeam(), points.get(agent.getTeam()) + 1);
        objects.remove(agent.getFlag().get());
        agent.setFlag(Optional.empty());
        updateAliveTeams();
    }
    /**
     * check all possible collision with a specific agent
     * @param agent a specific agent
     */
    private void collisions(Agent agent) {
        // Out of bounds
        agent.setCoordinate(new Coordinate(
                Math.min(Math.max(agent.getCoordinate().x(), 0), map.getCells().size() - 0.1f),
                Math.min(Math.max(agent.getCoordinate().y(), 0), map.getCells().getFirst().size() - 0.1f)
        ));

        // Players collision
        for(Agent other : agents) {
            if(other.equals(agent) || !agent.isInGame() || !other.isInGame()) continue;
            checkAgentCollision(agent, other);
        }

        // Flag safe zone
        for(GameObject object : objects){
            if(object instanceof Flag flag) {
                if(flag.getHolded()) continue;
                handleFlagSafeZone(agent, flag);
            }
        }

        // Wall collision
        for(List<Cell> cells : map.getCells()) {
            for(Cell cell : cells) {
                checkWallCollision(cell, agent);
            }
        }

        // Item Collision
        for(GameObject object : objects){
            checkItemCollision(agent, object);
        }

        // Move the flag to us
        if(agent.getFlag().isPresent()){
            agent.getFlag().get().setCoordinate(new Coordinate(agent.getCoordinate().x(), agent.getCoordinate().y()));
        }
    }

    /**
     * Compute and apply the collision between two agents
     * @param agent A particular agent check collision with another agent
     * @param other Another agent check collision with a specific agent
     */
    private void checkAgentCollision(Agent agent, Agent other) {
        // Distance between the two agents
        double squaredDistX = Math.pow(agent.getCoordinate().x() - other.getCoordinate().x(), 2);
        double squaredDistY = Math.pow(agent.getCoordinate().y() - other.getCoordinate().y(), 2);
        double collisionDistance = Math.sqrt(squaredDistX + squaredDistY);

        // END THE METHOD IF NO COLLISIONS
        double radius = agent.getRadius() + other.getRadius();
        if(collisionDistance >= radius) return;

        // Maybe we get a kill...
        if(agent.getTeam() != other.getTeam()) {
            boolean agentIsSafe = map.getCells()
                    .get((int)Math.floor(agent.getCoordinate().x()))
                    .get((int)Math.floor(agent.getCoordinate().y()))
                    .getTeam() == agent.getTeam();

            boolean otherIsSafe = map.getCells()
                    .get((int)Math.floor(other.getCoordinate().x()))
                    .get((int)Math.floor(other.getCoordinate().y()))
                    .getTeam() == other.getTeam();
            if(!agentIsSafe) {
                agent.setInGame(false);
                agent.setRespawnTimer(respawnTime);
                if (agent.getFlag().isPresent()){
                    agent.getFlag().get().setHolded(false);
                    agent.setFlag(Optional.empty());
                }
            }

            if(!otherIsSafe) {
                other.setInGame(false);
                other.setRespawnTimer(respawnTime);
                if (other.getFlag().isPresent()){
                    other.getFlag().get().setHolded(false);
                    other.setFlag(Optional.empty());
                }
            }

            // kill -> no collision
            if(!agentIsSafe || !otherIsSafe) return;
        }

        // Same team OR both in their own territory -> push
        double overlap = radius - collisionDistance;
        Coordinate pushVector = getUnidirectionalPush(
                other.getCoordinate(),
                agent.getCoordinate(),
                overlap
        );
        agent.setCoordinate(new Coordinate(
                agent.getCoordinate().x() + pushVector.x()/2,
                agent.getCoordinate().y() + pushVector.y()/2
        ));
        other.setCoordinate(new Coordinate(
                other.getCoordinate().x() - pushVector.x()/2,
                other.getCoordinate().y() - pushVector.y()/2
        ));
    }

    /**
     * Compute and apply the collision of a given cell
     * @param cell a specific cell of the map
     * @param agent a specific agent
     */
    private void checkWallCollision(Cell cell, Agent agent) {
        if(cell.isWalkable()) return;

        // Closest point of the cell from the agent
        double closestX = Math.clamp(agent.getCoordinate().x(), cell.getCoordinate().x(), cell.getCoordinate().x() + 1);
        double closestY = Math.clamp(agent.getCoordinate().y(), cell.getCoordinate().y(), cell.getCoordinate().y() + 1);

        // Distance between the point and the agent
        double squaredDistX = Math.pow(agent.getCoordinate().x() - closestX, 2);
        double squaredDistY = Math.pow(agent.getCoordinate().y() - closestY, 2);
        double collisionDistance = Math.sqrt(squaredDistX + squaredDistY);

        // END THE METHOD IF NO COLLISIONS
        if(collisionDistance >= agent.getRadius()) return;

        // Push logic
        double overlap = agent.getRadius() - collisionDistance;
        Coordinate pushVector = getUnidirectionalPush(
                new Coordinate(cell.getCoordinate().x() + 0.5, cell.getCoordinate().y() + 0.5),
                agent.getCoordinate(),
                overlap
                );
        agent.setCoordinate(new Coordinate(
                agent.getCoordinate().x() + pushVector.x(),
                agent.getCoordinate().y() + pushVector.y()
        ));
    }

    private void handleFlagSafeZone(Agent agent, Flag flag) {
        // Distance between the two agents
        double squaredDistX = Math.pow(agent.getCoordinate().x() - flag.getCoordinate().x(), 2);
        double squaredDistY = Math.pow(agent.getCoordinate().y() - flag.getCoordinate().y(), 2);
        double collisionDistance = Math.sqrt(squaredDistX + squaredDistY);

        // END THE METHOD IF NO COLLISIONS
        double radius = agent.getRadius() + flagSafeZoneRadius;
        if(collisionDistance >= radius) return;

        // Push logic
        double overlap = radius - collisionDistance;
        Coordinate pushVector = getUnidirectionalPush(
                new Coordinate(flag.getCoordinate().x(), flag.getCoordinate().y()),
                agent.getCoordinate(),
                overlap
        );
        agent.setCoordinate(new Coordinate(
                agent.getCoordinate().x() + pushVector.x(),
                agent.getCoordinate().y() + pushVector.y()
        ));
    }

    /**
     * Method for checking collisions between
     * @param agent Agent that we check collision with an object
     * @param object GameObject that we check collision with an agent
     */
    private void checkItemCollision(Agent agent, GameObject object){
        // Distance between the agent and the object
        double distX = Math.pow(agent.getCoordinate().x() - object.getCoordinate().x(), 2);
        double distY = Math.pow(agent.getCoordinate().y() - object.getCoordinate().y(), 2);
        double distCollision = Math.sqrt(distX+distY);

        // END THE METHOD IF NO COLLISIONS
        double radius = agent.getRadius() + object.getRadius();
        if(distCollision >= radius) return;

        //switch with a different behavior for each GameObject existing
        switch (object) {
            case Flag flag -> {
                if (agent.getTeam() == flag.getTeam()) {
                    return;
                }

                if(!agent.isInGame()) {
                    return;
                }

                if(flag.getHolded() || agent.getFlag().isPresent()) {
                    return;
                }
                flag.setHolded(true);
                agent.setFlag(Optional.of(flag));
            }
            default -> {
                //You shouldn't be here. Neither should you.
            }
        }

    }

    /**
     * Method for computing the repulsion vector that starts from a static object to another object
     * @param staticObject The position of the non-movable object
     * @param thingToPush The position of the object that will be pushed away
     * @param overlap The amount of overlap between the two objects
     * @return A vector describing the distance to move the thingToPush object to get rid of the overlap
     */

    private Coordinate getUnidirectionalPush(Coordinate staticObject, Coordinate thingToPush, double overlap) {
        double offsetX = thingToPush.x() - staticObject.x();
        double offsetY = thingToPush.y() - staticObject.y();
        double offsetMagnitude = Math.sqrt(offsetX * offsetX + offsetY * offsetY);
        double pushDirX = (offsetMagnitude != 0) ? offsetX/offsetMagnitude : 1;
        double pushDirY = (offsetMagnitude != 0) ? offsetY/offsetMagnitude : 0;
        return new Coordinate(pushDirX * overlap, pushDirY * overlap);
    }

    public GameClock getClock() {return clock;}
    public boolean isRunAsFastAsPossible() {return runAsFastAsPossible;}
    public Map<Team, Integer> getPoints() {return points;}
    public int getActualTps() {return actualTps;}
    public double getTps() {return tps;}
    public void setRunAsFastAsPossible(boolean runAsFastAsPossible) {this.runAsFastAsPossible = runAsFastAsPossible;}
    public void setRespawnTime(int respawnTime) {this.respawnTime = respawnTime;}
    public void setTps(int tps) {this.tps = tps;}

    public double getFlagSafeZoneRadius() {
        return flagSafeZoneRadius;
    }
}
