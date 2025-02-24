package engine.agent;

import engine.Vector2;
import engine.Engine;
import engine.Team;
import ia.model.Model;
import engine.map.GameMap;
import engine.object.Flag;
import engine.object.GameObject;

import java.util.List;
import java.util.Optional;

public class Agent extends GameObject {
    /** rotation position 0 equals RIGHT*/
    private double angular_position;
    /** radius size */
    private double radius;
    /** maximum travel speed in forward */
    private double speed;
    /** maximum travel speed in reverse */
    private double backSpeed;
    /** maximum rotation speed */
    private double rotateSpeed;
    /** decision making model */
    private Model model;
    /** reference Flag if has a flag otherwise null */
    private Optional<Flag> flag;
    /** agent living on the map or waiting to respawn */
    private boolean inGame = false;
    /** time remaining before respawn */
    private int respawnTimer = 0;
    /** time remaining before being push by flag's safe zone */
    private int safeZoneTimer = 0;
    /** maximum distance of vision */
    private double maxDistanceVision = 1;

    /**
     * @param coord
     * @param radius
     * @param speed
     * @param backSpeed
     * @param rotateSpeed
     * @param team
     * @param flag
     * @param model
     * @param maxDistanceVision
     *
     * @throws IllegalArgumentException if
     * <br>- coord==null
     * <br>- team==null
     * <br>- model==null
     * <br>- radius<0
     * <br>- speed<0
     * <br>- backSpeed<0
     * <br>- rotation
     */
    public Agent(Vector2 coord, double radius, double speed, double backSpeed, double rotateSpeed, Team team, Optional<Flag> flag, Model model, double maxDistanceVision) {
        super(coord, team);

        // check object coord, team, and model are not null
        if (coord == null)
            throw new IllegalArgumentException("Coordinate cannot be null");
        if (team == null)
            throw new IllegalArgumentException("Team cannot be null");
        if (model == null)
            throw new IllegalArgumentException("Model cannot be null");

        // check radius not < 0
        // check radius, all speed are not < 0
        if (radius < 0)
            throw new IllegalArgumentException("Radius cannot be negative");
        if (speed < 0 || backSpeed < 0 || rotateSpeed<0)
            throw new IllegalArgumentException("Speed cannot be negative");
        if (maxDistanceVision < 0)
            throw new IllegalArgumentException("Max distance vision cannot be negative");

        this.coordinate = coord;
        this.radius = radius;
        this.speed = speed;
        this.backSpeed = backSpeed;
        this.rotateSpeed = rotateSpeed;
        this.flag = flag;
        this.model = model;
        this.maxDistanceVision = maxDistanceVision;
        model.setMyself(this);
        this.angular_position = 0;
    }

    public Agent() {
        super(new Vector2(0, 0), null);
        this.speed = 0;
        this.backSpeed = 0;
        this.rotateSpeed = 0;
        this.model = null;
        this.maxDistanceVision = 0;
        this.angular_position = 0;
    }

    /**
     * call the model's getAction()
     * @param map GameMap
     * @param agents list Agent
     * @param objects list GameObject
     * @return
     */
    public Action getAction(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects){
        // application de getAction du models en attribut
        return model.getAction(engine, map, agents, objects);
    }

    public double getRadius() { return radius; }
    public double getAngular_position() {return angular_position;}
    public double getSpeed() { return speed; }
    public double getBackSpeed() { return backSpeed; }
    public double getRotateSpeed() { return rotateSpeed; }
    public Model getModel() { return model; }
    public Optional<Flag> getFlag() { return flag; }
    public boolean isInGame() { return inGame; }
    public int getRespawnTimer() { return respawnTimer; }
    public void setRespawnTimer(int respawnTimer) {this.respawnTimer = respawnTimer;}
    public void setInGame(boolean inGame) {this.inGame = inGame;}
    public void setFlag(Optional<Flag> flag) {this.flag = flag;}
    public void setAngular_position(double angular_position) {this.angular_position = angular_position;}
    public int getSafeZoneTimer() {
        return safeZoneTimer;
    }
    public void setSafeZoneTimer(int safeZoneTimer) {
        this.safeZoneTimer = safeZoneTimer;
    }
    public double getMaxDistanceVision() {
        return maxDistanceVision;
    }
    public void setMaxDistanceVision(double maxDistanceVision) {
        this.maxDistanceVision = maxDistanceVision;
    }
}
