package ia.model;

import display.InputListener;
import engine.Engine;
import engine.agent.Action;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashSet;
import java.util.List;

public class Human extends Model implements InputListener {

    private final HashSet<KeyCode> keysPressed = new HashSet<>();

    public Human() {}

    boolean isInputSet = false;
    @Override
    public Action getAction(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects) {
        double rot = 0;
        double speed = 0;
        if(!isInputSet) {
            engine.getDisplay().addListener(this);
        }

        boolean z = keysPressed.contains(KeyCode.Z);
        boolean q = keysPressed.contains(KeyCode.Q);
        boolean s = keysPressed.contains(KeyCode.S);
        boolean d = keysPressed.contains(KeyCode.D);

        if(z) speed += 1;
        if(q) rot -= 1;
        if(s) speed -= 1;
        if(d) rot += 1;

        return new Action(rot,speed);
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.Z) {
            keysPressed.add(KeyCode.Z);
        }
        if (e.getCode() == KeyCode.S) {
            keysPressed.add(KeyCode.S);
        }
        if (e.getCode() == KeyCode.Q) {
            keysPressed.add(KeyCode.Q);
        }
        if (e.getCode() == KeyCode.D) {
            keysPressed.add(KeyCode.D);
        }
    }

    @Override
    public void onKeyReleased(KeyEvent e) {
        if (e.getCode() == KeyCode.Z) {
            keysPressed.remove(KeyCode.Z);
        }
        if (e.getCode() == KeyCode.S) {
            keysPressed.remove(KeyCode.S);
        }
        if (e.getCode() == KeyCode.Q) {
            keysPressed.remove(KeyCode.Q);
        }
        if (e.getCode() == KeyCode.D) {
            keysPressed.remove(KeyCode.D);
        }
    }
}
