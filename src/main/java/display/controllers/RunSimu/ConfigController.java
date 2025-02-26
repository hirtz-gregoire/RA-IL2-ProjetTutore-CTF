package display.controllers.RunSimu;

import display.controllers.Controller;
import display.model.RunSimuModel;
import display.views.RunSimu.Config;
import display.views.RunSimu.EnumRunSimu;
import ia.model.ModelEnum;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ConfigController extends Controller {

    @FXML
    public TextField seed;
    @FXML
    private CheckBox checkBoxPlaySelf;
    @FXML
    private Spinner<Integer> respawnTime;
    @FXML
    private Spinner<Integer> nbPlayers;
    @FXML
    private Spinner<Double> speedPlayers;
    @FXML
    private Spinner<Integer> maxTurns;
    @FXML
    private HBox listTeamsHBox;

    @FXML
    public void initialize() {

        respawnTime.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 10));
        nbPlayers.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 3));
        speedPlayers.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(1, 10, 1));
        maxTurns.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0,1000));

        respawnTime.setEditable(true);
        nbPlayers.setEditable(true);
        speedPlayers.setEditable(true);
        maxTurns.setEditable(true);
        seed.setEditable(true);

        addNumericValidationToSpinner(respawnTime);
        addFocusValidationToSpinner(respawnTime);

        addNumericValidationToSpinner(nbPlayers);
        addFocusValidationToSpinner(nbPlayers);

        addNumericValidationToSpinner(maxTurns);
        addFocusValidationToSpinner(maxTurns);
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
        model.setMaxTurns(maxTurns.getValue());

        model.setPlaySelf(checkBoxPlaySelf.isSelected());

        model.setSeed(Long.parseLong(seed.getCharacters().toString()));

        List<Node> list = listTeamsHBox.getChildren();
        List<List<ModelEnum>> modelList = new ArrayList<>();

        List<String> neuralModelNames = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            VBox team = (VBox) list.get(i);
            VBox models = (VBox) team.getChildren().get(1);
            List<ModelEnum> teamModels = new ArrayList<>();

            boolean find = false;
            for (int j=0; j<models.getChildren().size(); j++) {
                RadioButton rb = (RadioButton) models.getChildren().get(j);
                if (rb.isSelected()) {
                    for(int k=0;k<model.getNbPlayers();k++)
                        teamModels.add(ModelEnum.getEnum(j));
                    if(team.getChildren().size()>2){
                        VBox modelNames = (VBox) team.getChildren().get(3);
                        for(Node node : modelNames.getChildren()) {
                            if(node instanceof RadioButton && ((RadioButton) node).isSelected()) {
                                neuralModelNames.add(((RadioButton) node).getText());
                            }
                        }
                    }
                    else neuralModelNames.add("");
                    find = true;
                    break;
                }
            }
            if (!find) {
                teamModels.add(ModelEnum.Random);
            }
            modelList.add(teamModels);
        }
        model.setModelList(modelList);

        model.setNeuralNetworkTeam(neuralModelNames);
        model.update();
        model.getGlobalModel().updateRacine();
    }

    public void btnSeed(){
        System.out.println("btnSeed");
        RunSimuModel model = (RunSimuModel) this.model;
        Config config = (Config) model.getView();
        config.updateSeed();
    }

}
