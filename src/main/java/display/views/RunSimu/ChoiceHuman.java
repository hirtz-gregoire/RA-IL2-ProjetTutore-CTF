package display.views.RunSimu;

import display.Display;
import display.model.ModelMVC;
import display.model.RunSimuModel;
import display.views.View;
import engine.Files;
import engine.Team;
import engine.map.GameMap;
import ia.model.ModelEnum;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ChoiceHuman extends View {
    public ChoiceHuman(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("RunSimu/ChoiceHuman", this.modelMVC);

        this.update();
    }

    @Override
    public void update() {
        RunSimuModel model = (RunSimuModel) this.modelMVC;

        //Affichage nombre joueurs max
        ((BorderPane)this.pane).setTop(new Label("Veuillez choisir quel joueur joue dans quel équipe"));

        //Ajout de chaque équipe
        HBox listTeams = (HBox)((ScrollPane)this.pane.lookup("#listTeams")).getContent();
        listTeams.getChildren().clear();

        List<String> humansControls = Arrays.asList("Bot", "ZQSD", "OKLM", "Manette1", "Manette2");

        for (int numTeam=0; numTeam < model.getModelList().size(); numTeam++) {
            VBox vboxTeam = new VBox();
            vboxTeam.getChildren().add(new Label(Team.numEquipeToString(numTeam+1)));

            for (int numJoueur = 0; numJoueur < model.getNbPlayers(); numJoueur++) {
                HBox hboxJoueur = new HBox();
                hboxJoueur.getChildren().add(new Label("J" + (numJoueur+1) + ": "));

                ComboBox<String> comboBoxJoueur = new ComboBox<>();
                comboBoxJoueur.getItems().setAll(humansControls);

                int finalNumTeam = numTeam;
                int finalNumJoueur = numJoueur;
                comboBoxJoueur.valueProperty().addListener(observable -> {
                    System.out.println(model.getHumanTeam());
                    model.setHumanTeamByTeamByHuman(finalNumTeam, finalNumJoueur, comboBoxJoueur.getValue());
                });
                comboBoxJoueur.getSelectionModel().select("Bot");

                hboxJoueur.getChildren().add(comboBoxJoueur);

                vboxTeam.getChildren().add(hboxJoueur);
            }
            listTeams.getChildren().add(vboxTeam);
        }

        super.update();
    }
}
