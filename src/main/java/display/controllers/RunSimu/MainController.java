package display.controllers.RunSimu;

import display.controllers.Controller;
import display.model.ModelMVC;
import display.model.RunSimuModel;
import display.views.RunSimu.EnumRunSimu;
import display.views.ViewType;
import engine.Engine;

import java.io.IOException;
import java.util.Random;

public class MainController extends Controller {

    public void btnPlayPauseClicked() {
        RunSimuModel model = (RunSimuModel) this.model;
        if (model.getEngine().isPresent()){
            Engine engine = model.getEngine().get();

            if (engine.isGameFinished()){
                System.out.println("Restart Game");
            }else{
                if (model.isRunning()){
                    engine.setTps(0);
                }else{
                    engine.setTps(model.getSaveTps());
                }
                model.switchIsRunning();
            }
        }
    }

    public void btnMulti2Clicked(){
        RunSimuModel model = (RunSimuModel) this.model;
        if (model.getEngine().isPresent()) {
            Engine engine = model.getEngine().get();

            if(engine.isGameFinished()) {
                System.out.println("NO X2");
            }else{
                int tps = model.getSaveTps();
                tps = tps * 2;
                model.setSaveTps(tps);

                if (model.isRunning()) {
                    engine.setTps(tps);
                }

                model.updateViews();
            }
        }
    }

    public void btnDiv2Clicked(){
        RunSimuModel model = (RunSimuModel) this.model;
        if (model.getEngine().isPresent()){
            Engine engine = model.getEngine().get();

            if(engine.isGameFinished()) {
                System.out.println("NO /2");
            }else {
                int tps = model.getSaveTps();
                if (tps > 1) {
                    tps = tps / 2;
                    model.setSaveTps(tps);
                }

                if (model.isRunning()) {
                    engine.setTps(tps);
                }

                model.updateViews();
            }
        }
    }

    public void btnRestart() throws IOException {
        RunSimuModel model = (RunSimuModel) this.model;
        model.restart();

        model.update();
        model.getGlobalModel().updateRacine();
    }

    public void btnNewSeed() throws IOException {
        RunSimuModel model = (RunSimuModel) this.model;
        model.setSeed(new Random().nextLong());

        btnRestart();
    }

    public void btnExit(){
        RunSimuModel model = (RunSimuModel) this.model;

        model.getEngine().get().stop();
        ModelMVC.clearInstance(RunSimuModel.class);

        model.setEnumRunSimu(EnumRunSimu.Mode);
        model.getGlobalModel().setCurrentViewType(ViewType.MainMenu);

        model.update();
        model.getGlobalModel().updateRacine();
    }
}
