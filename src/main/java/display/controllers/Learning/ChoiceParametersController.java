package display.controllers.Learning;

import display.SongPlayer;
import display.controllers.Controller;
import display.model.LearningModel;
import display.views.Learning.EnumLearning;
import ia.model.ModelEnum;
import ia.model.NeuralNetworks.TransferFonctionEnum;
import ia.perception.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ChoiceParametersController extends Controller {

    @FXML
    private TextField textFieldModelName;
    @FXML
    private Label labelModelName;
    @FXML
    private Spinner<Integer> respawnTime;
    @FXML
    private Spinner<Integer> nbPlayers;
    @FXML
    private Spinner<Integer> speedPlayers;
    @FXML
    private Spinner<Integer> maxTurns;
    @FXML
    private Spinner<Integer> numberOfGenerations;
    @FXML
    private HBox listTeamsHBox;
    @FXML
    private VBox listPerceptions;
    @FXML
    private CheckBox checkBoxNearestEnemyFlagCompass;
    @FXML
    private CheckBox checkBoxNearestAllyFlagCompass;
    @FXML
    public CheckBox checkBoxWallCompass;
    @FXML
    private CheckBox checkBoxTerritoryCompass;
    @FXML
    private CheckBox checkBoxRecurrentNetwork;
    @FXML
    private Spinner<Integer> memorySize;
    @FXML
    private HBox memorySizeContainer;
    @FXML
    private VBox listRaycasts;
    @FXML
    private VBox transferFonctionsVBox;
    @FXML
    private VBox listLayers;
    @FXML
    private Label labelNeuronsFirstLayer;
    @FXML
    private Label labelWeightsCount;
    @FXML
    private Label labelNeuronsLastLayer;

    private int numberOfNeuronsFirstLayer = 0;

    @FXML
    public void initialize() {
        respawnTime.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 10));
        nbPlayers.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 3));
        speedPlayers.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        maxTurns.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0,1000));
        numberOfGenerations.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 1000));
        memorySize.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0,1));

        respawnTime.setEditable(true);
        nbPlayers.setEditable(true);
        speedPlayers.setEditable(true);
        maxTurns.setEditable(true);
        numberOfGenerations.setEditable(true);
        memorySize.setEditable(true);

        addNumericValidationToSpinner(respawnTime);
        addFocusValidationToSpinner(respawnTime);
        addNumericValidationToSpinner(nbPlayers);
        addFocusValidationToSpinner(nbPlayers);
        addNumericValidationToSpinner(speedPlayers);
        addFocusValidationToSpinner(speedPlayers);
        addNumericValidationToSpinner(maxTurns);
        addFocusValidationToSpinner(maxTurns);
        addNumericValidationToSpinner(numberOfGenerations);
        addFocusValidationToSpinner(numberOfGenerations);
        addNumericValidationToSpinner(memorySize);
        addFocusValidationToSpinner(memorySize);

        //Ajout de neurones dans la première couche en choisissant les compas
        checkBoxNearestEnemyFlagCompass.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                CheckBox checkBox = (CheckBox) e.getSource();
                if (checkBox.isSelected()) {
                    modifyNumberOfNeuronsFirstLayer(new FlagCompass(null, null, true).getNumberOfPerceptionsValuesNormalise());
                }
                else {
                    modifyNumberOfNeuronsFirstLayer(- new FlagCompass(null, null, true).getNumberOfPerceptionsValuesNormalise());
                }
                updateNumberOfWeights();
            }
        });
        checkBoxNearestAllyFlagCompass.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                CheckBox checkBox = (CheckBox) e.getSource();
                if (checkBox.isSelected()) {
                    modifyNumberOfNeuronsFirstLayer(new FlagCompass(null, null, false).getNumberOfPerceptionsValuesNormalise());
                }
                else {
                    modifyNumberOfNeuronsFirstLayer(- new FlagCompass(null, null, false).getNumberOfPerceptionsValuesNormalise());
                }
                updateNumberOfWeights();
            }
        });
        checkBoxTerritoryCompass.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                CheckBox checkBox = (CheckBox) e.getSource();
                if (checkBox.isSelected()) {
                    modifyNumberOfNeuronsFirstLayer(new TerritoryCompass(null, null).getNumberOfPerceptionsValuesNormalise());
                }
                else {
                    modifyNumberOfNeuronsFirstLayer(- new TerritoryCompass(null, null).getNumberOfPerceptionsValuesNormalise());
                }
                updateNumberOfWeights();
            }
        });
        checkBoxWallCompass.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                CheckBox checkBox = (CheckBox) e.getSource();
                if (checkBox.isSelected()) {
                    modifyNumberOfNeuronsFirstLayer(WallCompass.numberOfPerceptionsValuesNormalise);
                }
                else {
                    modifyNumberOfNeuronsFirstLayer(- WallCompass.numberOfPerceptionsValuesNormalise);
                }
                updateNumberOfWeights();
            }
        });

        checkBoxRecurrentNetwork.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                CheckBox checkBox = (CheckBox) actionEvent.getSource();
                boolean isSelected = checkBox.isSelected();

                memorySizeContainer.setVisible(isSelected);
                memorySizeContainer.setManaged(isSelected);
                modifyNumberOfNeuronsFirstLayer((memorySize.getValue() + 2) * (isSelected ? 1 : -1));
                updateNumberOfWeights();
            }
        });

        memorySize.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer integer, Integer t1) {
                modifyNumberOfNeuronsFirstLayer(-integer - 2);
                modifyNumberOfNeuronsFirstLayer(t1 + 2);
                updateNumberOfWeights();
            }
        });

        //Choix de la fonction d'activation
        ToggleGroup toggleGroupTransfertFonction = new ToggleGroup();
        boolean first = true;
        for (TransferFonctionEnum transferFunction : TransferFonctionEnum.values()) {
            RadioButton radioButton = new RadioButton(transferFunction.toString());
            if (first){
                radioButton.setSelected(true);
                first = false;
            }
            radioButton.setToggleGroup(toggleGroupTransfertFonction);
            transferFonctionsVBox.getChildren().add(radioButton);
        }

        updateNumberOfWeights();
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

    public void addRaycast() {
        int numRaycast = listRaycasts.getChildren().size();

        HBox raycastHBox = new HBox();

        Label nameRaycast = new Label("Raycast " + numRaycast);

        //Taille des rayons
        Label labelRayLenght = new Label("Taille des rayons");
        Spinner spinnerRayLenght = new Spinner();
        spinnerRayLenght.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 1));
        spinnerRayLenght.setEditable(true);
        addNumericValidationToSpinner(spinnerRayLenght);
        addFocusValidationToSpinner(spinnerRayLenght);


        //Nombre de rayons
        Label labelNumberOfRays = new Label("Nombre de rayons");
        Spinner spinnerNumberOfRays = new Spinner();
        int numberOfRay = 2;
        spinnerNumberOfRays.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, numberOfRay));
        addNumericValidationToSpinner(spinnerNumberOfRays);
        addFocusValidationToSpinner(spinnerNumberOfRays);
        //Ajout de neurones dans la première couche
        modifyNumberOfNeuronsFirstLayer(numberOfRay * PerceptionRaycast.numberOfPerceptionsValuesNormalise);
        spinnerNumberOfRays.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            modifyNumberOfNeuronsFirstLayer(-Integer.parseInt(oldValue)*PerceptionRaycast.numberOfPerceptionsValuesNormalise);
            modifyNumberOfNeuronsFirstLayer(Integer.parseInt(newValue)*PerceptionRaycast.numberOfPerceptionsValuesNormalise);
            updateNumberOfWeights();
        });

        //Angle
        Label labelAngle = new Label("Angle");
        Spinner spinnerAngle = new Spinner();
        spinnerAngle.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 360, 1));
        addNumericValidationToSpinner(spinnerAngle);
        addFocusValidationToSpinner(spinnerAngle);

        //Suppression
        Button buttonRemove = new Button("Supprimer");
        buttonRemove.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                Button button = (Button) e.getTarget();

                //Suppression des neurones dans la première couche
                Spinner spinnerNumberOfRays = (Spinner)((HBox)button.getParent()).getChildren().get(4);
                modifyNumberOfNeuronsFirstLayer(- (int)spinnerNumberOfRays.getValue()*PerceptionRaycast.numberOfPerceptionsValuesNormalise);
                updateNumberOfWeights();

                listRaycasts.getChildren().remove(button.getParent());

                //Renommmage des autres hbox raycast
                for (Node node : listRaycasts.getChildren()) {
                    HBox raycastHBox = (HBox) node;

                    Label nameRaycast = (Label) raycastHBox.getChildren().getFirst();
                    int newIndex = listRaycasts.getChildren().indexOf(node)+1;
                    nameRaycast.setText("Raycast " + newIndex);

                    Button buttonRemove = (Button) raycastHBox.getChildren().getLast();
                    buttonRemove.setUserData(newIndex);
                }
            }
        });

        raycastHBox.getChildren().addAll(nameRaycast,
                labelRayLenght, spinnerRayLenght,
                labelNumberOfRays, spinnerNumberOfRays,
                labelAngle, spinnerAngle,
                buttonRemove);

        listRaycasts.getChildren().add(raycastHBox);
        updateNumberOfWeights();
    }

    public void addLayer() {
        HBox layerHBox = new HBox();

        Spinner spinnerLayer = new Spinner();
        spinnerLayer.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 500 , 10));
        addNumericValidationToSpinner(spinnerLayer);
        addFocusValidationToSpinner(spinnerLayer);

        spinnerLayer.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateNumberOfWeights();
        });

        Button buttonRemove = new Button("Supprimer");
        buttonRemove.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                Button button = (Button) e.getTarget();
                listLayers.getChildren().remove(button.getParent());
                updateNumberOfWeights();
            }
        });

        layerHBox.getChildren().addAll(spinnerLayer, buttonRemove);

        listLayers.getChildren().add(layerHBox);
        updateNumberOfWeights();
    }

    public void modifyNumberOfNeuronsFirstLayer(int numberOfNeurons) {
        numberOfNeuronsFirstLayer += numberOfNeurons;
        labelNeuronsFirstLayer.setText("Neurones d'entrées : " + numberOfNeuronsFirstLayer);
    }

    public void updateNumberOfWeights() {
        var layers = new int[listLayers.getChildren().size() + 2];
        layers[0] = numberOfNeuronsFirstLayer;
        for(int i = 0; i < listLayers.getChildren().size(); i++) {
            HBox layerHBox = (HBox) listLayers.getChildren().get(i);
            Spinner spinner = (Spinner) layerHBox.getChildren().getFirst();
            layers[i + 1] = (Integer) spinner.getValue();
        }
        layers[layers.length - 1] = 2 + (checkBoxRecurrentNetwork.isSelected() ? memorySize.getValue() : 0);

        var weightsCount = 0;
        for(int i = 0; i < layers.length - 1; i++) {
            weightsCount += layers[i] * layers[i+1];
        }

        labelWeightsCount.setText("Nombre de poids : " + weightsCount);
        labelNeuronsLastLayer.setText("Nombre de poids : " + layers[layers.length - 1]);
    }

    public void nextMenu(){
        LearningModel model = (LearningModel) this.model;

        //Nom du modèle
        if (textFieldModelName.getText() != null && !textFieldModelName.getText().equals("")) {
            SongPlayer.stopAllSongs();

            model.setNameModel(textFieldModelName.getText());

            //Paramètres de la partie
            model.setRespawnTime(respawnTime.getValue());
            model.setNbPlayers(nbPlayers.getValue());
            model.setSpeedPlayers(speedPlayers.getValue());
            model.setMaxTurns(maxTurns.getValue());
            model.setNumberOfGenerations(numberOfGenerations.getValue());

            //Modèles ennemis choisis
            List<Node> teams = listTeamsHBox.getChildren();
            List<List<ModelEnum>> modelsTeams = new ArrayList<>();

            List<List<String>> neuralNetworksTeams = new ArrayList<>();

            for (int numTeam = 0; numTeam < teams.size(); numTeam++) {
                VBox team = (VBox) teams.get(numTeam);
                VBox models = (VBox) team.getChildren().get(1);

                List<ModelEnum> selectedModels = new ArrayList<>();
                for (int j = 0; j < models.getChildren().size(); j++) {
                    CheckBox cb = (CheckBox) models.getChildren().get(j);
                    if (cb.isSelected()) {
                        selectedModels.add(ModelEnum.getEnum(j));
                    }
                }
                modelsTeams.add(selectedModels);

                List<String> selectedNNs = new ArrayList<>();
                if (team.getChildren().size() == 4) {
                    VBox modelsNN = (VBox) team.getChildren().get(3);
                    for (int j = 0; j < modelsNN.getChildren().size(); j++) {
                        RadioButton rb = (RadioButton) modelsNN.getChildren().get(j);
                        if (rb.isSelected()) {
                            selectedNNs.add("ressources/models/" + rb.getText());
                        }
                    }
                }
                neuralNetworksTeams.add(selectedNNs);
            }

            model.setModelsTeams(modelsTeams);
            model.setNeuralNetworksTeams(neuralNetworksTeams);


            //Récupération des perceptions
            model.setNearestEnnemyFlagCompass(((CheckBox)listPerceptions.getChildren().get(1)).isSelected());
            model.setNearestAllyFlagCompass(((CheckBox)listPerceptions.getChildren().get(2)).isSelected());
            model.setTerritoryCompass(((CheckBox)listPerceptions.getChildren().get(3)).isSelected());
            model.setWallCompass(((CheckBox)listPerceptions.getChildren().get(4)).isSelected());

            for (Node node : listRaycasts.getChildren()) {
                HBox raycastHBox = (HBox) node;
                List<Integer> raycast = new ArrayList<>();
                //ray lenghts
                raycast.add((int)((Spinner)raycastHBox.getChildren().get(2)).getValue());
                //number of rays
                raycast.add((int)((Spinner)raycastHBox.getChildren().get(4)).getValue());
                //angle
                raycast.add((int)((Spinner)raycastHBox.getChildren().get(6)).getValue());

                model.addRaycasts(raycast);
            }

            //Récupération du réseau
            model.setRecurrentNetwork(checkBoxRecurrentNetwork.isSelected());
            model.setRecurrentNetworkMemorySize(memorySize.getValue());

            //Fonction d'activation
            List<Node> transferFonctions = transferFonctionsVBox.getChildren();
            for (Node node : transferFonctions) {
                RadioButton radioButton = (RadioButton) node;
                if (radioButton.isSelected()) {
                    model.setTransferFunction(TransferFonctionEnum.getTransferFonctionByString(radioButton.getText()));
                }
            }
            //Couches
            List<Integer> layers = new ArrayList<>();
            layers.add(numberOfNeuronsFirstLayer);
            for (Node node : listLayers.getChildren()) {
                HBox layerHBox = (HBox) node;
                Spinner spinner = (Spinner) layerHBox.getChildren().getFirst();
                layers.add((Integer) spinner.getValue());
            }
            layers.add(2 + model.getRecurrentNetworkMemorySize());
            model.setLayersNeuralNetwork(layers);

            model.setEnumLearning(EnumLearning.Main);
            model.update();
            model.getGlobalModel().updateRacine();
        } else {
            labelModelName.setText("Veuillez saisir un nom de modèle");
        }
    }
}
