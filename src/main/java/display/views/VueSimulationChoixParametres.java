package display.views;

import display.controlers.ControlerVue;
import engine.Files;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import display.modele.Modele;

import java.io.File;
import java.util.Random;

public class VueSimulationChoixParametres extends Pane implements Observateur {
    public VueSimulationChoixParametres() {
        super();
    }

    @Override
    public void actualiser(Modele modele) {
        this.getChildren().clear();  // efface toute la vue

        //on n'utilise la vue que si la vue est en colonne
        if (modele.getVue().equals(ViewsEnum.SimulationChoixParametres)) {
            Color textColor = Color.BLACK;

            //controleur pour modifier, créer tache et liste
            ControlerVue controlVue = new ControlerVue(modele);

            BorderPane borderPane = new BorderPane();

            //Paramètres au millieu de la border pane
            //Dans une grid
            GridPane grid = new GridPane();
            //Choix temps de réapration
            Slider tempsReaparition = new Slider(1, 50, modele.getTempsReaparition());
            tempsReaparition.setMajorTickUnit(1);         // Espacement entre les ticks principaux
            tempsReaparition.setMinorTickCount(0);        // Pas de ticks intermédiaires
            tempsReaparition.setSnapToTicks(true);        // Alignement sur les ticks
            tempsReaparition.setShowTickMarks(true);      // Afficher les ticks
            tempsReaparition.setShowTickLabels(true);     // Afficher les labels
            Label tempsReaparitionText = new Label("Temps de réaparition :");
            Label tempsReaparitionValue = new Label(Double.toString(tempsReaparition.getValue()));
            tempsReaparitionText.setTextFill(textColor);
            GridPane.setConstraints(tempsReaparitionText, 0, 0);
            tempsReaparition.valueProperty().addListener((
                    ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) -> {
                tempsReaparitionValue.setText(String.format("%.2f", new_val));
                modele.setTempsReaparition(new_val.intValue());
            });
            GridPane.setConstraints(tempsReaparition, 1, 0);
            tempsReaparitionValue.setTextFill(textColor);
            GridPane.setConstraints(tempsReaparitionValue, 2, 0);
            //Choix Nombre de joueurs
            Slider nombreJoueur = new Slider(1, 50, modele.getNbJoueurs());
            nombreJoueur.setMajorTickUnit(1);         // Espacement entre les ticks principaux
            nombreJoueur.setMinorTickCount(0);        // Pas de ticks intermédiaires
            nombreJoueur.setSnapToTicks(true);        // Alignement sur les ticks
            nombreJoueur.setShowTickMarks(true);      // Afficher les ticks
            nombreJoueur.setShowTickLabels(true);     // Afficher les labels
            Label nombreJoueurText = new Label("Nombre de joueur :");
            Label nombreJoueurValue = new Label(Double.toString(nombreJoueur.getValue()));
            nombreJoueurText.setTextFill(textColor);
            GridPane.setConstraints(nombreJoueurText, 0, 1);
            nombreJoueur.valueProperty().addListener((
                    ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) -> {
                nombreJoueurValue.setText(String.format("%.2f", new_val));
                modele.setNbJoueurs(new_val.intValue());
            });
            GridPane.setConstraints(nombreJoueur, 1, 1);
            nombreJoueurValue.setTextFill(textColor);
            GridPane.setConstraints(nombreJoueurValue, 2, 1);
            //Choix Vitesse de déplacement
            Slider vitesseDeplacement = new Slider(1, 5, modele.getVitesseDeplacement());
            vitesseDeplacement.setMajorTickUnit(1);         // Espacement entre les ticks principaux
            vitesseDeplacement.setMinorTickCount(0);        // Pas de ticks intermédiaires
            vitesseDeplacement.setSnapToTicks(true);        // Alignement sur les ticks
            vitesseDeplacement.setShowTickMarks(true);      // Afficher les ticks
            vitesseDeplacement.setShowTickLabels(true);     // Afficher les labels
            Label vitesseDeplacementText = new Label("Vitesse déplacement :");
            Label vitesseDeplacementValue = new Label(Double.toString(vitesseDeplacement.getValue()));
            vitesseDeplacementText.setTextFill(textColor);
            GridPane.setConstraints(vitesseDeplacementText, 0, 2);
            vitesseDeplacement.valueProperty().addListener((
                    ObservableValue<? extends Number> ov, Number old_val,
                    Number new_val) -> {
                vitesseDeplacementValue.setText(String.format("%.2f", new_val));
                modele.setVitesseDeplacement(new_val.intValue());
            });
            GridPane.setConstraints(vitesseDeplacement, 1, 2);
            vitesseDeplacementValue.setTextFill(textColor);
            GridPane.setConstraints(vitesseDeplacementValue, 2, 2);

            grid.getChildren().addAll(tempsReaparitionText, tempsReaparition, tempsReaparitionValue,
                    nombreJoueurText, nombreJoueur, nombreJoueurValue,
                    vitesseDeplacementText, vitesseDeplacement, vitesseDeplacementValue);
            borderPane.setCenter(grid);

            //HBox choix des models des équipes
            HBox boxChoixEquipes = new HBox();
            for (int numEquipe = 0; numEquipe < modele.getNbEquipes(); numEquipe++) {
                //VBox des choix d'une seule équipe
                Label labelEquipe = new Label("Choix équipe "+numEquipe+1);
                VBox boxChoixEquipe = new VBox(labelEquipe);

                ToggleGroup groupeBoutonsEquipe = new ToggleGroup();
                //Boucle avec les models d'agent
                for (File fichierModel : Files.getListeFichiersModels()) {
                    RadioButton button = new RadioButton(fichierModel.getName());
                    button.setToggleGroup(groupeBoutonsEquipe);
                    boxChoixEquipe.getChildren().add(button);
                    button.fire();
                }
                //Listener pour détécter le choix d'une carte
                int finalNumEquipe = numEquipe;
                groupeBoutonsEquipe.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                    public void changed(ObservableValue<? extends Toggle> ob, Toggle o, Toggle n) {
                        RadioButton rb = (RadioButton)groupeBoutonsEquipe.getSelectedToggle();
                        if (rb != null) {
                            String s = rb.getText();
                            //Enregistrer le model de l'agent choisit dans le modele
                            modele.setModelEquipeIndex(s, finalNumEquipe);
                        }
                    }
                });
                boxChoixEquipes.getChildren().add(boxChoixEquipe);
            }
            //Choix des équipe à gauche de la border Pane
            borderPane.setLeft(boxChoixEquipes);


            //Choix seed
            Label seedText = new Label("Seed :");
            seedText.setTextFill(textColor);
            GridPane.setConstraints(seedText, 0, 3);

            TextField seedInput = new TextField();
            seedInput.setPromptText("Entrez la seed");
            modele.setSeed(new Random().nextLong());
            seedInput.textProperty().addListener((observable, oldValue, newValue) -> {
                // Permet uniquement les chiffres
                if (!newValue.matches("\\d*")) {
                    seedInput.setText(newValue.replaceAll("[^\\d]", ""));
                }
                if (!seedInput.getText().isEmpty()) {
                    modele.setSeed(Integer.parseInt(seedInput.getText()));
                }
            });
            GridPane.setConstraints(seedInput, 1, 3);
            grid.getChildren().addAll(seedText, seedInput);

            //Boutton Lancer Partie en bas de la border pane
            Button buttonLancerSimulation = new Button("Lancer Simulation");
            //ajout des controles sur le bouton
            buttonLancerSimulation.setOnMouseClicked(controlVue);
            borderPane.setBottom(buttonLancerSimulation);

            this.getChildren().add(borderPane);
        }
    }
}
