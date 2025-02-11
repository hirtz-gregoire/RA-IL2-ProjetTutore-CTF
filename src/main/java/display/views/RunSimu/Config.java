package display.views.RunSimu;

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

public class Config extends View {

    public Config(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("RunSimu/Config", this.modelMVC);

        updateSeed();

        this.update();
    }

    @Override
    public void update() {

        HBox listTeams = (HBox) this.pane.lookup("#listTeams");
        listTeams.getChildren().clear();

        RunSimuModel model = (RunSimuModel) modelMVC;
        GameMap map = model.getMap();
        List<Team> teams = map.getTeams();

        for (int i=0; i<teams.size(); i++) {
            VBox team = new VBox();
            team.getChildren().add(new Label(teams.get(i).name()));

            VBox models = new VBox();
                ToggleGroup group = new ToggleGroup();
                boolean first = true;
                for (ModelEnum m : ModelEnum.values()){
                    RadioButton rb = new RadioButton(m.toString());
                    if (first){
                        rb.setSelected(true);
                        first = false;
                    }
                    rb.setToggleGroup(group);
                    models.getChildren().add(rb);
                }
            team.getChildren().add(models);
            listTeams.getChildren().add(team);
        }

        super.update();
    }

    public void updateSeed(){
        System.out.println("Updating seed");
        RunSimuModel model = (RunSimuModel) modelMVC;
        model.setSeed(new Random().nextLong());
        TextField fiels = (TextField)this.pane.lookup("#seed");
        fiels.setText(String.valueOf(model.getSeed()));
    }

}
