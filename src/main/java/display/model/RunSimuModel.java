package display.model;

import display.Display;
import display.views.RunSimu.EnumRunSimu;
import engine.Engine;
import engine.map.GameMap;
import ia.model.Model;
import ia.model.ModelEnum;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RunSimuModel extends ModelMVC{

    private EnumRunSimu anEnumRunSimu = EnumRunSimu.Mode;

    // attribut pour vue ChoiceMap
    private File[] files;
    private Optional<Integer> indiceMapSelected = Optional.empty();
    private GameMap map = GameMap.loadFile("ressources/maps/open_space.txt");

    // attribut pour vue Config
    private int respawnTime = 10;
    private int nbPlayers = 3;
    private double speedPlayers = 1;
    private int maxTurns = -1;
    private List<List<ModelEnum>> modelList = new ArrayList<>();
    private List<String> neuralNetworkTeam = new ArrayList<>();
    private long seed;
    private boolean playSelf;

    // attribut pour vue Main
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

    public void restart() throws IOException {
        this.engine.ifPresent(engine->{
            engine.stop();
        });
        display = Optional.empty();

        setMap(GameMap.loadFile(getFiles()[getIndiceMapSelected().get()]));

        this.setSaveTps(Engine.DEFAULT_TPS);
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

    public File[] getFiles() {return files;}
    public void setFiles(File[] files) {this.files = files;}

    public Optional<Integer> getIndiceMapSelected() {return indiceMapSelected;}
    public void setIndiceMapSelected(int selected) {this.indiceMapSelected = Optional.of(selected);}

    public GameMap getMap() {return map;}
    public void setMap(GameMap map) {this.map = map;}

    public int getRespawnTime() {return respawnTime;}
    public void setRespawnTime(int respawnTime) {this.respawnTime = respawnTime;}

    public int getNbPlayers() {return nbPlayers;}
    public void setNbPlayers(int nbPlayers) {this.nbPlayers = nbPlayers;}

    public double getSpeedPlayers() {return speedPlayers;}
    public void setSpeedPlayers(double speedPlayers) {this.speedPlayers = speedPlayers;}

    public int getMaxTurns(){return maxTurns;}
    public void setMaxTurns(int max_turns){this.maxTurns = max_turns;}

    public List<List<ModelEnum>> getModelList() {return modelList;}
    public void setModelList(List<List<ModelEnum>> modelList) {this.modelList = modelList;}

    public List<String> getNeuralNetworkTeam() {
        return neuralNetworkTeam;
    }
    public void setNeuralNetworkTeam(List<String> neuralNetworkTeam) {
        this.neuralNetworkTeam = neuralNetworkTeam;
    }

    public long getSeed() {return seed;}
    public void setSeed(long seed) {this.seed = seed;}

    public boolean isPlaySelf() {
        return playSelf;
    }
    public void setPlaySelf(boolean playSelf) {
        this.playSelf = playSelf;
    }
}
