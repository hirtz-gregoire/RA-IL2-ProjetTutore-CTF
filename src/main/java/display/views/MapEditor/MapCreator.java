package display.views.MapEditor;

import display.model.ModelMVC;
import display.views.View;
import engine.Team;
import engine.map.Ground;
import engine.map.Wall;
import engine.object.Flag;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class MapCreator extends View {
    private final int TAILLE_CASE = 16;
    @FXML
    private GridPane gridPaneMapTeam;
    @FXML
    private GridPane gridPaneMapCellType;
    @FXML
    private ImageView imageViewWallButton;
    @FXML
    private ImageView imageViewGroundButton;
    @FXML
    private ImageView imageViewFlagButton;
    @FXML
    private ImageView imageViewSpawnButton;

    public MapCreator(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("MapEditor/MapCreator", this.modelMVC);

        imageViewWallButton.setImage(Team.getCellSprite(new Wall(null, null), TAILLE_CASE));
        imageViewGroundButton.setImage(Team.getCellSprite(new Ground(null, Team.NEUTRAL), TAILLE_CASE));
        imageViewFlagButton.setImage(Team.getObjectSprite(new Flag(null, Team.BLUE), TAILLE_CASE));
        //Image du spawn à rajouter
        //imageViewSpawnButton.setImage();
        int height = modelMVC.getHeightMap();
        int width = modelMVC.getWidthMap();
        int nbTeam = modelMVC.getNbTeam();

        mapTeam = new int[height][width];
        mapCellType = new CellType[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                mapTeam[row][col] = 0;
                Rectangle cell = new Rectangle(TAILLE_CASE, TAILLE_CASE, Color.WHITE);
                gridPaneMapTeam.add(cell, row, col);

                mapCellType[row][col] = CellType.VIDE;
                Image spriteCell = Team.getCellSprite(new Ground(new Coordinate(0, 0), Team.NEUTRAL), TAILLE_CASE);
                ImageView imageView = new ImageView(spriteCell);
                GridPane.setConstraints(imageView, row, col);
                gridPaneMapCellType.getChildren().add(imageView);
            }
        }

        this.update();
    }

    @Override
    public void update() {
        super.update();
    }
}
