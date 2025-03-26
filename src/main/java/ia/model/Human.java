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
        String[] gamepad = new String[2];
        if(controls.contains("Manette")){
            gamepad = controls.split(" ");
            controls = gamepad[0];
        }
        //System.out.println(Arrays.toString(gamepad));
        switch (controls) {
            case "ZQSD", "ARROWS", "OKLM":
                this.control = new Keyboard(controls);
                break;
            case "Manette" :
                this.control = new Gamepad(gamepad[1]);
            default :
                System.out.println(controls + " est invalide !!!");
                break;
        }
    }

    @Override
    public Action getAction(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects) {
        return control.getMovement(engine);
    }
}
