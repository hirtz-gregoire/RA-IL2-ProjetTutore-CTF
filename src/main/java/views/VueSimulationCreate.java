package views;

import controlers.ControlerVue;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import modele.Modele;

public class VueSimulationCreate extends Pane implements Observateur {
    public VueSimulationCreate() {
        super();
    }

    @Override
    public void actualiser(Modele modele) {
        this.getChildren().clear();  // efface toute la vue

        //on n'utilise la vue que si la vue est en colonne
        if (modele.getVue().equals("simulation_creation")) {
            Color textColor = Color.BLACK;

            //controleur pour modifier, créer tache et liste
            ControlerVue controlVue = new ControlerVue(modele);

            BorderPane borderPane = new BorderPane();

            //Paramètres au millieu de la border pane
            //Dans une grid
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(10, 10, 10, 10));
            grid.setVgap(10);
            grid.setHgap(70);
            //Choix temps de réapration
            Slider tempsReaparition = new Slider(5, 50, 5);
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
            });
            GridPane.setConstraints(tempsReaparition, 1, 0);
            tempsReaparitionValue.setTextFill(textColor);
            GridPane.setConstraints(tempsReaparitionValue, 2, 0);
            //Choix Nombre de joueurs
            Slider nombreJoueur = new Slider(3, 10, 3);
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
            });
            GridPane.setConstraints(nombreJoueur, 1, 1);
            nombreJoueurValue.setTextFill(textColor);
            GridPane.setConstraints(nombreJoueurValue, 2, 1);
            //Choix Vitesse de déplacement
            Slider vitesseDeplacement = new Slider(1, 10, 1);
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
            });
            GridPane.setConstraints(vitesseDeplacement, 1, 2);
            vitesseDeplacementValue.setTextFill(textColor);
            GridPane.setConstraints(vitesseDeplacementValue, 2, 2);

            grid.getChildren().addAll(tempsReaparitionText, tempsReaparition, tempsReaparitionValue,
                    nombreJoueurText, nombreJoueur, nombreJoueurValue,
                    vitesseDeplacementText, vitesseDeplacement, vitesseDeplacementValue);
            borderPane.setCenter(grid);

            //Boutons choix modèles équipe1
            RadioButton button1 = new RadioButton("Agent aléatoire 1");
            RadioButton button2 = new RadioButton("Renforcement RN 1");
            RadioButton button3 = new RadioButton("Renforcement RN 2");
            RadioButton button4 = new RadioButton("Algorithme Génétique RN 1");
            ToggleGroup groupeBoutons = new ToggleGroup();
            button1.setToggleGroup(groupeBoutons);
            button2.setToggleGroup(groupeBoutons);
            button3.setToggleGroup(groupeBoutons);
            button4.setToggleGroup(groupeBoutons);
            VBox choixModeleEquipe1 = new VBox(5);
            choixModeleEquipe1.setFillWidth(false);
            choixModeleEquipe1.setPadding(new Insets(5, 5, 5, 50));
            choixModeleEquipe1.getChildren().addAll(button1, button2, button3, button4);
            //Choix équipe 1 à gauche de la border pane
            borderPane.setLeft(choixModeleEquipe1);

            //Boutons choix modèles équipe2
            RadioButton button5 = new RadioButton("Agent aléatoire 1");
            RadioButton button6 = new RadioButton("Renforcement RN 1");
            RadioButton button7 = new RadioButton("Renforcement RN 2");
            RadioButton button8 = new RadioButton("Algorithme Génétique RN 1");
            ToggleGroup groupeBoutons2 = new ToggleGroup();
            button5.setToggleGroup(groupeBoutons2);
            button6.setToggleGroup(groupeBoutons2);
            button7.setToggleGroup(groupeBoutons2);
            button8.setToggleGroup(groupeBoutons2);
            VBox choixModeleEquipe2 = new VBox(5);
            choixModeleEquipe2.setFillWidth(false);
            choixModeleEquipe2.setPadding(new Insets(5, 5, 5, 50));
            choixModeleEquipe2.getChildren().addAll(button5, button6, button7, button8);
            //Choix équipe 2 à droite de la border pane
            borderPane.setRight(choixModeleEquipe2);

            //Boutton Lancer Partie en bas de la border pane
            Button buttonLancerSimulation = new Button("Lancer Simulation");
            //on assigne le bouton à la liste correspondante
            buttonLancerSimulation.setMinWidth(50);
            buttonLancerSimulation.setMinHeight(15);
            //ajout des controles sur le bouton
            buttonLancerSimulation.setOnMouseClicked(controlVue);
            borderPane.setBottom(buttonLancerSimulation);

            borderPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(2))));

            this.getChildren().add(borderPane);
        }
    }
}
