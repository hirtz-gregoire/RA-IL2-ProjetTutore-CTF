package display.views.MapEditor;

import display.model.MapEditorModel;
import display.model.MapEditorModel.CellType;
import display.model.ModelMVC;
import display.views.View;
import engine.Team;
import engine.map.EditorMap;
import engine.map.Ground;
import engine.map.Wall;
import engine.object.Flag;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.util.HashMap;

public class MapModify extends View {
    private final Image spawnImage = new Image("file:ressources/top/spawn.png", 32, 32, false, false);

    public MapModify(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("MapEditor/MapModify", this.modelMVC);
        MapEditorModel model = (MapEditorModel) modelMVC;
        model.setCellSize(Math.round(800 / Math.max(model.getMap().getWidth(), model.getMap().getHeight()*2)));
        int TAILLE_CASE = model.getCellSize();
        int TAILLE_BUTTON = 64;

        //Récupération de la map séléctionnée et transformation en une EditorMap
        model.setMap(EditorMap.loadFile(model.getFiles()[model.getIndiceMapSelected().get()]));
        System.out.println(model.getMap().getCellTeam(0, 0));

        //Nom de la carte déjà existant mais modifiable
        TextField textFieldMapName = (TextField) pane.lookup("#textFieldMapName");
        textFieldMapName.setText(model.getMap().getName());

        //Menu choix de l'équipe
        Rectangle rectangleTeamChoice = (Rectangle) this.pane.lookup("#rectangleTeamChoice");
        rectangleTeamChoice.setHeight(TAILLE_BUTTON); rectangleTeamChoice.setWidth(TAILLE_BUTTON);
        rectangleTeamChoice.setFill(Color.WHITE);
        rectangleTeamChoice.setStroke(Color.BLACK);

        //Menu choix du type de case
        ImageView imageViewWallButton = (ImageView) this.pane.lookup("#imageViewWallButton");
        ImageView imageViewGroundButton = (ImageView) this.pane.lookup("#imageViewGroundButton");
        ImageView imageViewFlagButton = (ImageView) this.pane.lookup("#imageViewFlagButton");
        ImageView imageViewSpawnButton = (ImageView) this.pane.lookup("#imageViewSpawnButton");

        imageViewWallButton.setImage(Team.getCellSprite(new Wall(null, null), TAILLE_BUTTON));
        imageViewGroundButton.setImage(Team.getCellSprite(new Ground(null, Team.NEUTRAL), TAILLE_BUTTON));
        imageViewFlagButton.setImage(Team.getObjectSprite(new Flag(null, Team.BLUE), TAILLE_BUTTON));
        imageViewSpawnButton.setImage(spawnImage);

        //Cartes
        GridPane gridPaneMapTeam = (GridPane) this.pane.lookup("#gridPaneMapTeam");
        GridPane gridPaneMapCellType = (GridPane) this.pane.lookup("#gridPaneMapCellType");

        int height = model.getMap().getHeight();
        int width = model.getMap().getWidth();

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                HashMap<String, Integer> cellPosition = new HashMap<>();
                cellPosition.put("row", row);
                cellPosition.put("col", col);

                //StackPane cellType
                StackPane stackPane = new StackPane();
                stackPane.setUserData(cellPosition);
                gridPaneMapCellType.add(stackPane, col, row);

                this.changeCellType(model, stackPane, model.getMap().getCellType(row, col));

                stackPane.setOnMouseClicked(event -> {
                    this.changeCellType(model, stackPane, model.getSelectedCellType());
                });

                //Chargement des couleurs des équipes
                Rectangle cell = new Rectangle(TAILLE_CASE, TAILLE_CASE, Team.TeamToColor(Team.numEquipeToTeam(model.getMap().getCellTeam(row, col))));
                cell.setUserData(cellPosition);
                cell.setStroke(Color.BLACK);
                gridPaneMapTeam.add(cell, col, row);

                //Méthode pour changer la team d'une case sur la carte
                cell.setOnMouseClicked(event -> {
                    HashMap<String, Integer> cellPositionValue = (HashMap<String, Integer>) cell.getUserData();
                    int rowValue = cellPositionValue.get("row");
                    int colValue = cellPositionValue.get("col");

                    model.getMap().setCellTeam(rowValue, colValue, model.getSelectedTeam());
                    cell.setFill(Team.TeamToColor(Team.numEquipeToTeam(model.getSelectedTeam())));

                    //Mise à jour de la case modifiée dans la carte des types de cellules
                    this.changeCellType(model, stackPane, model.getMap().getCellType(rowValue, colValue));
                });
            }
        }
        this.update();
    }

    //Méthode pour changer le type de case sur la carte
    public void changeCellType(MapEditorModel model, StackPane stackPane, CellType cellType) {
        HashMap<String, Integer> cellPositionValue = (HashMap<String, Integer>) stackPane.getUserData();
        int row = cellPositionValue.get("row");
        int col = cellPositionValue.get("col");
        int cellTeam = model.getMap().getCellTeam(row, col);
        stackPane.getChildren().clear();
        if (!((cellType == CellType.FLAG || cellType == CellType.SPAWN) && cellTeam == 0)) {
            model.getMap().setCellType(row, col, cellType);
            ImageView imageViewCell = new ImageView();
            ImageView imageViewObject = new ImageView();
            if (cellType == CellType.WALL) {
                imageViewCell.setImage(Team.getCellSprite(new Wall(null, null), model.getCellSize()));
            }
            else {
                imageViewCell.setImage(Team.getCellSprite(new Ground(null, Team.numEquipeToTeam(cellTeam)),  model.getCellSize()));
                //Affichage des objets au-dessus de la case
                switch (cellType) {
                    case CellType.FLAG -> imageViewObject = new ImageView(Team.getObjectSprite(new Flag(null, Team.numEquipeToTeam(cellTeam)),  model.getCellSize()));
                    case CellType.SPAWN -> {
                        imageViewObject = new ImageView(spawnImage);
                        imageViewObject.setFitHeight(model.getCellSize()); imageViewObject.setFitWidth(model.getCellSize());
                    }
                }
            }
            //Rectangle pour avoir une bordure
            Rectangle rectangleBorder = new Rectangle(model.getCellSize(),  model.getCellSize());
            rectangleBorder.setFill(Color.TRANSPARENT);
            rectangleBorder.setStroke(Color.BLACK);
            stackPane.getChildren().addAll(imageViewCell, imageViewObject, rectangleBorder);
        }
        else {
            changeCellType(model, stackPane, CellType.EMPTY);
        }
    }

    @Override
    public void update() {
        super.update();
    }
}
