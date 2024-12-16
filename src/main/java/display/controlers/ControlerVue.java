package display.controlers;

import display.modele.Modele;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import display.views.ViewsEnum;

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
            modele.setVue(ViewsEnum.SimulationMenu);
        }
        else if (b.getText().equals("Apprentissage")) {
            modele.setVue(ViewsEnum.ApprentissageMenu);
        }
        else if (b.getText().equals("Cartes")) {
            modele.setVue(ViewsEnum.Cartes);
        }
        else if (b.getText().equals("Quitter")) {
            System.exit(1);
        }
        //Pages Simulation
        else if (b.getText().equals("Nouvelle Partie")) {
            modele.setVue(ViewsEnum.SimulationCreate);
        }
        else if (b.getText().equals("Charger Partie")) {
            modele.setVue(ViewsEnum.SimulationChoixPartie);
        }
        else if (b.getText().equals("Choisir Carte")) {
            modele.setVue(ViewsEnum.SimulationChoixCarte);
        }
        else if (b.getText().equals("Lancer Simulation")) {
            modele.setVue(ViewsEnum.SimulationMain);
        }
        //Pages Apprentissage
        else if (b.getText().equals("Lancer Apprentissage")) {
            modele.setVue(ViewsEnum.ApprentissageMain);
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