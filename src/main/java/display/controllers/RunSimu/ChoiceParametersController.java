package display.controllers.RunSimu;

import display.controllers.Controller;
import display.controllers.SpinnerVerification;
import display.model.RunSimuModel;
import display.views.RunSimu.ChoiceParameters;
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

public class ChoiceParametersController extends Controller {

    @FXML
    public TextField seed;
    @FXML
    public CheckBox checkBoxPlayHuman;
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

        SpinnerVerification.addNumericValidationToSpinnerInteger(respawnTime);
        SpinnerVerification.addFocusValidationToSpinnerInteger(respawnTime);
        SpinnerVerification.addNumericValidationToSpinnerInteger(nbPlayers);
        SpinnerVerification.addFocusValidationToSpinnerInteger(nbPlayers);
        SpinnerVerification.addNumericValidationToSpinnerInteger(maxTurns);
        SpinnerVerification.addFocusValidationToSpinnerInteger(maxTurns);
        SpinnerVerification.addNumericValidationToSpinnerDouble(speedPlayers);
        SpinnerVerification.addFocusValidationToSpinnerDouble(speedPlayers);
    }

    public void nextMenu() {
        RunSimuModel model = (RunSimuModel) this.model;
        if (checkBoxPlayHuman.isSelected()) {
            model.setEnumRunSimu(EnumRunSimu.ChoiceHuman);
        }
        else {
            model.setEnumRunSimu(EnumRunSimu.Main);
        }

        model.setRespawnTime(respawnTime.getValue());
        model.setNbPlayers(nbPlayers.getValue());
        model.setSpeedPlayers(speedPlayers.getValue());
        model.setMaxTurns(maxTurns.getValue());

        model.setSeed(Long.parseLong(seed.getCharacters().toString()));

        List<Node> listTeam = listTeamsHBox.getChildren();
        List<ModelEnum> modelList = new ArrayList<>();
        List<String> neuralModelNames = new ArrayList<>();

        for (int i = 0; i < listTeam.size(); i++) {
            VBox team = (VBox) listTeam.get(i);
            VBox models = (VBox) team.getChildren().get(1);

            ModelEnum modelTeam = null;

            boolean find = false;
            for (int j=0; j<models.getChildren().size(); j++) {
                RadioButton rb = (RadioButton) models.getChildren().get(j);
                if (rb.isSelected()) {
                    modelTeam = ModelEnum.getEnum(j);
                    if(team.getChildren().size()>2){
                        VBox modelNames = (VBox) team.getChildren().get(3);
                        for(Node node : modelNames.getChildren()) {
                            if(node instanceof RadioButton && ((RadioButton) node).isSelected())
                                neuralModelNames.add(((RadioButton) node).getText());
                        }
                    }
                    else neuralModelNames.add("");
                    find = true;
                    break;
                }
            }
            if (!find) {
                modelTeam = ModelEnum.Random;
            }
            modelList.add(modelTeam);
        }
        model.setModelList(modelList);

        model.setNeuralNetworkTeam(neuralModelNames);

        //Instancier humanTeam du modèle
        List<List<String>> humanTeam = new ArrayList<List<String>>(model.getModelList().size());
        for (int numTeam = 0; numTeam < model.getModelList().size(); numTeam++) {
            humanTeam.add(new ArrayList<>(model.getNbPlayers()));
        }
        for (int numTeam = 0; numTeam < model.getModelList().size(); numTeam++) {
            for (int numPlayer = 0; numPlayer < model.getNbPlayers(); numPlayer++) {
                humanTeam.get(numTeam).add("Bot");
            }
        }

        model.setHumanTeam(humanTeam);

        model.update();
        model.getGlobalModel().updateRacine();
    }

    public void btnSeed(){
        System.out.println("btnSeed");
        RunSimuModel model = (RunSimuModel) this.model;
        ChoiceParameters choiceParameters = (ChoiceParameters) model.getView();
        choiceParameters.updateSeed();
    }

}
