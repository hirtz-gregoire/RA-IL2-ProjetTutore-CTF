package display.controllers.RunSimu;

import display.controllers.Controller;
import display.model.RunSimuModel;
import display.views.RunSimu.Config;
import display.views.RunSimu.EnumRunSimu;
import ia.model.ModelEnum;
import ia.model.NeuralNetworks.ModelNeuralNetwork;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigController extends Controller {

    @FXML
    private Spinner<Integer> respawnTime;
    @FXML
    private Spinner<Integer> nbPlayers;
    @FXML
    private Spinner<Integer> speedPlayers;
    @FXML
    private HBox listTeams;

    @FXML
    public void initialize() {

        respawnTime.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 10));
        nbPlayers.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 3));
        speedPlayers.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));

        respawnTime.setEditable(true);
        nbPlayers.setEditable(true);

        addNumericValidationToSpinner(respawnTime);
        addFocusValidationToSpinner(respawnTime);

        addNumericValidationToSpinner(nbPlayers);
        addFocusValidationToSpinner(nbPlayers);
    }

    private void addNumericValidationToSpinner(Spinner<Integer> spinner) {
        spinner.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                spinner.getEditor().setText(oldValue);
            } else {
                try {
                    int value = Integer.parseInt(newValue);
                    spinner.getValueFactory().setValue(value);
                } catch (NumberFormatException e) {
                }
            }
        });
    }

    private void addFocusValidationToSpinner(Spinner<Integer> spinner) {
        // Écouteur sur la propriété de focus de l'éditeur
        spinner.getEditor().focusedProperty().addListener((observable, oldFocused, newFocused) -> {
            if (!newFocused) {
                String text = spinner.getEditor().getText();

                if (text.isEmpty() || !text.matches("\\d*")) {
                    spinner.getEditor().setText("1");
                    spinner.getValueFactory().setValue(1);
                } else {
                    try {
                        int value = Integer.parseInt(text);
                        spinner.getValueFactory().setValue(value);
                    } catch (NumberFormatException e) {
                        spinner.getEditor().setText("1");
                        spinner.getValueFactory().setValue(1);
                    }
                }
            }
        });
    }

    public void nextMenu(){
        RunSimuModel model = (RunSimuModel) this.model;
        model.setEnumRunSimu(EnumRunSimu.Main);

        model.setRespawnTime(respawnTime.getValue());
        model.setNbPlayers(nbPlayers.getValue());
        model.setSpeedPlayers(speedPlayers.getValue());

        List<Node> list = listTeams.getChildren();
        List<ModelEnum> modelByTeam = new ArrayList<>();
        List<String> neuralNetworksByTeam = new ArrayList<>();

        for (int numTeam = 0; numTeam < list.size(); numTeam++) {
            VBox team = (VBox) list.get(numTeam);
            VBox models = (VBox) team.getChildren().get(1);

            for (int j=0; j<models.getChildren().size(); j++) {
                RadioButton rb = (RadioButton) models.getChildren().get(j);
                if (rb.isSelected()) {
                    modelByTeam.add(ModelEnum.getEnum(j));
                    break;
                }
            }

            //S'il y a un model de NN choisit
            if (team.getChildren().size() == 4) {
                VBox modelsNN = (VBox) team.getChildren().get(3);
                for (int j=0; j<modelsNN.getChildren().size(); j++) {
                    RadioButton rb = (RadioButton) modelsNN.getChildren().get(j);
                    if (rb.isSelected()) {
                        neuralNetworksByTeam.add("ressources/models/"+rb.getText());
                    }
                }
            }
            else {
                neuralNetworksByTeam.add(null);
            }
        }
        model.setModelByTeam(modelByTeam);
        model.setNeuralNetworkByTeam(neuralNetworksByTeam);

        model.update();
        model.getGlobalModel().updateRacine();
    }

    public void btnSeed(){
        RunSimuModel model = (RunSimuModel) this.model;
        Config config = (Config) model.getView();
        config.updateSeed();
    }
}
