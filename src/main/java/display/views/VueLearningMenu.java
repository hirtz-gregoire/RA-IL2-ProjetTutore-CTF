package display.views;

import display.controlers.ControlerVue;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import display.modele.ModeleMVC;

public class VueLearningMenu extends StackPane implements Observateur {

	public VueLearningMenu() {
		super();
	}

	@Override
	public void actualiser(ModeleMVC modeleMVC) {
		this.getChildren().clear();
		//on n'utilise la vue que si la vue est en liste
		if (modeleMVC.getVue().equals(ViewsEnum.LearningMenu)) {
			Color textColor = Color.BLACK;

			//controleur pour modifier, créer tache et liste
			ControlerVue controlVue = new ControlerVue(modeleMVC);

			BorderPane borderPane = new BorderPane();

			//Paramètres au millieu de la border pane
			//Dans une grid
			GridPane grid = new GridPane();
			//Choix temps de réapration
			Slider nombreRayons = new Slider(5, 50, 5);
			nombreRayons.setMajorTickUnit(1);         // Espacement entre les ticks principaux
			nombreRayons.setMinorTickCount(0);        // Pas de ticks intermédiaires
			nombreRayons.setSnapToTicks(true);        // Alignement sur les ticks
			nombreRayons.setShowTickMarks(true);      // Afficher les ticks
			nombreRayons.setShowTickLabels(true);     // Afficher les labels
			Label nombreRayonsText = new Label("Nombre de rayons :");
			Label nombreRayonsValue = new Label(Double.toString(nombreRayons.getValue()));
			nombreRayonsText.setTextFill(textColor);
			GridPane.setConstraints(nombreRayonsText, 0, 0);
			nombreRayons.valueProperty().addListener((
					ObservableValue<? extends Number> ov,
					Number old_val, Number new_val) -> {
				nombreRayonsValue.setText(String.format("%.2f", new_val));
			});
			GridPane.setConstraints(nombreRayons, 1, 0);
			nombreRayonsValue.setTextFill(textColor);
			GridPane.setConstraints(nombreRayonsValue, 2, 0);
			//Choix Nombre de joueurs
			Slider champVision = new Slider(3, 10, 3);
			champVision.setMajorTickUnit(1);         // Espacement entre les ticks principaux
			champVision.setMinorTickCount(0);        // Pas de ticks intermédiaires
			champVision.setSnapToTicks(true);        // Alignement sur les ticks
			champVision.setShowTickMarks(true);      // Afficher les ticks
			champVision.setShowTickLabels(true);     // Afficher les labels
			Label champVisionText = new Label("Champ de vision :");
			Label champVisionValue = new Label(Double.toString(champVision.getValue()));
			champVisionText.setTextFill(textColor);
			GridPane.setConstraints(champVisionText, 0, 1);
			champVision.valueProperty().addListener((
					ObservableValue<? extends Number> ov,
					Number old_val, Number new_val) -> {
				champVisionValue.setText(String.format("%.2f", new_val));
			});
			GridPane.setConstraints(champVision, 1, 1);
			champVisionValue.setTextFill(textColor);
			GridPane.setConstraints(champVisionValue, 2, 1);
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

			grid.getChildren().addAll(nombreRayonsText, nombreRayons, nombreRayonsValue,
					champVisionText, champVision, champVisionValue,
					vitesseDeplacementText, vitesseDeplacement, vitesseDeplacementValue);
			borderPane.setCenter(grid);

			//Boutons choix modèles équipe1
			RadioButton button1 = new RadioButton("Renforcement RN");
			RadioButton button2 = new RadioButton("Algorithme Génétique RN");
			ToggleGroup groupeBoutons = new ToggleGroup();
			button1.setToggleGroup(groupeBoutons);
			button2.setToggleGroup(groupeBoutons);
			VBox choixModeleEquipe1 = new VBox(5);
			choixModeleEquipe1.setFillWidth(false);
			choixModeleEquipe1.getChildren().addAll(button1, button2);
			//Choix équipe 1 à gauche de la border pane
			borderPane.setLeft(choixModeleEquipe1);


			//Boutton Lancer Partie en bas de la border pane
			Button buttonLancerSimulation = new Button("Lancer Apprentissage");
			//ajout des controles sur le bouton
			buttonLancerSimulation.setOnMouseClicked(controlVue);
			borderPane.setBottom(buttonLancerSimulation);

			this.getChildren().add(borderPane);
		}
	}
}