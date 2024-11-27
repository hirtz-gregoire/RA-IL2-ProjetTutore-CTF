package controlers;

import modele.Modele;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

/**
 * Controleur des différentes vues
 * Apres un clic sur un des boutons le controleur demande au modele de se modifier
 */
public class ControlerVue implements EventHandler<MouseEvent> {
    Modele modele;
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
            modele.setVue("apprentissage_menu");
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
            modele.setVue("simulation_choix_partie");
        }
        else if (b.getText().equals("Lancer Simulation")) {
            modele.setVue("simulation_main");
        }
        else if (b.getText().equals("Lancer Apprentissage")) {
            modele.setVue("apprentissage_main");
        }
        //On notifie les observateurs pour mettre à jour la vue
        //A ENELEVER LE TRY CATCH
        try {
            modele.notifierObservateurs();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}