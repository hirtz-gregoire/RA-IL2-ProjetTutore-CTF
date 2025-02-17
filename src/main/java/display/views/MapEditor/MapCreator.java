package display.views.MapEditor;

import display.model.MapEditorModel;
import display.model.MapEditorModel.CellType;
import display.model.ModelMVC;
import display.views.View;
import engine.Team;
import engine.map.Ground;
import engine.map.Wall;
import engine.object.Flag;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.util.HashMap;

public class MapCreator extends View {
    private final int TAILLE_BUTTON = 64;
    private final Image spawnImage = new Image("file:ressources/top/spawn.png", 32, 32, false, false);
    private boolean isDrawing = false;

    public MapCreator(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("MapEditor/MapModify", this.modelMVC);
        MapEditorModel model = (MapEditorModel) modelMVC;
        model.setCellSize(Math.round(800/ Math.max(model.getMap().getWidth(), model.getMap().getHeight()*2)));
        int TAILLE_CASE = model.getCellSize();

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

        gridPaneMapTeam.setOnMousePressed(event -> {
            this.isDrawing = true;
        });
        gridPaneMapTeam.setOnMouseReleased(event -> {
            this.isDrawing = false;
        });

        gridPaneMapCellType.setOnMousePressed(event -> {
            this.isDrawing = true;
        });
        gridPaneMapCellType.setOnMouseReleased(event -> {
            this.isDrawing = false;
        });

        int height = model.getMap().getHeight();
        int width = model.getMap().getWidth();

        int[][] mapTeam = new int[height][width];
        CellType[][] mapCellType = new CellType[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                HashMap<String, Integer> cellPosition = new HashMap<>();
                cellPosition.put("row", row);
                cellPosition.put("col", col);

                //Mise de toutes les cases à vide (ground)
                mapCellType[row][col] = CellType.EMPTY;
                //Image de la case
                Image spriteCell = Team.getCellSprite(new Ground(null, Team.NEUTRAL), TAILLE_CASE);
                ImageView imageView = new ImageView(spriteCell);
                //Rectangle pour avoir une bordure
                Rectangle border = new Rectangle(TAILLE_CASE, TAILLE_CASE);
                border.setFill(Color.TRANSPARENT);
                border.setStroke(Color.BLACK);
                //StackPane qui combine les deux
                StackPane stackPane = new StackPane();
                stackPane.setUserData(cellPosition);
                stackPane.getChildren().addAll(imageView, border);
                gridPaneMapCellType.add(stackPane, col, row);

                stackPane.setOnMouseClicked(event -> {
                    changeCellType(model, stackPane, model.getSelectedCellType());
                });

                stackPane.setOnDragDetected(event -> {
                    stackPane.startFullDrag();
                });

                stackPane.setOnMouseDragEntered(event -> {
                    if (isDrawing) {
                        changeCellType(model, stackPane, model.getSelectedCellType());
                    }
                });

                //Mise de toutes les cases à 0 (zone neutre)
                mapTeam[row][col] = 0;
                Rectangle cell = new Rectangle(TAILLE_CASE, TAILLE_CASE, Color.WHITE);
                cell.setUserData(cellPosition);
                cell.setStroke(Color.BLACK);
                gridPaneMapTeam.add(cell, col, row);

                cell.setOnMouseClicked(event -> {
                    HashMap<String, Integer> cellPositionValue = (HashMap<String, Integer>) cell.getUserData();
                    int rowValue = cellPositionValue.get("row");
                    int colValue = cellPositionValue.get("col");

                    model.getMap().setCellTeam(rowValue, colValue, model.getSelectedTeam());
                    cell.setFill(Team.TeamToColor(Team.numEquipeToTeam(model.getSelectedTeam())));
                    changeCellType(model, stackPane, model.getMap().getCellType(rowValue, colValue));
                });

                cell.setOnDragDetected(event -> {
                    cell.startFullDrag();
                });

                cell.setOnMouseDragEntered(event -> {
                    HashMap<String, Integer> cellPositionValue = (HashMap<String, Integer>) cell.getUserData();
                    int rowValue = cellPositionValue.get("row");
                    int colValue = cellPositionValue.get("col");
                    if (isDrawing) {
                        model.getMap().setCellTeam(rowValue, colValue, model.getSelectedTeam());
                        cell.setFill(Team.TeamToColor(Team.numEquipeToTeam(model.getSelectedTeam())));
                        changeCellType(model, stackPane, model.getMap().getCellType(rowValue, colValue));
                    }
                });
            }
            //Enregistrement des cartes dans le modèle
            model.getMap().setMapTeam(mapTeam);
            model.getMap().setMapCellType(mapCellType);
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
