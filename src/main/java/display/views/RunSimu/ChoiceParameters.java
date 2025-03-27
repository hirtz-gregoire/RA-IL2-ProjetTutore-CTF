package display.views.RunSimu;

import display.model.ModelMVC;
import display.model.RunSimuModel;
import display.views.View;
import engine.Files;
import engine.Team;
import engine.map.GameMap;
import ia.model.ModelEnum;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChoiceParameters extends View {

    public ChoiceParameters(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("RunSimu/ChoiceParameters", this.modelMVC);

        updateSeed();

        this.update();
    }

    @Override
    public void update() {

        RunSimuModel model = (RunSimuModel) modelMVC;

        HBox listTeams = (HBox)((ScrollPane)this.pane.lookup("#listTeams")).getContent();
        listTeams.getChildren().clear();

        GameMap map = model.getMap();
        List<Team> teams = map.getTeams();

        for (int numTeam=0; numTeam<teams.size(); numTeam++) {
            VBox vboxTeam = new VBox();
            vboxTeam.getChildren().add(new Label(teams.get(numTeam).name()));

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
                                vboxTeam.getChildren().add(labelNeuralNetwork);
                                var files = Files.getListSavesFilesModels();
                                boolean first = true;
                                for (File file : files) {
                                    RadioButton radioButton = new RadioButton(file.getName());
                                    if (first){
                                        radioButton.setSelected(true);
                                        first = false;
                                    }
                                    radioButton.setToggleGroup(toggleGroupNN);
                                    neuralNetworksVBox.getChildren().add(radioButton);
                                }
                                vboxTeam.getChildren().add(neuralNetworksVBox);
                            } else {
                                neuralNetworksVBox.getChildren().clear();
                                vboxTeam.getChildren().removeAll(labelNeuralNetwork, neuralNetworksVBox);
                            }
                        }
                    });
                }
                radioButton.setToggleGroup(toggleGroup);
                modelsVBox.getChildren().add(radioButton);
            }
            vboxTeam.getChildren().addAll(modelsVBox);
            listTeams.getChildren().add(vboxTeam);
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
