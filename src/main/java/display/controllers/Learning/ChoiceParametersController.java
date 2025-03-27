package display.controllers.Learning;

import com.sun.javafx.collections.ObservableListWrapper;
import display.SongPlayer;
import display.controllers.Controller;
import display.model.LearningModel;
import display.views.Learning.EnumLearning;
import engine.Files;
import ia.model.ModelEnum;
import ia.model.NeuralNetworks.MLP.MLP;
import ia.model.NeuralNetworks.ModelNeuralNetwork;
import ia.model.NeuralNetworks.NNFileLoader;
import ia.model.NeuralNetworks.TransferFonctionEnum;
import ia.perception.*;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChoiceParametersController extends Controller {

    @FXML
    public CheckBox overwriteModel;
    @FXML
    public ComboBox<String> baseModel;
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

    private final String NO_MODEL = "Aucun modèle";

    private int nbRaycasts = 0;

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

        List<String> models = new ArrayList<>();
        models.add(NO_MODEL);
        for (File file : Files.getListSavesFilesModels()) {
            models.add(file.getName());
        }
        baseModel.setItems(new ObservableListWrapper<>(models));
        baseModel.setValue(baseModel.getItems().get(0));

        baseModel.valueProperty().addListener(_ -> {
            String modelName = baseModel.getValue();
            LearningModel learningModel = (LearningModel) this.model;
            learningModel.setMlpFile(modelName);

            checkBoxNearestAllyFlagCompass.setSelected(false);
            checkBoxNearestEnemyFlagCompass.setSelected(false);
            checkBoxTerritoryCompass.setSelected(false);
            checkBoxWallCompass.setSelected(false);
            checkBoxRecurrentNetwork.setSelected(false);
            memorySize.decrement(memorySize.getValue());
            listRaycasts.getChildren().clear();

            memorySizeContainer.setVisible(false);
            memorySizeContainer.setManaged(false);

            nbRaycasts = 0;

            listLayers.getChildren().clear();

            updateNumberOfWeights();

            if(modelName.equals(NO_MODEL)) {
                return;
            }

            try {
                ModelNeuralNetwork selectedNetwork = NNFileLoader.loadModel(modelName);

                // Updating perceptions
                for(Perception perception : selectedNetwork.getPerceptions()) {
                    System.out.println(perception);
                    switch (perception){
                        case FlagCompass c -> {
                            if (c.getTeamMode().equals(Filter.TeamMode.ALLY))
                                checkBoxNearestAllyFlagCompass.setSelected(true);
                            else checkBoxNearestEnemyFlagCompass.setSelected(true);
                        }
                        case TerritoryCompass c -> checkBoxTerritoryCompass.setSelected(true);
                        case WallCompass c -> checkBoxWallCompass.setSelected(true);
                        case PerceptionRaycast r -> addRaycast(r.getViewAngle(),r.getRayCount(),r.getRaySize()[0]);
                        default -> throw new IllegalStateException("Unexpected value: " + perception);
                    }
                }
                int memory = selectedNetwork.getNumberOfInputsMLP() - numberOfNeuronsFirstLayer;
                if(memory > 0) {
                    checkBoxRecurrentNetwork.setSelected(true);
                    memorySize.increment(memory-2);
                    memorySizeContainer.setVisible(true);
                    memorySizeContainer.setManaged(true);
                }

                MLP mlp = (MLP) selectedNetwork.getNeuralNetwork();
                int[] layerSize = mlp.getLayerSize();
                for (int i = 1; i < layerSize.length - 1; i++) {
                    addLayer(layerSize[i]);
                }
                
                updateNumberOfWeights();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        //Ajout de neurones dans la première couche en choisissant les compas
        EventHandler<ActionEvent> onCheckEvent = e -> updateNumberOfWeights();
        checkBoxNearestEnemyFlagCompass.setOnAction(onCheckEvent);
        checkBoxNearestAllyFlagCompass.setOnAction(onCheckEvent);
        checkBoxTerritoryCompass.setOnAction(onCheckEvent);
        checkBoxWallCompass.setOnAction(onCheckEvent);

        checkBoxRecurrentNetwork.setOnAction(actionEvent -> {
            CheckBox checkBox = (CheckBox) actionEvent.getSource();
            boolean isSelected = checkBox.isSelected();

            memorySizeContainer.setVisible(isSelected);
            memorySizeContainer.setManaged(isSelected);
            updateNumberOfWeights();
        });

        memorySize.valueProperty().addListener((_, _, _) -> updateNumberOfWeights());

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

    private void addNumericValidationToSpinner(Spinner spinner) {
        spinner.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            boolean newValueValid = !newValue.matches("\\d*");

            if (newValueValid) {
                spinner.getEditor().setText(oldValue);
            } else {
                try {
                    int value = Integer.parseInt(newValue);
                    spinner.getValueFactory().setValue(value);
                } catch (NumberFormatException _) {
                }
            }
        });
    }

    private void addFocusValidationToSpinner(Spinner<Integer> spinner) {
        // Écouteur sur la propriété de focus de l'éditeur
        spinner.getEditor().focusedProperty().addListener((_, _, newFocused) -> {
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

        addRaycast(70.0, 2, 1.0);
    }

    private void addRaycast(Double angleView, Integer rayCount, Double raySize) {
        int idRaycast = listRaycasts.getChildren().size();

        HBox raycastHBox = new HBox();

        Label nameRaycast = new Label("Raycast " + idRaycast);

        //Taille des rayons
        Label labelRayLenght = new Label("Taille des rayons");
        Spinner<Integer> spinnerRayLenght = new Spinner<Integer>();
        spinnerRayLenght.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, raySize.intValue()));

        spinnerRayLenght.setEditable(true);
        addNumericValidationToSpinner(spinnerRayLenght);
        addFocusValidationToSpinner(spinnerRayLenght);


        //Nombre de rayons
        Label labelNumberOfRays = new Label("Nombre de rayons");
        Spinner<Integer> spinnerNumberOfRays = new Spinner<>();
        spinnerNumberOfRays.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, rayCount));
        addNumericValidationToSpinner(spinnerNumberOfRays);
        addFocusValidationToSpinner(spinnerNumberOfRays);
        //Ajout de neurones dans la première couche
        spinnerNumberOfRays.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {

            nbRaycasts += Integer.parseInt(newValue) - Integer.parseInt(oldValue);
            updateNumberOfWeights();
        });

        //Angle
        Label labelAngle = new Label("Angle");
        Spinner<Integer> spinnerAngle = new Spinner<>();
        spinnerAngle.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 360, angleView.intValue()));
        addNumericValidationToSpinner(spinnerAngle);
        addFocusValidationToSpinner(spinnerAngle);
        spinnerAngle.setEditable(true);

        //Suppression
        Button buttonRemove = new Button("Supprimer");
        buttonRemove.setOnAction(e -> {
            Button button = (Button) e.getTarget();

            //Suppression des neurones dans la première couche
            Spinner<Integer> spinnerNumberOfRays1 = (Spinner<Integer>)((HBox)button.getParent()).getChildren().get(4);

            listRaycasts.getChildren().remove(button.getParent());

            nbRaycasts -= spinnerNumberOfRays1.getValue();

            updateNumberOfWeights();
            //Renommmage des autres hbox raycast
            for (Node node : listRaycasts.getChildren()) {
                HBox raycastHBox1 = (HBox) node;

                Label nameRaycast1 = (Label) raycastHBox1.getChildren().getFirst();
                int newIndex = listRaycasts.getChildren().indexOf(node)+1;
                nameRaycast1.setText("Raycast " + newIndex);

                Button buttonRemove1 = (Button) raycastHBox1.getChildren().getLast();
                buttonRemove1.setUserData(newIndex);
            }
        });

        raycastHBox.getChildren().addAll(nameRaycast,
                labelRayLenght, spinnerRayLenght,
                labelNumberOfRays, spinnerNumberOfRays,
                labelAngle, spinnerAngle,
                buttonRemove);

        listRaycasts.getChildren().add(raycastHBox);
        nbRaycasts += rayCount;
        updateNumberOfWeights();
    }

    public void addLayer() {
        addLayer(10);
    }

    private void addLayer(int layerSize) {
        HBox layerHBox = new HBox();

        Spinner<Integer> spinnerLayer = new Spinner<>();
        spinnerLayer.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 500 , layerSize));
        addNumericValidationToSpinner(spinnerLayer);
        addFocusValidationToSpinner(spinnerLayer);

        spinnerLayer.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateNumberOfWeights();
        });

        Button buttonRemove = new Button("Supprimer");
        buttonRemove.setOnAction(e -> {
            Button button = (Button) e.getTarget();
            listLayers.getChildren().remove(button.getParent());
            updateNumberOfWeights();
        });

        layerHBox.getChildren().addAll(spinnerLayer, buttonRemove);

        listLayers.getChildren().add(layerHBox);
        updateNumberOfWeights();
    }

    private void computeNumberOfNeuronsFirstLayer() {
        numberOfNeuronsFirstLayer = 0;
        if(checkBoxNearestAllyFlagCompass.isSelected())
            numberOfNeuronsFirstLayer += FlagCompass.numberOfPerceptionsValuesNormalise;
        if(checkBoxNearestEnemyFlagCompass.isSelected())
            numberOfNeuronsFirstLayer += FlagCompass.numberOfPerceptionsValuesNormalise;
        if(checkBoxWallCompass.isSelected())
            numberOfNeuronsFirstLayer += WallCompass.numberOfPerceptionsValuesNormalise;
        if(checkBoxTerritoryCompass.isSelected())
            numberOfNeuronsFirstLayer += TerritoryCompass.numberOfPerceptionsValuesNormalise;

        numberOfNeuronsFirstLayer += nbRaycasts * PerceptionRaycast.numberOfPerceptionsValuesNormalise;

        if(checkBoxRecurrentNetwork.isSelected()){
            numberOfNeuronsFirstLayer += memorySize.getValue()+2;

            LearningModel learningModel = (LearningModel) this.model;
            learningModel.setRecurrentNetwork(true);
            learningModel.setRecurrentNetworkMemorySize(memorySize.getValue());
        }
    }

    public void updateNumberOfWeights() {
        var layers = new int[listLayers.getChildren().size() + 2];
        computeNumberOfNeuronsFirstLayer();
        layers[0] = numberOfNeuronsFirstLayer;
        for(int i = 0; i < listLayers.getChildren().size(); i++) {
            HBox layerHBox = (HBox) listLayers.getChildren().get(i);
            Spinner<?> spinner = (Spinner<?>) layerHBox.getChildren().getFirst();
            layers[i + 1] = (Integer) spinner.getValue();
        }
        layers[layers.length - 1] = 2 + (checkBoxRecurrentNetwork.isSelected() ? memorySize.getValue() : 0);

        var weightsCount = 0;
        for(int i = 0; i < layers.length - 1; i++) {
            weightsCount += layers[i] * layers[i+1];
        }

        labelNeuronsFirstLayer.setText("Neurones d'entrées : " + numberOfNeuronsFirstLayer);
        labelWeightsCount.setText("Nombre de poids : " + weightsCount);
        labelNeuronsLastLayer.setText("Nombre de poids : " + layers[layers.length - 1]);
    }

    public void nextMenu(){
        LearningModel model = (LearningModel) this.model;

        //Nom du modèle
        if (textFieldModelName.getText() != null && !textFieldModelName.getText().isEmpty()) {
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
                List<Double> raycast = new ArrayList<>();
                //ray lenghts
                raycast.add((double)((Spinner<?>)raycastHBox.getChildren().get(2)).getValue());
                //number of rays
                raycast.add((double)((Spinner<?>)raycastHBox.getChildren().get(4)).getValue());
                //angle
                raycast.add((double)((Spinner<?>)raycastHBox.getChildren().get(6)).getValue());

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
                Spinner<Integer> spinner = (Spinner<Integer>) layerHBox.getChildren().getFirst();
                layers.add(spinner.getValue());
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
