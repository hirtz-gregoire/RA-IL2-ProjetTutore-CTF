package display.controllers.RunSimu;

import display.controllers.Controller;
import display.model.RunSimuModel;
import engine.Engine;

public class MainCtrl extends Controller {

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

}
