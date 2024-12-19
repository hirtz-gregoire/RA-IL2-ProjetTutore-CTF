package display.views;

import java.io.*;
import display.Display;
import display.controlers.ControlerVue;
import display.modele.ModeleMVC;
import engine.Files;
import engine.map.GameMap;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class VueSimulationMapChoice extends BorderPane implements Observateur {

	private VBox vBox = new VBox();
	private HBox currentBox;

	private final String defaultColor = "white";
	private final String choseColor = "#9C9C9CFF";


	public VueSimulationMapChoice() {
		super();
	}

	@Override
	public void actualiser(ModeleMVC modeleMVC) throws IOException {
		this.getChildren().clear();
		currentBox = null;

		if (modeleMVC.getVue().equals(ViewsEnum.SimulationMapChoice)) {
			ControlerVue controlerVue = new ControlerVue(modeleMVC);

			##############ME##############################
			if (Files.getListFilesMaps().length > 0) {

				//VBox avec toutes les cartes enregistrées
				VBox cartesBox = new VBox();

				//ToggleGroup pour enregistrer les radios buttons
				ToggleGroup toggleGroup = new ToggleGroup();
				// Variable pour stocker le premier RadioButton
				RadioButton firstRadioButton = null;
				//Boucle avec toutes les cartes enregistrées
				for (File fichierCarte : Files.getListFilesMaps()) {
					//La carte est une HBox
					HBox carteBox = new HBox();

					//Petite image de la carte
					GameMap gameMap = GameMap.loadFile(fichierCarte.getAbsolutePath());
					//Label d'affichage des TPS actuels de l'engine
					Display carteImage = new Display(new HBox(), gameMap, "petit", null, null, null);
					carteBox.getChildren().add(carteImage.getGridPaneCarte());
					//RadioButton pour choisir cette carte
					RadioButton radioButton = new RadioButton(fichierCarte.getName().replace(".txt", ""));
					radioButton.setToggleGroup(toggleGroup);
					radioButton.setUserData(gameMap.getNbEquipes());
					//Enregistrement du premier RadioButton
					if (Files.getListFilesMaps()[0].equals(fichierCarte)) {
						firstRadioButton = radioButton;
					}
					carteBox.getChildren().add(radioButton);
					cartesBox.getChildren().add(carteBox);
				}
				firstRadioButton.setSelected(true);
				// Execution manuel du listener pour le premier radioButton
				chooseMap(modeleMVC, firstRadioButton.getText(), (int) firstRadioButton.getUserData());

				Button buttonChoisirParametres = new Button("Choisir paramètres");
				//Ajout des controles sur les boutons
				buttonChoisirParametres.setOnMouseClicked(controlerVue);

				//Le menu est une vbox contenu dans une scrollPane
				VBox vBox = new VBox(cartesBox, buttonChoisirParametres);
				ScrollPane scrollPane = new ScrollPane();
				scrollPane.setContent(vBox);
				scrollPane.setFitToWidth(true); // Adapte la largeur au parent

				this.setCenter(scrollPane);

				//Listener pour détécter le choix d'une carte
				toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
					public void changed(ObservableValue<? extends Toggle> ob, Toggle o, Toggle n) {
						RadioButton rb = (RadioButton) toggleGroup.getSelectedToggle();
						//Enregistrer la carte et le nombre d'équipes choisie dans le modèle pour la génération de la partie
						chooseMap(modeleMVC, rb.getText(), (int) rb.getUserData());
					}
				});
			}
			else {
				Label label = new Label("Aucune map enregistrée !");
				Button buttonNouvelleCarte = new Button("Cartes");
				buttonNouvelleCarte.setOnMouseClicked(controlerVue);
				VBox vBox = new VBox(label, buttonNouvelleCarte);
				this.setCenter(vBox);
			}
		}
	}

	public void chooseMap(ModeleMVC modeleMVC, String nomMap, int nbEquipes) {
		modeleMVC.setCarte(nomMap);
		modeleMVC.setNbEquipes(nbEquipes);
		modeleMVC.setModelsEquipesString(new String[nbEquipes]);
		##################################ME####################################
			##########################################G###########################
			BorderPane bp = new BorderPane();

			Label title = new Label("Choisir Map");
			bp.setTop(title);

			ScrollPane scrollPane = new ScrollPane();
			VBox scrollPaneMain = new VBox();

			for (File fichierCarte : Files.getListFilesMaps()) {
				HBox map_item = new HBox();
					map_item.setPadding(new Insets(10));

					map_item.setStyle("-fx-background-color: " + defaultColor + ";");
					map_item.getChildren().add(new Label(fichierCarte.getName()));

					map_item.setOnMouseClicked((MouseEvent e) -> {
						vBox.getChildren().clear();

						if (currentBox != null) {
							currentBox.setStyle("-fx-background-color: " + defaultColor + ";");
						}

						HBox hb = (HBox)e.getSource();
						if (hb == currentBox) {
							currentBox = null;
						}else{
							currentBox = hb;


							currentBox.setStyle("-fx-background-color: " + choseColor + ";");
							Label label = (Label)currentBox.getChildren().getFirst();
							vBox.getChildren().add(new Label(label.getText()));

							try{
								GameMap gameMap = GameMap.loadFile(fichierCarte.getAbsolutePath());

								modele.setCarte(fichierCarte.getName());
								modele.setNbEquipes(gameMap.getNbEquipes());

								System.out.println(gameMap);
								Display carteImage = new Display(new HBox(), gameMap, 512, null, null, null);
								vBox.getChildren().add(carteImage.getGridPaneCarte());

							}catch (IOException exception){
								System.out.println("Erreur des chargement de la map");
							}
						}
					});
				scrollPaneMain.getChildren().add(map_item);
			}
			scrollPane.setContent(scrollPaneMain);
			bp.setLeft(scrollPane);

			VBox showInfo = this.vBox;
			bp.setCenter(showInfo);

			Button buttonChoisirParametres = new Button("Choisir paramètres");
			//Ajout des controles sur les boutons
			buttonChoisirParametres.setOnMouseClicked((MouseEvent e) -> {
				Button b = (Button) e.getSource();
				if (currentBox != null) {
					modele.setVue(ViewsEnum.SimulationParametersChoice);
					modele.setModelsEquipes(new String[modele.getNbEquipes()]);
					try{
						modele.notifierObservateurs();
					}catch (Exception exception){}
				}else{
					b.setStyle("-fx-background-color: red;");
				}
			});

			bp.setBottom(buttonChoisirParametres);

			this.setCenter(bp);
		}
	}


	public void chooseMap(Modele modele, String nomMap, int nbEquipes) {
		modele.setCarte(nomMap);
		modele.setNbEquipes(nbEquipes);
		modele.setModelsEquipes(new String[nbEquipes]);
		########################################G################################
	}
}
