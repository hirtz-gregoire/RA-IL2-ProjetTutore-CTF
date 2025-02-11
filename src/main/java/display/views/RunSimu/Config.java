package display.views.RunSimu;

import display.model.ModelMVC;
import display.model.RunSimuModel;
import display.views.View;
import engine.Files;
import engine.Team;
import engine.map.GameMap;
import ia.model.Model;
import ia.model.ModelEnum;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

            VBox modelsVBox = new VBox();

            VBox neuralNetworksVBox = new VBox();

            ToggleGroup group = new ToggleGroup();
            boolean first = true;
            for (ModelEnum modelIA : ModelEnum.values()) {
                RadioButton radioButton = new RadioButton(modelIA.toString());
                if (first){
                    radioButton.setSelected(true);
                    first = false;
                }
                if (modelIA.equals(ModelEnum.NeuralNetwork)) {
                    radioButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
                            if (isNowSelected) {
                                neuralNetworksVBox.getChildren().add(new Label("Sélectionnez un modèle de RN existant :"));
                                File[] files = Files.getListSavesFilesModels();
                                for (File file : files) {
                                    if (file.getName().split("\\.")[1].equals("ctf")) {
                                        RadioButton radioButton = new RadioButton(file.getName());
                                        neuralNetworksVBox.getChildren().add(radioButton);
                                    }
                                }
                                team.getChildren().add(neuralNetworksVBox);
                            } else {
                                neuralNetworksVBox.getChildren().clear();
                                team.getChildren().remove(neuralNetworksVBox);
                            }
                        }
                    });
                }
                radioButton.setToggleGroup(group);
                modelsVBox.getChildren().add(radioButton);
            }
            team.getChildren().addAll(modelsVBox);
            listTeams.getChildren().add(team);
        }

        super.update();
    }

    public void updateSeed(){
        RunSimuModel model = (RunSimuModel) modelMVC;
        model.setSeed(new Random().nextLong());
        TextField fiels = (TextField)this.pane.lookup("#seed");
        fiels.setText(String.valueOf(model.getSeed()));
    }
}
