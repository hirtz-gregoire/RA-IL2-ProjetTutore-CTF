package display.views;

import display.SongPlayer;
import display.model.ModelMVC;
import java.io.IOException;

public class MainMenu extends View {

    public MainMenu(ModelMVC modelMVC) throws IOException {
        super(modelMVC);
        this.pane = loadFxml("MainMenu", this.modelMVC);
        SongPlayer.playRepeatSong("menu");
    }

    @Override
    public void update() {
        super.update();
    }
}
