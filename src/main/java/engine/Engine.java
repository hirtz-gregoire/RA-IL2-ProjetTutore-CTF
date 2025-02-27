package engine;

import display.Display;
import engine.agent.Action;
import engine.agent.Agent;
import engine.map.Cell;
import engine.map.GameMap;
import engine.map.SpawningCell;
import engine.object.Flag;
import engine.object.GameObject;
import ia.evaluationFunctions.DistanceEval;
import ia.evaluationFunctions.EvaluationFunction;
import javafx.application.Platform;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Engine {
    private final int nbEquipes;
    private final Random random = new Random();
    private final List<Agent> agents;
    private final GameMap map;
    private final List<GameObject> objects;
    private final Display display;
    private final EvaluationFunction evaluationFunction;
    private GameClock clock;
    private int respawnTime;
    private double flagSafeZoneRadius;
    private boolean runAsFastAsPossible = false;
    private final AtomicBoolean isRendering = new AtomicBoolean(false);
    private final Map<Team, Boolean> isTeamAlive = new HashMap<>();
    private final Map<Team, Integer> points = new HashMap<>();
    private volatile boolean running = true;
    private int limit_turn;
    public static final int INFINITE_TURN = -666;

    public static final int DEFAULT_TPS = 60;
    private double tps = DEFAULT_TPS;
    private int actualTps = 0;
    private double lastTpsUpdate = 0;
    private int safeZoneTime = 5 * DEFAULT_TPS;
    /**
     * Create an engine with a display
     *
     * @param agents      List of agents to simulate, automatically spawned at the right position
     * @param map         The map to play on
     * @param objects     List of objects to play with, like flags, their position is not automatic
     * @param display     The display to use to display the game (can be null for no display)
     * @param respawnTime The desired respawn time (in seconds)
     * @param seed         The seed for all things that will be randomized
     */
    public Engine(int nbEquipes, List<Agent> agents, GameMap map, List<GameObject> objects, Display display, double respawnTime, double flagSafeZoneRadius, Long seed, int maxTurns) {
        this.agents = agents;
        this.nbEquipes = nbEquipes;
        this.map = map;
        this.objects = objects;
        this.display = display;
        this.limit_turn = maxTurns;
        //Computing respawnTime in turn
        this.respawnTime = (int)Math.floor(respawnTime * DEFAULT_TPS);
        this.flagSafeZoneRadius = flagSafeZoneRadius;
        this.random.setSeed(seed);
        this.evaluationFunction = null;
    }

    /**
     * Create an engine without a display but with an eval function
     * @param agents List of agents to simulate, automatically spawned at the right position
     * @param map The map to play on
     * @param objects List of objects to play with, like flags, their position is not automatic
     * @param respawnTime The desired respawn time (in seconds)
     */
    public Engine(int nbEquipes, List<Agent> agents, GameMap map, List<GameObject> objects, EvaluationFunction evaluationFunction, double respawnTime, double flagSafeZoneRadius, Long seed, int maxTurns) {
        this.nbEquipes = nbEquipes;
        this.agents = agents;
        this.map = map;
        this.objects = objects;
        this.display = null;
        this.limit_turn = maxTurns;
        this.respawnTime = (int)Math.floor(respawnTime * DEFAULT_TPS);
        this.flagSafeZoneRadius = flagSafeZoneRadius;
        runAsFastAsPossible = true;
        this.evaluationFunction = evaluationFunction;
        this.random.setSeed(seed);
    }

    /**
     * Stop the game
     */
    public void stop() {
        running = false;
    }

    /**
     * Start the game
     */
    public double run() {
        // Set up the status of teams
        for(Agent agent : agents) {
            isTeamAlive.put(agent.getTeam(), true);
            points.put(agent.getTeam(), 0);
        }

        clock = new GameClock();
        double prevUpdate = -1;
        int updateCount = 0;
        lastTpsUpdate = 0;

        gameCount++;

        while (running) {
            double time = clock.millis();
            // We only work in turns to ease the game-saving process
            if (tps <= 0) continue;
            if (!runAsFastAsPossible && time - prevUpdate < 1000.0 / tps) continue;

            // Update the TPS estimation every 30 updates
            if (updateCount == 100) {
                var delta = time - lastTpsUpdate;
                actualTps = (int) (100.0 / (delta / 1000.0));

                updateCount = 0;
                lastTpsUpdate = time;
            }

            prevUpdate = clock.millis();
            updateCount++;
            next();
            if(limit_turn != INFINITE_TURN) {
                limit_turn--;
            }
            if (isGameFinished() != null || (limit_turn <= 0 && limit_turn != INFINITE_TURN)) {
                //only stop if the game is finished or if
                if(display != null) {
                    Platform.runLater(() -> {
                        display.update(this, map, agents, objects);
                    });
                }
                break;
            }
        }
        if(evaluationFunction != null) {
            return evaluationFunction.result(this, map, agents, objects);
        }
        return 0;
    }

    private static int gameCount;

    /**
     * Compute the next turn of simulation
     */
    public void next() {

        // Spawn agents
        spawnAgents();

        // Actions
        var actions = fetchActions();
        var agentsToUpdate = new LinkedList<>(actions.keySet());
        Collections.shuffle(agentsToUpdate, random);

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

        // Update the eval
        if(evaluationFunction != null) {
            evaluationFunction.update(this, map, agents, objects);
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
    public Team isGameFinished() {
        Team t = null;
        boolean firstFlag = true;
        Flag save = null;
        for(GameObject ob : this.objects){
            if (ob instanceof Flag flag) {
                if(firstFlag){
                    t = flag.getTeam();
                    firstFlag = false;
                }else{
                    if(t != flag.getTeam()){
                        return null;
                    }
                }
                save = flag;
            }
        }
        if(save == null){
            return null;
        }
        return save.getTeam();
    }

    /**
     * Method for spawning agents
     */
    private void spawnAgents() {
        // Prepare all the spawning cells, we don't want multiple units spawning on
        // the same cell
        var spawningCells = map.getSpawningCells();
        Collections.shuffle(spawningCells, random);
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
                    agent.setCoordinate(new Vector2(spawningCells.get(i).getCoordinate().x()+0.5, spawningCells.get(i).getCoordinate().y()+0.5));
                    agent.setAngular_position(random.nextDouble(360));
                    agent.setInGame(true);
                    spawningCellsUsage.put(spawningCells.get(i), true);
                    agent.setSafeZoneTimer(safeZoneTime);
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
        var res = new LinkedHashMap<Agent, Action>();
        for(Agent agent : agents) {
            if(!agent.isInGame()) continue;
            Action act = agent.getAction(this, this.map, this.agents, this.objects);
            if ( Double.isNaN(act.rotationRatio()) ){
                act = new Action(0,act.speedRatio());
            }
            if(act.rotationRatio() > 1 || act.rotationRatio() < -1 ){
                act = new Action(Math.clamp(act.rotationRatio(),-1,1),act.speedRatio());
            }
            res.put(agent, act);
        }
        return res;
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
        double new_angle = (prev_angle + (action.rotationRatio() * rotationSpeed)) % 360;
        if (new_angle < 0) {
            new_angle += 360;
        }

        agent.setAngular_position(new_angle);
        //double angle_in_radians = Math.toRadians(new_angle);
        Vector2 vect = Vector2.fromAngle(new_angle);
        //calculate new position of the Agent
        double speed = action.speedRatio() * ((action.speedRatio() >= 0) ? agent.getSpeed() : agent.getBackSpeed());
        speed /= DEFAULT_TPS; // The rotation speed is given in meter per seconds
        vect = vect.multiply(speed);

        vect = vect.add(agent.getCoordinate());
        agent.setCoordinate(vect);
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

        if(!agent.isInGame()) {
            Flag flag = agent.getFlag().get();
            flag.setCoordinate(flag.getSpawnCoordinate());
            agent.setFlag(Optional.empty());
            flag.setHolded(false);
            return;
        }

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
        agent.setCoordinate(new Vector2(
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
                if(agent.getTeam()!=flag.getTeam()) continue;
                if(agent.getSafeZoneTimer() > 0) continue;
                handleFlagSafeZone(agent, flag);
            }
        }

        if(agent.getSafeZoneTimer() > 0) {
            agent.setSafeZoneTimer(agent.getSafeZoneTimer() - 1);
        }

        // Wall collision
        int agent_x = (int)Math.floor(agent.getCoordinate().x());
        int agent_y = (int)Math.floor(agent.getCoordinate().y());

        for(Cell cell : map.getCellsInRange(agent_x - 1, agent_y - 1, 3, 3)) {
            checkWallCollision(cell, agent);
        }

        // Item Collision
        for(GameObject object : objects){
            checkItemCollision(agent, object);
        }

        // Remove off-game agent
        if(agent.getCoordinate().x()<0 || agent.getCoordinate().x() >= map.getCells().size()
                || agent.getCoordinate().y() < 0 || agent.getCoordinate().y() > map.getCells().getFirst().size())
            agent.setInGame(false);

        // Move the flag to us
        if(agent.getFlag().isPresent()){
            agent.getFlag().get().setCoordinate(new Vector2(agent.getCoordinate().x(), agent.getCoordinate().y()));
        }
    }

    /**
     * Compute and apply the collision between two agents
     * @param agent A particular agent check collision with another agent
     * @param other Another agent check collision with a specific agent
     */
    private void checkAgentCollision(Agent agent, Agent other) {
        // Distance between the two agents
        double collisionDistance = agent.getCoordinate().distance(other.getCoordinate());

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
                    checkFlagAreaColissionDoingSoftlock(agent.getFlag().get());
                    agent.getFlag().get().setHolded(false);
                    agent.setFlag(Optional.empty());
                }
            }

            if(!otherIsSafe) {
                other.setInGame(false);
                other.setRespawnTimer(respawnTime);
                if (other.getFlag().isPresent()){
                    checkFlagAreaColissionDoingSoftlock(other.getFlag().get());
                    other.getFlag().get().setHolded(false);
                    other.setFlag(Optional.empty());
                }
            }

            // kill -> no collision
            if(!agentIsSafe || !otherIsSafe) return;
        }

        // Same team OR both in their own territory -> push
        double overlap = radius - collisionDistance;
        Vector2 pushVector = getUnidirectionalPush(
                other.getCoordinate(),
                agent.getCoordinate(),
                overlap
        );
        agent.setCoordinate(new Vector2(
                agent.getCoordinate().x() + pushVector.x()/2,
                agent.getCoordinate().y() + pushVector.y()/2
        ));
        other.setCoordinate(new Vector2(
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
        Vector2 closest = new Vector2(
                Math.clamp(agent.getCoordinate().x(), cell.getCoordinate().x(), cell.getCoordinate().x() + 1),
                Math.clamp(agent.getCoordinate().y(), cell.getCoordinate().y(), cell.getCoordinate().y() + 1)
        );
        // Distance between the point and the agent
        double collisionDistance = agent.getCoordinate().distance(closest);

        // END THE METHOD IF NO COLLISIONS
        if(collisionDistance >= agent.getRadius()) return;

        // Push logic
        double overlap = agent.getRadius() - collisionDistance;
        Vector2 pushVector = getUnidirectionalPush(
                new Vector2(cell.getCoordinate().x() + 0.5, cell.getCoordinate().y() + 0.5),
                agent.getCoordinate(),
                overlap
                );
        agent.setCoordinate(new Vector2(
                agent.getCoordinate().x() + pushVector.x(),
                agent.getCoordinate().y() + pushVector.y()
        ));
    }

    private void handleFlagSafeZone(Agent agent, Flag flag) {
        // Distance between the two agents
        double collisionDistance = agent.getCoordinate().distance(flag.getCoordinate());

        // END THE METHOD IF NO COLLISIONS
        double radius = agent.getRadius() + flagSafeZoneRadius;
        if(collisionDistance >= radius) return;

        // Push logic
        double overlap = radius - collisionDistance;
        Vector2 pushVector = getUnidirectionalPush(
                new Vector2(flag.getCoordinate().x(), flag.getCoordinate().y()),
                agent.getCoordinate(),
                overlap
        );
        agent.setCoordinate(new Vector2(
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
        double distCollision = agent.getCoordinate().distance(object.getCoordinate());

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
     * a method only for the edge case where two flag bearers kills each other in a neutral territory
     * @param flag_to_check flag that need to place pushed aside from the other flag
     */
    private void checkFlagAreaColissionDoingSoftlock(Flag flag_to_check) {
        //also called CFACDS
        ArrayList<Flag> flag_list = new ArrayList<>();
        if(objects.isEmpty())return;
        for(GameObject obj : objects){
            if(obj instanceof Flag temp_flag){
                if(!temp_flag.getHolded()){
                    flag_list.add(temp_flag);
                }
            }
        }
        flag_list.remove(flag_to_check);
        boolean colision = true;
        int place = 0;
        while(colision && !flag_list.isEmpty()) {
            if(flag_list.size() <= place){
                place = 0;
                colision = false;
            }
            Flag other = flag_list.get(place);
            place++;

            // Distance between the two agents
            double collisionDistance = flag_to_check.getCoordinate().distance(other.getCoordinate());

            // END THE METHOD IF NO COLLISIONS
            double radius = flag_to_check.getRadius() + flagSafeZoneRadius;
            if (collisionDistance >= radius) continue;


            double overlap = radius - collisionDistance;
            Vector2 pushVector = getUnidirectionalPush(
                    new Vector2(flag_to_check.getCoordinate().x(), other.getCoordinate().y()),
                    flag_to_check.getCoordinate(),
                    overlap/2
            );
            flag_to_check.setCoordinate(new Vector2(
                    flag_to_check.getCoordinate().x() + pushVector.x(),
                    flag_to_check.getCoordinate().y() + pushVector.y()
            ));
            other.setCoordinate(new Vector2(
                    other.getCoordinate().x() - pushVector.x(),
                    other.getCoordinate().y() - pushVector.y()
            ));

            flag_list.remove(other);
            colision = true;

        }
    }

    /**
     * Method for computing the repulsion vector that starts from a static object to another object
     * @param staticObject The position of the non-movable object
     * @param thingToPush The position of the object that will be pushed away
     * @param overlap The amount of overlap between the two objects
     * @return A vector describing the distance to move the thingToPush object to get rid of the overlap
     */
    private Vector2 getUnidirectionalPush(Vector2 staticObject, Vector2 thingToPush, double overlap) {
        double offsetX = thingToPush.x() - staticObject.x();
        double offsetY = thingToPush.y() - staticObject.y();
        double offsetMagnitude = Math.sqrt(offsetX * offsetX + offsetY * offsetY);
        double pushDirX = (offsetMagnitude != 0) ? offsetX/offsetMagnitude : 1;
        double pushDirY = (offsetMagnitude != 0) ? offsetY/offsetMagnitude : 0;
        return new Vector2(pushDirX * overlap, pushDirY * overlap);
    }
    public int getNbJoueursMortsByNumEquipe(int numEquipe) {
        int count = 0;
        for (Agent agent : agents) {
            if (agent.getTeam().equals(Team.numEquipeToTeam(numEquipe)) && !agent.isInGame()) {
                count += 1;
            }
        }
        return count;
    }
    public int getTempsReaparitionByNumEquipe(int numEquipe) {
        int tempsReaparitionMin = 0;
        //Trouver le premier joueur mort
        for (Agent agent : agents) {
            if (!agent.isInGame() && agent.getTeam().equals(Team.numEquipeToTeam(numEquipe))) {
                tempsReaparitionMin = agent.getRespawnTimer();
                break;
            }
        }
        for (Agent agent : agents) {
            if (!agent.isInGame() && agent.getTeam().equals(Team.numEquipeToTeam(numEquipe)) && agent.getRespawnTimer() < tempsReaparitionMin) {
                tempsReaparitionMin = agent.getRespawnTimer();
            }
        }
        return tempsReaparitionMin;
    }
    public GameClock getClock() {return clock;}
    public boolean isRunAsFastAsPossible() {return runAsFastAsPossible;}
    public Map<Team, Integer> getPoints() {return points;}
    public int getActualTps() {return actualTps;}
    public double getTps() {return tps;}
    public void setRunAsFastAsPossible(boolean runAsFastAsPossible) {this.runAsFastAsPossible = runAsFastAsPossible;}
    public void setRespawnTime(int respawnTime) {this.respawnTime = respawnTime;}
    public void setTps(int tps) {this.tps = tps;}
    public int getNbEquipes() {
        return nbEquipes;
    }
    public double getFlagSafeZoneRadius() {return flagSafeZoneRadius;}
    public Random getRandom() {return random;}
    public int getLimit_turn(){return limit_turn;}
    public Display getDisplay() {return display;}
}
