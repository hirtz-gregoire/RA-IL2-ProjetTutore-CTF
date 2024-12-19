package display.views;

import display.controlers.ControlerVue;
import engine.Files;
import engine.Team;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import display.modele.ModeleMVC;

import java.io.File;
import java.util.Random;

public class VueSimulationParametersChoice extends Pane implements Observateur {
    public VueSimulationParametersChoice() {
        super();
    }

    @Override
    public void actualiser(ModeleMVC modeleMVC) {
        this.getChildren().clear();

        if (modeleMVC.getVue().equals(ViewsEnum.SimulationParametersChoice)) {
            ControlerVue controlVue = new ControlerVue(modeleMVC);
            BorderPane borderPane = new BorderPane();
            VBox vboxParametres = new VBox();

            //Choix temps de réapration
            Slider tempsReaparitionSlider = new Slider(1, 100, modeleMVC.getTempsReaparition());
            tempsReaparitionSlider.setMajorTickUnit(1);         // Espacement entre les ticks principaux
            tempsReaparitionSlider.setMinorTickCount(0);        // Pas de ticks intermédiaires
            tempsReaparitionSlider.setSnapToTicks(true);        // Alignement sur les ticks
            tempsReaparitionSlider.setShowTickMarks(true);      // Afficher les ticks
            tempsReaparitionSlider.setShowTickLabels(true);     // Afficher les labels
            Label tempsReaparitionText = new Label("Temps de réaparition :");
            Label tempsReaparitionValue = new Label(Double.toString(tempsReaparitionSlider.getValue()));
            tempsReaparitionSlider.valueProperty().addListener((
                    ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) -> {
                tempsReaparitionValue.setText(String.format("%.2f", new_val));
                modeleMVC.setTempsReaparition(new_val.intValue());
            });

            //Choix Nombre de joueurs
            Slider nombreJoueur = new Slider(1, 50, modeleMVC.getNbJoueurs());
            nombreJoueur.setMajorTickUnit(1); nombreJoueur.setMinorTickCount(0); nombreJoueur.setSnapToTicks(true); nombreJoueur.setShowTickMarks(true); nombreJoueur.setShowTickLabels(true);
            Label nombreJoueurText = new Label("Nombre de joueur :");
            Label nombreJoueurValue = new Label(Double.toString(nombreJoueur.getValue()));
            GridPane.setConstraints(nombreJoueurText, 0, 1);
            nombreJoueur.valueProperty().addListener((
                    ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) -> {
                nombreJoueurValue.setText(String.format("%.2f", new_val));
                modeleMVC.setNbJoueurs(new_val.intValue());
            });

            //Choix Vitesse de déplacement
            Slider vitesseDeplacement = new Slider(1, 5, modeleMVC.getVitesseDeplacement());
            vitesseDeplacement.setMajorTickUnit(1); vitesseDeplacement.setMinorTickCount(0); vitesseDeplacement.setSnapToTicks(true); vitesseDeplacement.setShowTickMarks(true); vitesseDeplacement.setShowTickLabels(true);
            Label vitesseDeplacementText = new Label("Vitesse déplacement :");
            Label vitesseDeplacementValue = new Label(Double.toString(vitesseDeplacement.getValue()));
            GridPane.setConstraints(vitesseDeplacementText, 0, 2);
            vitesseDeplacement.valueProperty().addListener((
                    ObservableValue<? extends Number> ov, Number old_val,
                    Number new_val) -> {
                vitesseDeplacementValue.setText(String.format("%.2f", new_val));
                modeleMVC.setVitesseDeplacement(new_val.intValue());
            });

            vboxParametres.getChildren().addAll(tempsReaparitionText, tempsReaparitionSlider, tempsReaparitionValue,
                    nombreJoueurText, nombreJoueur, nombreJoueurValue,
                    vitesseDeplacementText, vitesseDeplacement, vitesseDeplacementValue);
            borderPane.setLeft(vboxParametres);

            //HBox choix des models des équipes
            HBox boxChoixModels = new HBox();
            for (int numEquipe = 0; numEquipe < modeleMVC.getNbEquipes(); numEquipe++) {
                //VBox des choix d'une seule équipe
                Label labelEquipe = new Label("Choix équipe "+ Team.numEquipeToString(numEquipe+1));
                VBox boxChoixModel = new VBox(labelEquipe);

                ToggleGroup toggleGroupBoutonsModel = new ToggleGroup();
                // Variable pour stocker le premier RadioButton
                RadioButton firstRadioButton = null;
                //Boucle avec les models d'agent
                for (File fichierModel : Files.getListSavesFilesModels()) {
                    RadioButton radioButton = new RadioButton(fichierModel.getName().replace(".txt", ""));
                    radioButton.setToggleGroup(toggleGroupBoutonsModel);
                    radioButton.setUserData(numEquipe);
                    if (Files.getListSavesFilesModels()[0].equals(fichierModel)) {
                        firstRadioButton = radioButton;
                    }
                    boxChoixModel.getChildren().add(radioButton);
                }
                firstRadioButton.setSelected(true);
                chooseModel(modeleMVC, firstRadioButton.getText(), (int)firstRadioButton.getUserData());

                //Listener pour détécter le choix d'une carte
                int finalNumEquipe = numEquipe;
                toggleGroupBoutonsModel.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                    public void changed(ObservableValue<? extends Toggle> ob, Toggle o, Toggle n) {
                        RadioButton radioButton = (RadioButton)toggleGroupBoutonsModel.getSelectedToggle();
                        chooseModel(modeleMVC, radioButton.getText().replace(".txt", ""), finalNumEquipe);
                    }
                });
                boxChoixModels.getChildren().add(boxChoixModel);
            }
            //Choix des équipes à gauche de la border Pane
            borderPane.setCenter(boxChoixModels);

            //Choix seed
            Label seedText = new Label("Seed :");
            TextField seedInput = new TextField();
            seedInput.setPromptText("Entrez la seed");
            modeleMVC.setSeed(new Random().nextLong());
            seedInput.textProperty().addListener((observable, oldValue, newValue) -> {
                // Permet uniquement les chiffres
                if (!newValue.matches("\\d*")) {
                    seedInput.setText(newValue.replaceAll("[^\\d]", ""));
                }
                if (!seedInput.getText().isEmpty()) {
                    modeleMVC.setSeed(Integer.parseInt(seedInput.getText()));
                }
            });
            VBox vboxSeed = new VBox(seedText, seedInput);
            borderPane.setRight(vboxSeed);

            //Boutton Lancer Partie en bas de la border pane
            Button buttonLancerSimulation = new Button("Lancer Simulation");
            //ajout des controles sur le bouton
            buttonLancerSimulation.setOnMouseClicked(controlVue);
            borderPane.setBottom(buttonLancerSimulation);

            this.getChildren().add(borderPane);
        }
    }

    private void chooseModel(ModeleMVC modeleMVC, String modelString, int numEquipe) {
        modeleMVC.setModelEquipeIndexString(modelString, numEquipe);
    }
}
