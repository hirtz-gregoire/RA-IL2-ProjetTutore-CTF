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

	private VBox vboxMapImage = new VBox();
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
		vboxMapImage.getChildren().clear();

		if (modeleMVC.getVue().equals(ViewsEnum.SimulationMapChoice)) {
			ControlerVue controlerVue = new ControlerVue(modeleMVC);

			if (Files.getListFilesMaps().length > 0) {

				Label title = new Label("Choisir Map");
				this.setTop(title);

				ScrollPane scrollPaneMaps = new ScrollPane();
				VBox vboxMaps = new VBox();

				for (File fichierCarte : Files.getListFilesMaps()) {
					HBox hboxMap = new HBox();
					hboxMap.setPadding(new Insets(10));
					hboxMap.setStyle("-fx-background-color: " + defaultColor + ";");
					hboxMap.getChildren().add(new Label(fichierCarte.getName()));

					hboxMap.setOnMouseClicked((MouseEvent e) -> {
						vboxMapImage.getChildren().clear();
						//Si une autre map a été séléctionnée avant, on remet son label en blanc
						if (currentBox != null) {
							currentBox.setStyle("-fx-background-color: " + defaultColor + ";");
						}
						HBox hboxSelectedMap = (HBox)e.getSource();
						if (hboxSelectedMap == currentBox) {
							currentBox = null;
						}
						else {
							currentBox = hboxSelectedMap;
							currentBox.setStyle("-fx-background-color: " + choseColor + ";");

							try {
								GameMap gameMap = GameMap.loadFile(fichierCarte.getAbsolutePath());
								chooseMap(modeleMVC, fichierCarte.getName().replace(".txt", ""), gameMap.getNbEquipes());

								Display carteImage = new Display(new HBox(), gameMap, 512, null, null, null);
								vboxMapImage.getChildren().add(carteImage.getGridPaneCarte());

							}
							catch (IOException exception) {
								System.out.println("Erreur de chargement de la map");
							}
						}
					});
					vboxMaps.getChildren().add(hboxMap);
				}
				scrollPaneMaps.setContent(vboxMaps);
				this.setLeft(scrollPaneMaps);

				VBox showInfo = this.vboxMapImage;
				this.setCenter(showInfo);

				Button buttonChoisirParametres = new Button("Choisir paramètres");
				//Ajout des controles sur les boutons
				buttonChoisirParametres.setOnMouseClicked((MouseEvent e) -> {
					Button button = (Button) e.getSource();
					if (currentBox != null) {
						modeleMVC.setVue(ViewsEnum.SimulationParametersChoice);
						modeleMVC.setModelsEquipesString(new String[modeleMVC.getNbEquipes()]);
						try{
							modeleMVC.notifierObservateurs();
						}
						catch (Exception exception){}
					}
					else{
						button.setStyle("-fx-background-color: red;");
					}
				});
				this.setBottom(buttonChoisirParametres);
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
	}
}
