package display.controllers;

import display.SongPlayer;
import display.model.GlobalModel;
import display.model.ModelMVC;
import display.model.TopModel;
import display.views.ViewType;

import java.io.IOException;


public class TopController extends Controller {

    public void test() throws IOException {
        TopModel model = (TopModel) this.model;
        GlobalModel globalModel = model.getGlobalModel();

        ViewType v = globalModel.getCurrentViewType();
        ModelMVC m = ViewType.getViewInstance(v, globalModel);

        ModelMVC.clearInstance(m.getClass());
        globalModel.setCurrentViewType(ViewType.MainMenu);
        SongPlayer.playRepeatSong("menu");
        globalModel.updateRacine();
    }

}
