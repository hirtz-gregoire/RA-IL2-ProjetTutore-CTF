package display.controllers.MapEditor;

import display.controllers.Controller;
import display.model.MapEditorModel;
import display.views.MapEditor.EnumMapEditor;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class MapParametersController extends Controller {

    @FXML
    private Slider heightMapSlider;
    @FXML
    private Label heightMapLabel;
    @FXML
    private Slider widthMapSlider;
    @FXML
    private Label widthMapLabel;
    @FXML
    private Slider nbTeamSlider;
    @FXML
    private Label nbTeamLabel;

    public void setHeightMap() {
        MapEditorModel model = (MapEditorModel) this.model;
        int height = (int) heightMapSlider.getValue();
        model.setHeightMap(height);
        heightMapLabel.setText(String.valueOf(height));
        setNbTeamMax();
    }

    public void setWidthMap() {
        MapEditorModel model = (MapEditorModel) this.model;
        int width = (int) widthMapSlider.getValue();
        model.setWidthMap(width);
        widthMapLabel.setText(String.valueOf(width));
        setNbTeamMax();
    }

    public void setNbTeam() {
        MapEditorModel model = (MapEditorModel) this.model;
        int nbTeam = (int) nbTeamSlider.getValue();
        model.setNbTeam(nbTeam);
        nbTeamLabel.setText(String.valueOf(nbTeam));
    }

    public void setNbTeamMax() {
        int height = (int) heightMapSlider.getValue();
        int width = (int) widthMapSlider.getValue();
        int taille = height * width;
        MapEditorModel model = (MapEditorModel) this.model;
        int nbEquipesMax = Math.min(model.getGlobalModel().getNbTeamMax(), Math.round(taille/2));

        nbTeamSlider.setMax(nbEquipesMax);
        if (nbTeamSlider.getValue() > nbEquipesMax) {
            nbTeamSlider.setValue(nbEquipesMax);
            nbTeamLabel.setText(String.format("%.2f", nbEquipesMax));
        }
    }

    public void mapCreator() {
        MapEditorModel model = (MapEditorModel) this.model;
        model.setActualMapEditorView(EnumMapEditor.MapCreator);

        model.update();
        model.getGlobalModel().updateRacine();
    }
}
