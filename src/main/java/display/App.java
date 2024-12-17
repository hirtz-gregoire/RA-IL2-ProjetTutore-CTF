package display;

import display.modele.Modele;
import display.views.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import display.controlers.ControlerVue;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        //Creation du projet (le modèle) avec sa vue de base
        Modele modele = new Modele(ViewsEnum.SimulationMenu);

        //Controler des vues
        ControlerVue controlVue = new ControlerVue(modele);

        //Les vues
        VueSimulationMenu vueSimulationMenu = new VueSimulationMenu();
        VueSimulationChoixParametres vueSimulationCreate = new VueSimulationChoixParametres();
        VueSimulationChoixPartie vueSimulationChoixPartie = new VueSimulationChoixPartie();
        VueSimulationChoixCarte vueSimulationChoixCarte = new VueSimulationChoixCarte();
        VueSimulationMain vueSimulationMain = new VueSimulationMain();
        VueApprentissageMenu vueApprentissageMenu = new VueApprentissageMenu();
        VueApprentissageMain vueApprentissageMain = new VueApprentissageMain();
        VueCartes vueCartes = new VueCartes(1);

        //Les vues s'enregistrent comme vue du modele
        modele.enregistrerObservateur(vueSimulationMenu);
        modele.enregistrerObservateur(vueSimulationCreate);
        modele.enregistrerObservateur(vueSimulationChoixPartie);
        modele.enregistrerObservateur(vueSimulationChoixCarte);
        modele.enregistrerObservateur(vueSimulationMain);
        modele.enregistrerObservateur(vueApprentissageMenu);
        modele.enregistrerObservateur(vueApprentissageMain);
        modele.enregistrerObservateur(vueCartes);

        //La page principale
        BorderPane borderPane = new BorderPane();

        //la partie avec les boutons pour accéder aux autres vues
        HBox head = new HBox();
        //les boutons pour accéder aux vues
        Button buttonVueSimulation = new Button("Simulation");
        Button buttonVueApprentissage = new Button("Apprentissage");
        Button buttonVueCartes = new Button("Cartes");
        Button buttonQuitter = new Button("Quitter");
        //ajout du controle des boutons à controlerVue
        buttonVueSimulation.setOnMouseClicked(controlVue);
        buttonVueApprentissage.setOnMouseClicked(controlVue);
        buttonVueCartes.setOnMouseClicked(controlVue);
        buttonQuitter.setOnMouseClicked(controlVue);
        //on ajoute les boutons à la Page
        head.getChildren().addAll(buttonVueSimulation, buttonVueApprentissage, buttonVueCartes, buttonQuitter);
        borderPane.setTop(head);

        // Centre avec les vues
        VBox centerBox = new VBox(vueSimulationMenu, vueSimulationCreate, vueSimulationChoixPartie, vueSimulationChoixCarte, vueSimulationMain, vueApprentissageMenu, vueApprentissageMain, vueCartes);
        borderPane.setCenter(centerBox);

        //scene et stage
        Scene scene = new Scene(borderPane,1400,750);
        stage.setScene(scene);
        stage.setTitle("CTF");
        stage.show();

        //on actualise pour tout afficher
         modele.notifierObservateurs();
    }
}
