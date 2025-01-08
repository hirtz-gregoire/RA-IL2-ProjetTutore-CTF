package display.controlers;

import display.modele.ModeleMVC;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import display.views.ViewsEnum;

/**
 * Controleur des différentes vues
 * Apres un clic sur un des boutons le controleur demande au modele de se modifier
 */
public class ControlerVue implements EventHandler<MouseEvent> {
    ModeleMVC modeleMVC;
    /**
     * Constructeur
     * @param modeleMVC le projet
     */
    public ControlerVue(ModeleMVC modeleMVC) {
        this.modeleMVC = modeleMVC;
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
            modeleMVC.setVue(ViewsEnum.SimulationMenu);
        }
        else if (b.getText().equals("Apprentissage")) {
            modeleMVC.setVue(ViewsEnum.LearningMenu);
        }
        else if (b.getText().equals("Cartes")) {
            modeleMVC.setVue(ViewsEnum.Maps);
        }
        else if (b.getText().equals("Quitter")) {
            System.exit(1);
        }
        //Pages Simulation
        else if (b.getText().equals("Nouvelle Partie")) {
            modeleMVC.setVue(ViewsEnum.SimulationMapChoice);
        }
        else if (b.getText().equals("Charger Partie")) {
            modeleMVC.setVue(ViewsEnum.SimulationGameChoice);
        }
        else if (b.getText().equals("Choisir paramètres")) {
            modeleMVC.setVue(ViewsEnum.SimulationParametersChoice);
        }
        else if (b.getText().equals("Lancer Simulation")) {
            modeleMVC.setVue(ViewsEnum.SimulationMain);
        }
        else if (b.getText().equals("Recommencer Partie")) {
            System.out.println("Restart de la partie");
        }
        //Pages Apprentissage
        else if (b.getText().equals("Lancer Apprentissage")) {
            modeleMVC.setVue(ViewsEnum.LearningMain);
        }
        //On notifie les observateurs pour mettre à jour la vue
        //A ENELEVER LE TRY CATCH
        try {
            modeleMVC.notifierObservateurs();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}