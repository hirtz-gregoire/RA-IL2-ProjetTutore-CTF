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

        RunSimuModel model = (RunSimuModel) modelMVC;
        GameMap map = model.getMap();
        List<Team> teams = map.getTeams();

        for (int numTeam=0; numTeam<teams.size(); numTeam++) {
            VBox team = new VBox();
            team.getChildren().add(new Label(teams.get(numTeam).name()));

            VBox modelsVBox = new VBox();
            VBox neuralNetworksVBox = new VBox();
            Label labelNeuralNetwork = new Label("Choix RN :");

            ToggleGroup toggleGroup = new ToggleGroup();
            boolean first = true;
            for (ModelEnum modelAgent : ModelEnum.values()) {
                RadioButton radioButton = new RadioButton(modelAgent.toString());
                if (first){
                    radioButton.setSelected(true);
                    first = false;
                }
                if (modelAgent.equals(ModelEnum.NeuralNetwork)) {
                    radioButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
                            if (isNowSelected) {
                                ToggleGroup toggleGroupNN = new ToggleGroup();
                                team.getChildren().add(labelNeuralNetwork);
                                File[] files = Files.getListSavesFilesModels();
                                boolean first = true;
                                for (File file : files) {
                                    if (file.getName().split("\\.")[1].equals("ctf")) {
                                        RadioButton radioButton = new RadioButton(file.getName());
                                        if (first){
                                            radioButton.setSelected(true);
                                            first = false;
                                        }
                                        radioButton.setToggleGroup(toggleGroupNN);
                                        neuralNetworksVBox.getChildren().add(radioButton);
                                    }
                                }
                                team.getChildren().add(neuralNetworksVBox);
                            } else {
                                neuralNetworksVBox.getChildren().clear();
                                team.getChildren().removeAll(labelNeuralNetwork, neuralNetworksVBox);
                            }
                        }
                    });
                }
                radioButton.setToggleGroup(toggleGroup);
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
