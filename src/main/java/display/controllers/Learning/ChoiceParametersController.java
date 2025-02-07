package display.controllers.Learning;

import display.controllers.Controller;
import display.model.LearningModel;
import display.views.Learning.EnumLearning;
import ia.model.ModelEnum;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ChoiceParametersController extends Controller {

    @FXML
    private Spinner<Integer> respawnTime;
    @FXML
    private Spinner<Integer> nbPlayers;
    @FXML
    private Spinner<Integer> speedPlayers;
    @FXML
    private HBox listTeams;
    @FXML
    private VBox listPerceptions;

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
        LearningModel model = (LearningModel) this.model;
        model.setEnumLearning(EnumLearning.Main);

        model.setRespawnTime(respawnTime.getValue());
        model.setNbPlayers(nbPlayers.getValue());
        model.setSpeedPlayers(speedPlayers.getValue());

        List<Node> list = listTeams.getChildren();
        List<List<ModelEnum>> modelList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            VBox team = (VBox) list.get(i);
            VBox models = (VBox) team.getChildren().get(1);
            List<ModelEnum> teamModels = new ArrayList<>();

            boolean find = false;
            for (int j=0; j<models.getChildren().size(); j++) {
                RadioButton rb = (RadioButton) models.getChildren().get(j);
                if (rb.isSelected()) {
                    teamModels.add(ModelEnum.getEnum(j));
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

        model.update();
        model.getGlobalModel().updateRacine();
    }

    public void addRaycast() {
        LearningModel model = (LearningModel) this.model;

        model.setRaycasts(model.getRaycasts()+1);

        HBox raycastHBox = new HBox();

        Label nameRaycast = new Label("Raycast " + model.getRaycasts());

        //Taille des rayons
        Label labelRayLenght = new Label("Taille des rayons");
        Spinner spinnerRayLenght = new Spinner(0.5, 5, 1);

        //Nombre de rayons
        Label labelNumberOfRays = new Label("Nombre de rayons");
        Spinner spinnerNumberOfRays = new Spinner(1, 50, 2);

        //Angle
        Label labelAngle = new Label("Angle");
        Spinner spinnerAngle = new Spinner(1, 360, 1);

        raycastHBox.getChildren().addAll(nameRaycast,
                labelRayLenght, spinnerRayLenght,
                labelNumberOfRays, spinnerNumberOfRays,
                labelAngle, spinnerAngle);

        listPerceptions.getChildren().add(raycastHBox);
    }

}
