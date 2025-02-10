package display.views.Learning;

import display.model.LearningModel;
import display.model.ModelMVC;
import display.model.RunSimuModel;
import display.views.View;
import engine.Team;
import engine.map.GameMap;
import ia.model.ModelEnum;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class ChoiceParameters extends View {

    public ChoiceParameters(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("Learning/ChoiceParameters", this.modelMVC);
        this.update();
    }

    @Override
    public void update() {
        VBox modelEnemyHBox = (VBox) this.pane.lookup("#listModelsEnemy");

        ToggleGroup group = new ToggleGroup();
        boolean first = true;
        for (ModelEnum m : ModelEnum.values()){
            RadioButton rb = new RadioButton(m.toString());
            if (first){
                rb.setSelected(true);
                first = false;
            }
            rb.setToggleGroup(group);
            modelEnemyHBox.getChildren().add(rb);
        }

        super.update();
    }
}
