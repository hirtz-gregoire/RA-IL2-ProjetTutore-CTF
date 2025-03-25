package ia.model;

import engine.Engine;
import engine.agent.Action;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;
import ia.model.HumanControl.Gamepad;
import ia.model.HumanControl.HumanControl;
import ia.model.HumanControl.Keyboard;
import java.util.List;

public class Human extends Model  {

    HumanControl control;

    public Human(String controls) {
        switch (controls) {
            case "ZQSD" -> this.control = new Keyboard(controls);
            case "OKLM" -> this.control = new Keyboard(controls);
            case "ARROWS" -> this.control = new Keyboard(controls);
            case "Controller" -> this.control = new Gamepad(0);
            default -> System.out.println(controls + " est invalide !!!");
        }
    }

    @Override
    public Action getAction(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects) {
        return control.getMovement(engine);
    }
}
