package display.views;

import display.Display;
import display.controlers.ControlerVue;
import engine.Files;
import engine.map.GameMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import display.modele.ModeleMVC;

import java.io.*;
import java.util.List;

public class VueSimulationGameChoice extends BorderPane implements Observateur {
	public VueSimulationGameChoice() {
		super();
	}

	@Override
	public void actualiser(ModeleMVC modeleMVC) throws Exception {
		this.getChildren().clear();  // efface toute la vue

		if (modeleMVC.getVue().equals(ViewsEnum.SimulationGameChoice)) {
			ControlerVue controlerVue = new ControlerVue(modeleMVC);
			if (Files.getListFilesParties().length > 0) {
				//VBox avec toutes les cartes enregistrées
				VBox vboxParties = new VBox();

				//ToggleGroup pour enregistrer les radios buttons
				ToggleGroup toggleGroup = new ToggleGroup();
				// Variable pour stocker le premier RadioButton
				RadioButton firstRadioButton = null;

				//Boucle avec toutes les parties enregistrées
				for (File fichierPartie : Files.getListFilesParties()) {
					//Lecture du fichier
					BufferedReader reader = new BufferedReader(new FileReader(fichierPartie.getAbsolutePath()));
					String seed = reader.readLine();
					String fichierCarte = reader.readLine();
					String[] modelsEquipes = reader.readLine().split(";");
					String nbJoueurs = reader.readLine();
					String vitesseDeplacement = reader.readLine();
					String tempsReaparition = reader.readLine();

					HBox hboxPartie = new HBox();
					//Petite image de la carte
					try {
						GameMap gameMap = GameMap.loadFile(Files.getFileMapByName(fichierCarte));
						//Label d'affichage des TPS actuels de l'engine
						Display carteImage = new Display(new HBox(), gameMap, 128, null, null, null);
						hboxPartie.getChildren().add(carteImage.getGridPaneCarte());
						//RadioButton pour choisir cette partie
						RadioButton radioButton = new RadioButton(fichierPartie.getName().replace(".txt", ""));
						radioButton.setToggleGroup(toggleGroup);
						radioButton.setUserData(List.of(seed, gameMap.getNbEquipes(), fichierCarte, modelsEquipes, nbJoueurs, vitesseDeplacement, tempsReaparition));
						if (Files.getListFilesParties()[0].equals(fichierPartie)) {
							firstRadioButton = radioButton;
						}
						hboxPartie.getChildren().addAll(radioButton);
					} catch (Exception e) {
						e.printStackTrace();
					}

					//informations sur la partie
					hboxPartie.getChildren().add(new Label("Seed : " + seed));
					for (int i = 0; i < modelsEquipes.length; i++) {
						Label labelModel = new Label("Equipe : " + i + modelsEquipes[i]);
						hboxPartie.getChildren().add(labelModel);
					}
					hboxPartie.getChildren().add(new Label("Nombre de joueurs : " + nbJoueurs));
					hboxPartie.getChildren().add(new Label("Vitesse de déplacement : " + vitesseDeplacement));
					hboxPartie.getChildren().add(new Label("Temps de réaparition  :" + tempsReaparition));
					vboxParties.getChildren().add(hboxPartie);
				}
				firstRadioButton.setSelected(true);
				// Execution manuel du listener pour le premier radioButton
				chooseGame(modeleMVC, (List<Object>) firstRadioButton.getUserData());

				//Boutton pour choisir la partie
				Button buttonLancerPartie = new Button("Lancer Simulation");
				//Ajout des controles sur les boutons
				buttonLancerPartie.setOnMouseClicked(controlerVue);

				//Le menu est une vbox contenu dans une scrollPane
				VBox vBox = new VBox(vboxParties, buttonLancerPartie);
				ScrollPane scrollPane = new ScrollPane();
				scrollPane.setContent(vBox);
				scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
				scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
				scrollPane.setFitToWidth(true); // Adapte la largeur au parent

				this.setCenter(scrollPane);

				//Listener pour détécter le choix d'une carte
				toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
					public void changed(ObservableValue<? extends Toggle> ob, Toggle o, Toggle n) {
						RadioButton rb = (RadioButton) toggleGroup.getSelectedToggle();
						//Enregistrer les informations de la partie dans le modèle
						chooseGame(modeleMVC, (List<Object>) rb.getUserData());
					}
				});
			}
			//Aucune partie présente
			else {
				Label label = new Label("Aucune partie enregistrée !");
				Button buttonNouvellePartie = new Button("Nouvelle Partie");
				buttonNouvellePartie.setOnMouseClicked(controlerVue);
				VBox vBox = new VBox(label, buttonNouvellePartie);
				this.setCenter(vBox);
			}
		}
	}

	public void chooseGame(ModeleMVC modeleMVC, List<Object> datas) {
		modeleMVC.setSeed(Long.parseLong(datas.get(0).toString()));
		modeleMVC.setNbEquipes((int)datas.get(1));
		modeleMVC.setModelsEquipesString(new String[(int)datas.get(1)]);
		modeleMVC.setCarte(datas.get(2).toString());
		modeleMVC.setModelsEquipesString((String[]) datas.get(3));
		modeleMVC.setNbJoueurs(Integer.parseInt(datas.get(4).toString()));
		modeleMVC.setVitesseDeplacement(Integer.parseInt(datas.get(5).toString()));
		modeleMVC.setTempsReaparition(Integer.parseInt(datas.get(6).toString()));
	}
}
