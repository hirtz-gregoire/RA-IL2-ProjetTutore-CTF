package display;

import engine.Engine;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

//Controleur pour les boutons de la simulation
public class ControlerSimulation implements EventHandler<MouseEvent> {
    //L'engine est attribut du controleur
    Engine engine;

    public ControlerSimulation(Engine engine) {
        this.engine = engine;
    }

    public void handle (MouseEvent event) {
        Button b = (Button) event.getSource();
        //Menu principal
        if (b.getText().equals("Décélerer")) {
            engine.setTps((int)(engine.getTps()/2));
        }
        else if (b.getText().equals("Pas Arrière")) {
            engine.setTps(0);
        }
        else if (b.getText().equals("Pause")) {
            engine.setTps(0);
        }
        else if (b.getText().equals("Pas Avant")) {
            engine.setTps(0);
        }
        else if (b.getText().equals("Accélérer")) {
            engine.setTps(engine.getTps()*2);
        }
    }
}