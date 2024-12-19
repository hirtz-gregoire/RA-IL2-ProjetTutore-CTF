package display.views;

import java.io.*;
import display.Display;
import display.controlers.ControlerVue;
import display.modele.Modele;
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
	public void actualiser(Modele modele) throws Exception {
		this.getChildren().clear();
		currentBox = null;

		if (modele.getVue().equals(ViewsEnum.SimulationMapChoice)) {
			ControlerVue control = new ControlerVue(modele);

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
	}
}
