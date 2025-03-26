package display.controllers.MapEditor;

import display.SongPlayer;
import display.controllers.Controller;
import display.model.MapEditorModel;
import display.model.MapEditorModel.CellType;
import display.model.ModelMVC;
import display.model.RunSimuModel;
import display.views.MapEditor.EnumMapEditor;
import display.views.RunSimu.EnumRunSimu;
import display.views.ViewType;
import engine.Team;
import engine.map.EditorMap;
import engine.map.GameMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapModifyController extends Controller {
    @FXML
    private TextField textFieldMapName;
    @FXML
    private Label labelErrorSaveMap;
    @FXML
    private Rectangle rectangleTeamChoice;
    @FXML
    private Label labelTeamChoice;

    @FXML
    public void setSelectedTeam(ActionEvent event) {
        MapEditorModel model = (MapEditorModel) this.model;
        model.setSelectedTeam(model.getSelectedTeam()+1);
        if (model.getSelectedTeam() > model.getMap().getNbTeam()) {
            model.setSelectedTeam(0);
        }
        rectangleTeamChoice.setFill(Team.TeamToColor(Team.numEquipeToTeam(model.getSelectedTeam())));
        labelTeamChoice.setText(Team.numEquipeToString(model.getSelectedTeam()));
    }

    @FXML
    public void setSelectedCellType(ActionEvent event) {
        MapEditorModel model = (MapEditorModel) this.model;
        //Récupération du bouton cliqué
        Button clickedButton = (Button) event.getSource();
        CellType selectedCellType = null;

        switch (clickedButton.getId()) {
            case "mur" -> selectedCellType = CellType.WALL;
            case "ground" -> selectedCellType = CellType.EMPTY;
            case "flag" -> selectedCellType = CellType.FLAG;
            case "spawn" -> selectedCellType = CellType.SPAWN;
        }
        model.setSelectedCellType(selectedCellType);
    }

    public void buttonExit(){
        MapEditorModel model = (MapEditorModel) this.model;

        ModelMVC.clearInstance(MapEditorModel.class);

        SongPlayer.playRepeatSong("menu");

        model.setActualMapEditorView(EnumMapEditor.Mode);
        model.getGlobalModel().setCurrentViewType(ViewType.MainMenu);

        model.update();
        model.getGlobalModel().updateRacine();
    }

    public void saveMap() throws IOException {
        MapEditorModel model = (MapEditorModel) this.model;

        labelErrorSaveMap.setText("");

        if (textFieldMapName.getText().isEmpty()) {
            labelErrorSaveMap.setText("Nom de la map non renseignée");
        }
        else {
            model.getMap().setName(textFieldMapName.getText());

            //Remettre ces deux lignes pour créer des cartes librement
            //GameMap.saveFile(model.getMapName(), model.getHeightMap(), model.getWidthMap(), model.getMapTeam(), model.getMapCellType());
            //return;

            int numInvalidTeam = model.getInvalidTeamByCellPresence();
            if (numInvalidTeam != 0) {
                labelErrorSaveMap.setText(labelErrorSaveMap.getText() + "Equipe "+ Team.numEquipeToString(numInvalidTeam)+" invalide (zone de spawn ou drapeau non présent).");
            }
            else {
                boolean makingNonConformMaps = true;
                //Tester la validité de la carte (toutes les cases importantes sont reliées entre elles) en commentaire car temps de calcul trop long
                if (!model.getValidityMapByPath() && makingNonConformMaps) {
                    labelErrorSaveMap.setText(labelErrorSaveMap.getText() + "Carte Invalide : Chemin inexistant entre toutes les équipes");
                } else {
                    SongPlayer.playSuperposeSong("enregistrement_carte");
                    EditorMap.saveFile(model.getMap().getName(), model.getMap().getHeight(), model.getMap().getWidth(), model.getMap().getMapTeam(), model.getMap().getMapCellType());
                    labelErrorSaveMap.setText(" ~ Partie sauvgardée avec succès ! ~ ");
                }
            }
        }
    }
}
