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
import java.awt.Dimension;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Modele modele = new Modele(ViewsEnum.SimulationMenu);

        ControlerVue controlVue = new ControlerVue(modele);

        VueSimulationMenu vueSimulationMenu = new VueSimulationMenu();
        VueSimulationParametersChoice vueSimulationCreate = new VueSimulationParametersChoice();
        VueSimulationGameChoice vueSimulationGameChoice = new VueSimulationGameChoice();
        VueSimulationMapChoice vueSimulationMapChoice = new VueSimulationMapChoice();
        VueSimulationMain vueSimulationMain = new VueSimulationMain();
        VueLearningMenu vueLearningMenu = new VueLearningMenu();
        VueLearningMain vueLearningMain = new VueLearningMain();
        VueMaps vueMaps = new VueMaps();

        modele.enregistrerObservateur(vueSimulationMenu);
        modele.enregistrerObservateur(vueSimulationCreate);
        modele.enregistrerObservateur(vueSimulationGameChoice);
        modele.enregistrerObservateur(vueSimulationMapChoice);
        modele.enregistrerObservateur(vueSimulationMain);
        modele.enregistrerObservateur(vueLearningMenu);
        modele.enregistrerObservateur(vueLearningMain);
        modele.enregistrerObservateur(vueMaps);

        //La page principale
        BorderPane borderPane = new BorderPane();

        //la partie avec les boutons pour accéder aux autres vues
        HBox bouttonsMenu = new HBox();
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
        bouttonsMenu.getChildren().addAll(buttonVueSimulation, buttonVueApprentissage, buttonVueCartes, buttonQuitter);
        borderPane.setTop(bouttonsMenu);

        // Centre avec les vues
        VBox centerBox = new VBox(vueSimulationMenu, vueSimulationCreate, vueSimulationGameChoice, vueSimulationMapChoice, vueSimulationMain, vueLearningMenu, vueLearningMain, vueMaps);
        borderPane.setCenter(centerBox);

        //scene et stage
        Scene scene = new Scene(borderPane, 1000, 500);
        stage.setScene(scene);
        stage.setTitle("CTF");
        stage.setFullScreen(true);
        stage.show();

        //on actualise pour tout afficher
         modele.notifierObservateurs();
    }
}
