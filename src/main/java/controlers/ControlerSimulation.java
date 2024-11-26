package controlers;

import modele.Modele;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

//Controleur pour les boutons de la simulation
public class ControlerSimulation implements EventHandler<MouseEvent> {
    //L'engine est attribut du controleur
    Modele modele;

    public ControlerSimulation(Modele modele) {
        this.modele = modele;
    }

    public void handle (MouseEvent event) {
        Button b = (Button) event.getSource();
        //Menu principal
        if (b.getText().equals("Décélerer")) {
            modele.setTps((int)(modele.getTps()/2));
        }
        else if (b.getText().equals("Pas Arrière")) {
            modele.setTps(0);
        }
        else if (b.getText().equals("Pause")) {
            modele.setTps(0);
        }
        else if (b.getText().equals("Pas Avant")) {
            modele.setTps(0);
        }
        else if (b.getText().equals("Accélérer")) {
            modele.setTps(modele.getTps()*2);
        }
    }
}