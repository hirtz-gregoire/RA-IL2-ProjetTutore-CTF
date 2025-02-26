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

public class Human extends Model implements KeyListener {

    private final HashSet<Integer> keysPressed = new HashSet<>();
    private JFrame frame;

    public Human() {
//        frame = new JFrame("Human Controller");
//        frame.setSize(300, 200);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setVisible(true);
//        frame.addKeyListener(this); // Attach key listener
//        frame.setFocusable(true);
//        frame.requestFocus();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keysPressed.add(e.getKeyCode()); // Track pressed keys
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keysPressed.remove(e.getKeyCode()); // Remove released keys
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    boolean isInputSet = false;
    @Override
    public Action getAction(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects) {
        if(!isInputSet) {
            engine.getDisplay().getGrid().getScene().setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.A) {
                    System.out.println("The 'A' key was pressed");
                }
            });
        }

        boolean z = keysPressed.contains(KeyEvent.VK_Z);
        boolean q = keysPressed.contains(KeyEvent.VK_Q);
        boolean s = keysPressed.contains(KeyEvent.VK_S);
        boolean d = keysPressed.contains(KeyEvent.VK_D);

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

        return direction;
    }
}
