package ia.model;

import engine.Engine;
import engine.agent.Action;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;
import javafx.scene.input.KeyCode;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.List;
import javax.swing.JFrame;

public class Human extends Model{

    private final HashSet<KeyCode> keysPressed = new HashSet<>();
    private JFrame frame;

    public Human() {}

    boolean isInputSet = false;
    @Override
    public Action getAction(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects) {
        double rot = 0;
        double speed = 0;
        if(!isInputSet) {
            engine.getDisplay().getGrid().getScene().setOnKeyPressed(e -> {
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
            });
            engine.getDisplay().getGrid().getScene().setOnKeyReleased(e -> {
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
            });
        }

        boolean z = keysPressed.contains(KeyCode.Z);
        boolean q = keysPressed.contains(KeyCode.Q);
        boolean s = keysPressed.contains(KeyCode.S);
        boolean d = keysPressed.contains(KeyCode.D);

        Action direction = new Action(0,0);
        if (z && q){
            direction = new Action(1,1);
        } else if (z && d)
            direction = new Action(-1,1);
        else if (s && q)
            direction = new Action(1,-1);
        else if (s && d)
            direction = new Action(-1,-1);
        else if (z)
            direction = new Action(0,1);
        else if (s)
            direction = new Action(0,-1);
        else if (q)
            direction = new Action(1,0);
        else if (d)
            direction = new Action(-1,0);

        return new Action(rot,speed);
    }
}
