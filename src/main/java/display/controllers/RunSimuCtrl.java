package display.controllers;

import display.Display;
import display.model.RunSimuModel;
import engine.Engine;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

public class RunSimuCtrl extends Controller {

    public void btnPlayPauseClicked() {
        RunSimuModel model = (RunSimuModel) this.model;
        if (model.getEngine().isPresent()){
            Engine engine = model.getEngine().get();

            if (model.isRunning()){
                engine.setTps(0);
            }else{
                engine.setTps(model.getSaveTps());
            }
            model.switchIsRunning();
        }
    }

    public void btnMulti2Clicked(){
        RunSimuModel model = (RunSimuModel) this.model;
        if (model.getEngine().isPresent()){
            Engine engine = model.getEngine().get();

            int tps = model.getSaveTps();
            tps = tps * 2;
            model.setSaveTps(tps);

            if (model.isRunning()){
                engine.setTps(tps);
            }

            model.updateViews();
        }
    }

    public void btnDiv2Clicked(){
        RunSimuModel model = (RunSimuModel) this.model;
        if (model.getEngine().isPresent()){
            Engine engine = model.getEngine().get();

            int tps = model.getSaveTps();
            if (tps > 1){
                tps = tps / 2;
                model.setSaveTps(tps);
            }

            if (model.isRunning()){
                engine.setTps(tps);
            }

            model.updateViews();
        }
    }

    public void checkBoxColl(){
        RunSimuModel model = (RunSimuModel) this.model;
        if (model.getDisplay().isPresent()){
            Display display = model.getDisplay().get();
            display.switchShowBoxCollisions();
        }
    }
}
