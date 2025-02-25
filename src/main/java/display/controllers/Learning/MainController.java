package display.controllers.Learning;

import display.controllers.Controller;
import display.model.LearningModel;
import display.model.ModelMVC;
import display.views.Learning.EnumLearning;
import display.views.ViewType;
import engine.Files;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.*;
import java.util.*;

public class MainController extends Controller {

    @FXML
    private Label labelSauvegarde;

    public void buttonExit(){
        LearningModel model = (LearningModel) this.model;

        ModelMVC.clearInstance(LearningModel.class);

        model.setEnumLearning(EnumLearning.ChoiceMap);
        model.getGlobalModel().setCurrentViewType(ViewType.MainMenu);

        model.update();
        model.getGlobalModel().updateRacine();
    }

    public void buttonSaveNN() {
        LearningModel model = (LearningModel) this.model;

        try {
            //Récupération des poids
            Scanner scanner = new Scanner(Files.getFileLearning());
            String line = null;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
            }
            scanner.close();
            String weights = line.replace(" ", ";");

            //Création du fichier ctf
            FileWriter writerCTF = new FileWriter("ressources/models/"+ model.getNameModel() +".ctf");
            if (model.isTerritoryCompass()) {
                writerCTF.write("ia.perception.TerritoryCompass;BLUE\n");
            }
            if (model.isNearestAllyFlagCompass()) {
                writerCTF.write("ia.perception.NearestAllyFlag;BLUE;false\n");
            }
            if (model.isNearestEnnemyFlagCompass()) {
                writerCTF.write("ia.perception.NearestEnnemyFlag;BLUE\n");
            }
            for (List<Integer> raycast : model.getRaycasts()) {
                writerCTF.write("ia.perception.PerceptionRaycast");
                for (int i = 0; i < raycast.size(); i++) {
                    writerCTF.write(";"+raycast.get(i));
                }
                writerCTF.write("\n");
            }
            writerCTF.write("\nressources/models/"+ model.getNameModel() +".mlp");
            writerCTF.close();

            //Création du fichier mlp
            FileWriter writerMLP = new FileWriter("ressources/models/"+ model.getNameModel() +".mlp");
            writerMLP.write(model.getTransferFunction().toString()+"\n");
            for (Integer nbNeuronsLayer : model.getLayersNeuralNetwork()) {
                writerMLP.write(nbNeuronsLayer+";");
            }
            writerMLP.write("\n");
            writerMLP.write(weights);
            writerMLP.close();

            labelSauvegarde.setText("Modèle sauvegardé !");

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
