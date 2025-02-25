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
        int height = (int) heightMapSlider.getValue();
        heightMapLabel.setText(String.valueOf(height));
        //Ajustement de la largeur et du nombre d'équipes
        int minWidth;
        if (height == 1) {
            minWidth = 4;
        }
        else if (height == 2) {
            minWidth = 2;
        }
        else minWidth = 1;
        widthMapSlider.setMin(minWidth);
        if (widthMapSlider.getValue() < minWidth) {
            widthMapSlider.setValue(minWidth);
            widthMapLabel.setText(String.valueOf(minWidth));
        }
        setNbMaxTeam();
    }

    public void setWidthMap() {
        int width = (int) widthMapSlider.getValue();
        widthMapLabel.setText(String.valueOf(width));
        //Ajustement de la hauteur et du nombre d'équipes
        int minHeight;
        if (width == 1) {
            minHeight = 4;
        }
        else if (width == 2) {
            minHeight = 2;
        }
        else {
            minHeight = 1;
        }
        heightMapSlider.setMin(minHeight);
        if (heightMapSlider.getValue() < minHeight) {
            heightMapSlider.setValue(minHeight);
            heightMapLabel.setText(String.valueOf(minHeight));
        }
        setNbMaxTeam();
    }

    public void setNbTeam() {
        int nbTeam = (int) nbTeamSlider.getValue();
        nbTeamLabel.setText(String.valueOf(nbTeam));
    }

    public void setNbMaxTeam() {
        int height = (int) heightMapSlider.getValue();
        int width = (int) widthMapSlider.getValue();
        int taille = height * width;
        int nbEquipesMax = Math.min(model.getGlobalModel().getNbTeamMax(), Math.round(taille/2));

        if ((int) nbTeamSlider.getValue() > nbEquipesMax) {
            nbTeamSlider.setValue(nbEquipesMax);
            nbTeamLabel.setText(String.valueOf(nbEquipesMax));
        }
        nbTeamSlider.setMax(nbEquipesMax);
    }

    public void mapCreator() {
        MapEditorModel model = (MapEditorModel) this.model;
        model.setActualMapEditorView(EnumMapEditor.MapCreator);

        //Récupération des valeurs
        model.getMap().setHeight((int) heightMapSlider.getValue());
        model.getMap().setWidth((int) widthMapSlider.getValue());
        model.getMap().setNbTeam((int) nbTeamSlider.getValue());

        model.update();
        model.getGlobalModel().updateRacine();
    }
}
