import controlers.ControlerSimulation;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import controlers.ControlerVue;
import modele.*;
import views.*;

public class Main extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        //Creation du projet (le modèle)
        Modele modele = new Modele();

        //Contrôleur pour choisir la vue
        ControlerVue controlVue = new ControlerVue(modele);
        //Controler de l'affichage de la simulation
        ControlerSimulation controlerSimulation = new ControlerSimulation(modele);

        //Les Vues
        VueSimulationMenu vueSimulationMenu = new VueSimulationMenu();
        VueSimulationCreate vueSimulationCreate = new VueSimulationCreate();
        VueSimulationMain vueSimulationMain = new VueSimulationMain();
        VueApprentissage vueApprentissage = new VueApprentissage();
        VueCartes vueCartes = new VueCartes(10);
        //Les vues s'enregistrent comme vues du modele
        modele.enregistrerObservateur(vueSimulationMenu);
        modele.enregistrerObservateur(vueSimulationCreate);
        modele.enregistrerObservateur(vueSimulationMain);
        modele.enregistrerObservateur(vueApprentissage);
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
        VBox centerBox = new VBox(vueSimulationMenu, vueSimulationCreate, vueSimulationMain, vueApprentissage, vueCartes);
        borderPane.setCenter(centerBox);

        //scene et stage
        Scene scene = new Scene(borderPane,600,600);
        stage.setScene(scene);
        stage.setTitle("CTF");
        stage.show();

        //on actualise pour tout afficher
        modele.notifierObservateurs();
    }
}
