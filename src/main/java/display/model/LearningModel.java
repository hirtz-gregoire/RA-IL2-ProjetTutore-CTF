package display.model;

import display.views.Learning.EnumLearning;
import engine.Engine;
import engine.map.GameMap;
import ia.model.ModelEnum;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LearningModel extends ModelMVC {

    private EnumLearning anEnumLearning = EnumLearning.ChoiceMap;

    // attribut pour vue ChoiceMap
    private File[] files;
    private Optional<Integer> indiceMapSelected = Optional.empty();
    GameMap map = GameMap.loadFile("ressources/maps/open_space.txt");

    // attribut pour vue ChoiceParameters
    private int respawnTime = 10;
    private int nbPlayers = 3;
    private double speedPlayers = 1;
    private ModelEnum modelEnemy;
    private boolean nearestEnnemyFlagCompass = false;
    private boolean nearestAllyFlagCompass = false;
    private boolean territoryCompass = false;
    private List<List<Integer>> raycasts = new ArrayList<>();
    private List<Integer> layersNeuralNetwork = new ArrayList<>();

    // attribut pour vue Main
    private Optional<Engine> engine;
    private boolean isRunning = true;
    private int saveTps = Engine.DEFAULT_TPS;

    protected LearningModel(GlobalModel globalModel) throws IOException {
        super(globalModel);
        update();
    }

    public void update() {
        try{
            this.view = EnumLearning.getLearningEnum(anEnumLearning, this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public EnumLearning getEnumLearning() {return anEnumLearning;}
    public void setEnumLearning(EnumLearning type) {
        this.anEnumLearning = type;
    }

    public Optional<Engine> getEngine() {return engine;}
    public void setEngine(Engine engine) {this.engine = Optional.of(engine);}

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

    public ModelEnum getModelEnemy() {return modelEnemy;}
    public void setModelEnemy(ModelEnum modelEnemy) {this.modelEnemy = modelEnemy;}

    public boolean isNearestEnnemyFlagCompass() {
        return nearestEnnemyFlagCompass;
    }

    public void setNearestEnnemyFlagCompass(boolean nearestEnnemyFlagCompass) {
        this.nearestEnnemyFlagCompass = nearestEnnemyFlagCompass;
    }

    public boolean isNearestAllyFlagCompass() {
        return nearestAllyFlagCompass;
    }

    public void setNearestAllyFlagCompass(boolean nearestAllyFlagCompass) {
        this.nearestAllyFlagCompass = nearestAllyFlagCompass;
    }

    public boolean isTerritoryCompass() {
        return territoryCompass;
    }

    public void setTerritoryCompass(boolean territoryCompass) {
        this.territoryCompass = territoryCompass;
    }
    public List<List<Integer>> getRaycasts() {
        return raycasts;
    }
    public void setRaycasts(List<List<Integer>> raycasts) {
        this.raycasts = raycasts;
    }
    public void addRaycasts(List<Integer> raycast) {
        this.raycasts.add(raycast);
    }
    public void removeRaycasts(int index) {
        this.raycasts.remove(index);
    }
    public List<Integer> getLayersNeuralNetwork() {
        return layersNeuralNetwork;
    }
    public void setLayersNeuralNetwork(List<Integer> layersNeuralNetwork) {
        this.layersNeuralNetwork = layersNeuralNetwork;
    }
}
