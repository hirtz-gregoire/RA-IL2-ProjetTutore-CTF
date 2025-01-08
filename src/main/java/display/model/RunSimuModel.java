package display.model;

import display.Display;
import display.views.RunSimu.EnumRunSimu;
import engine.Engine;

import java.io.IOException;
import java.util.Optional;

public class RunSimuModel extends ModelMVC{

    private EnumRunSimu anEnumRunSimu = EnumRunSimu.Main;

    private Optional<Engine> engine;
    private Optional<Display> display;
    private boolean isRunning = true;
    private int saveTps = Engine.DEFAULT_TPS;

    protected RunSimuModel(GlobalModel globalModel) throws IOException {
        super(globalModel);
        update();
    }

    public void update() {
        try{
            this.view = EnumRunSimu.getRunSimuEnum(anEnumRunSimu, this);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public EnumRunSimu getEnumRunSimu() {return anEnumRunSimu;}
    public void setEnumRunSimu(EnumRunSimu type) {
        this.anEnumRunSimu = type;
    }


    public Optional<Engine> getEngine() {return engine;}
    public void setEngine(Engine engine) {this.engine = Optional.of(engine);}

    public Optional<Display> getDisplay() {return display;}
    public void setDisplay(Display display) {this.display = Optional.of(display);}

    public boolean isRunning() {return isRunning;}
    public void switchIsRunning() {this.isRunning = !isRunning;}

    public int getSaveTps() {return saveTps;}
    public void setSaveTps(int tps) {this.saveTps = tps;}
}
