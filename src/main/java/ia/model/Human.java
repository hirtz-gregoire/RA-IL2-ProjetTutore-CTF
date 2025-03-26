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

    public Human(String origin) {
        String[] controls = new String[2];
        controls[0] = origin;
        if(origin.contains("Manette")){
            controls = origin.split(" ");
            origin = controls[0];
        }
        switch (controls[0]) {
            case "ZQSD", "ARROWS", "OKLM":
                this.control = new Keyboard(controls[0]);
                break;
            case "Manette" :
                this.control = new Gamepad(controls[1]);
                break;
            default :
                System.out.println(origin + " est invalide !!!");
                break;
        }
    }

    @Override
    public Action getAction(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects) {
        return control.getMovement(engine);
    }
}
