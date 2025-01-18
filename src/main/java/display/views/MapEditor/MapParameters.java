package display.views.MapEditor;

import display.model.MapEditorModel;
import display.model.ModelMVC;
import display.views.View;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import java.io.IOException;

public class MapParameters extends View {

    public MapParameters(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("MapEditor/MapParameters", this.modelMVC);
        MapEditorModel model = (MapEditorModel) modelMVC;

        Slider heightMapSlider = (Slider) pane.lookup("#heightMapSlider");
        Label heightMapLabel = (Label) pane.lookup("#heightMapLabel");
        int heightMapValue = model.getMap().getHeight();

        Slider widthMapSlider = (Slider) pane.lookup("#widthMapSlider");
        Label widthMapLabel = (Label) pane.lookup("#widthMapLabel");
        int widthMapValue = model.getMap().getWidth();

        Slider nbTeamSlider = (Slider) pane.lookup("#nbTeamSlider");
        Label nbTeamLabel = (Label) pane.lookup("#nbTeamLabel");
        int nbTeamValue = model.getMap().getNbTeam();

        heightMapSlider.setMin(1);
        heightMapSlider.setMax(50);
        heightMapSlider.setValue(heightMapValue);
        heightMapLabel.setText(String.valueOf(heightMapValue));

        widthMapSlider.setMin(1);
        widthMapSlider.setMax(50);
        widthMapSlider.setValue(widthMapValue);
        widthMapLabel.setText(String.valueOf(widthMapValue));

        nbTeamSlider.setMin(2);
        nbTeamSlider.setMax(8);
        nbTeamSlider.setValue(nbTeamValue);
        nbTeamLabel.setText(String.valueOf(nbTeamValue));

        this.update();
    }

    @Override
    public void update() {
        super.update();
    }
}
