package views;

import engine.Engine;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

/**
 * Controleur des différentes vues
 * Apres un clic sur un des boutons le controleur demande au modele de se modifier
 */
public class ControlerVue implements EventHandler<MouseEvent> {
    Engine engine;
    /**
     * Constructeur
     * @param modele le projet
     */
    public ControlerVue(Modele modele) {
        this.modele = modele;
    }
    /**
     * Methode
     * lancee quand un evenement a lieu sur un des composants sous ecoute - ici les boutons
     * @param event ActionEvent obtenu apres clic sur un des boutons
     */
    public void handle (MouseEvent event) {
        Button b = (Button) event.getSource();
        //Menu principal
        if (b.getText().equals("Simulation")) {
            modele.setVue("simulation_menu");
        }
        else if (b.getText().equals("Apprentissage")) {
            modele.setVue("apprentissage");
        }
        else if (b.getText().equals("Cartes")) {
            modele.setVue("cartes");
        }
        else if (b.getText().equals("Quitter")) {
            System.exit(1);
        }
        //Pages Simulation
        else if (b.getText().equals("Nouvelle Partie")) {
            modele.setVue("simulation_creation");
        }
        else if (b.getText().equals("Charger Partie")) {
            modele.setVue("simulation_choix");
        }
        else if (b.getText().equals("Lancer Simulation")) {
            modele.setVue("simulation_main");
        }
        //On notifie les observateurs pour mettre à jour la vue
        modele.notifierObservateurs();
    }
}