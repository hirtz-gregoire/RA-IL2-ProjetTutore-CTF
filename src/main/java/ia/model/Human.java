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

    private final HashSet<String> keysPressed = new HashSet<>();
    private KeyCode[] keys;

    public Human(String controls) {
        switch (controls) {
            case "ZQSD" -> this.keys = new KeyCode[]{KeyCode.Z, KeyCode.Q, KeyCode.S, KeyCode.D};
            case "OKLM" -> this.keys = new KeyCode[]{KeyCode.O, KeyCode.K, KeyCode.L, KeyCode.M};
            case "ARROWS" -> this.keys = new KeyCode[]{KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT};
            default -> System.out.println(controls + " est invalide !!!");
        }
    }

    boolean isInputSet = false;
    @Override
    public Action getAction(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects) {
        double rot = 0;
        double speed = 0;
        if(!isInputSet) {
            engine.getDisplay().addListener(this);
        }

        boolean forward = keysPressed.contains("FORWARD");
        boolean backward = keysPressed.contains("BACKWARD");
        boolean left = keysPressed.contains("LEFT");
        boolean rigth = keysPressed.contains("RIGHT");

        if(forward) speed += 1;
        if(backward) rot -= 1;
        if(left) speed -= 1;
        if(rigth) rot += 1;

        return new Action(rot,speed);
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        if (e.getCode() == keys[0]) {
            keysPressed.add("FORWARD");
        }
        if (e.getCode() == keys[1]) {
            keysPressed.add("BACKWARD");
        }
        if (e.getCode() == keys[2]) {
            keysPressed.add("LEFT");
        }
        if (e.getCode() == keys[3]) {
            keysPressed.add("RIGHT");
        }
    }

    @Override
    public void onKeyReleased(KeyEvent e) {
        if (e.getCode() == keys[0]) {
            keysPressed.remove("FORWARD");
        }
        if (e.getCode() == keys[1]) {
            keysPressed.remove("BACKWARD");
        }
        if (e.getCode() == keys[2]) {
            keysPressed.remove("LEFT");
        }
        if (e.getCode() == keys[3]) {
            keysPressed.remove("RIGHT");
        }
    }
}
