package display.controllers.Learning;

import display.controllers.Controller;
import display.model.LearningModel;
import display.views.Learning.EnumLearning;
import ia.model.ModelEnum;
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
    private VBox listRaycasts;
    @FXML
    private VBox transferFonctionsVBox;
    @FXML
    private VBox listLayers;
    @FXML
    private Label labelNeuronsFirstLayer;

    private int numberOfNeuronsFirstLayer = 0;

    @FXML
    public void initialize() {
        respawnTime.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 10));
        nbPlayers.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 3));
        speedPlayers.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        numberOfGenerations.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 1000));
        respawnTime.setEditable(true);
        nbPlayers.setEditable(true);
        speedPlayers.setEditable(true);
        numberOfGenerations.setEditable(true);
        addNumericValidationToSpinner(respawnTime);
        addFocusValidationToSpinner(respawnTime);
        addNumericValidationToSpinner(nbPlayers);
        addFocusValidationToSpinner(nbPlayers);
        addNumericValidationToSpinner(speedPlayers);
        addFocusValidationToSpinner(speedPlayers);
        addNumericValidationToSpinner(numberOfGenerations);
        addFocusValidationToSpinner(numberOfGenerations);

        //Ajout de neurones dans la première couche en choisissant les compas
        checkBoxNearestEnemyFlagCompass.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                CheckBox checkBox = (CheckBox) e.getSource();
                if (checkBox.isSelected()) {
                    modifyNumberOfNeuronsFirstLayer(new FlagCompass(null, null, false).getNumberOfPerceptionsValuesNormalise());
                }
                else {
                    modifyNumberOfNeuronsFirstLayer(- new FlagCompass(null, null, false).getNumberOfPerceptionsValuesNormalise());
                }
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
    }

    public void addLayer() {
        HBox layerHBox = new HBox();

        Spinner spinnerLayer = new Spinner();
        spinnerLayer.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 500 , 10));
        addNumericValidationToSpinner(spinnerLayer);
        addFocusValidationToSpinner(spinnerLayer);

        Button buttonRemove = new Button("Supprimer");
        buttonRemove.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                Button button = (Button) e.getTarget();
                listLayers.getChildren().remove(button.getParent());
            }
        });

        layerHBox.getChildren().addAll(spinnerLayer, buttonRemove);

        listLayers.getChildren().add(layerHBox);
    }

    public void modifyNumberOfNeuronsFirstLayer(int numberOfNeurons) {
        numberOfNeuronsFirstLayer += numberOfNeurons;
        labelNeuronsFirstLayer.setText("Neurones d'entrées : " + numberOfNeuronsFirstLayer);
    }

    public void nextMenu(){
        LearningModel model = (LearningModel) this.model;
        model.setEnumLearning(EnumLearning.Main);

        //Nom du modèle
        if (textFieldModelName.getText() != null && !textFieldModelName.getText().equals("")) {
            model.setNameModel(textFieldModelName.getText());

            //Paramètres de la partie
            model.setRespawnTime(respawnTime.getValue());
            model.setNbPlayers(nbPlayers.getValue());
            model.setSpeedPlayers(speedPlayers.getValue());
            model.setNumberOfGenerations(numberOfGenerations.getValue());

            //Modèles ennemis choisis
            List<Node> teams = listTeamsHBox.getChildren();
            List<ModelEnum> modelByTeam = new ArrayList<>();
            List<String> neuralNetworksByTeam = new ArrayList<>();

            for (int numTeam = 0; numTeam < teams.size(); numTeam++) {
                VBox team = (VBox) teams.get(numTeam);
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
            model.setModelsTeam(modelByTeam);
            model.setNeuralNetworkTeam(neuralNetworksByTeam);

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
            layers.add(2);
            model.setLayersNeuralNetwork(layers);

            model.update();
            model.getGlobalModel().updateRacine();
        } else {
            labelModelName.setText("Veuillez saisir un nom de modèle");
        }
    }
}
