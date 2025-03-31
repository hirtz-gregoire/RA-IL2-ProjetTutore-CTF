package display.controllers;

import display.SongPlayer;
import display.model.MainMenuModel;
import display.views.ViewType;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MainMenuController extends Controller {

    @FXML
    public ImageView imageSound;

    public void switchToSimu() {

        MainMenuModel model = (MainMenuModel) this.model;
        model.getGlobalModel().setCurrentViewType(ViewType.RunSimu);

        model.getGlobalModel().updateRacine();
    }

    public void switchToMapEditor() {
        MainMenuModel model = (MainMenuModel) this.model;
        model.getGlobalModel().setCurrentViewType(ViewType.MapEditor);
        model.getGlobalModel().updateRacine();
    }

    public void switchToLearning() {
        MainMenuModel model = (MainMenuModel) this.model;
        model.getGlobalModel().setCurrentViewType(ViewType.Learning);
        model.getGlobalModel().updateRacine();
    }

    public void switchSound() {
        String imagePath = SongPlayer.isSoundOn() ? "/display/views/icone/sound_off.png" : "/display/views/icone/sound_on.png";
        imageSound.setImage(new Image(getClass().getResourceAsStream(imagePath)));
        SongPlayer.switchSound();
    }
}
