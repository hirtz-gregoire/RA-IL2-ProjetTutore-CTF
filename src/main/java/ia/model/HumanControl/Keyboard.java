package ia.model.HumanControl;

import display.InputListener;
import engine.Engine;
import engine.agent.Action;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashSet;

public class Keyboard extends HumanControl implements InputListener {

    private final HashSet<String> keysPressed = new HashSet<>();
    private KeyCode[] keys;
    boolean isInputSet = false;

    public Keyboard(String keys) {
        switch (keys) {
            case "ZQSD" -> this.keys = new KeyCode[]{KeyCode.Z, KeyCode.Q, KeyCode.S, KeyCode.D};
            case "OKLM" -> this.keys = new KeyCode[]{KeyCode.O, KeyCode.K, KeyCode.L, KeyCode.M};
            case "ARROWS" -> this.keys = new KeyCode[]{KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT};
            default -> System.out.println(keys + " est invalide !!!");
        }
    }

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

    @Override
    public Action getMovement(Engine engine) {
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
}
