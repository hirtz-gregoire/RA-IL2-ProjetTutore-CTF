package display.model;

import display.views.Learning.EnumLearning;
import engine.map.GameMap;
import ia.model.ModelEnum;
import ia.model.NeuralNetworks.MLP.TransferFunction;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LearningModel extends ModelMVC {

    private EnumLearning anEnumLearning = EnumLearning.ChoiceMap;

    //Attributs pour vue ChoiceMap
    private File[] files;
    private Optional<Integer> indiceMapSelected = Optional.empty();
    private List<GameMap> map = new ArrayList<>();
    private GameMap previewGameMap;

    //Attributs pour vue ChoiceParameters
    private String nameModel;
    private int respawnTime = 10;
    private int nbPlayers = 3;
    private double speedPlayers = 1;
    private int maxTurns = -1;
    private int numberOfGenerations = 100;
    private List<List<ModelEnum>> modelsTeams = new ArrayList<>();
    private List<List<String>> neuralNetworksTeams = new ArrayList<>();
    private boolean nearestEnnemyFlagCompass = false;
    private boolean nearestAllyFlagCompass = false;
    private boolean territoryCompass = false;
    private boolean wallCompass = false;
    private List<List<Integer>> raycasts = new ArrayList<>();
    private TransferFunction transferFunction;
    private List<Integer> layersNeuralNetwork = new ArrayList<>();
    private boolean isRecurrentNetwork = false;
    private int recurrentNetworkMemorySize = 0;

    //Attributs pour vue Main
    private XYChart.Series<Number, Number> bestFitnessSerie;
    private XYChart.Series<Number, Number> worstFitnessSerie;
    private XYChart.Series<Number, Number> averageFitnessSerie;
    private NumberAxis XAxisFitness;
    private NumberAxis YAxisFitness;

    private XYChart.Series<Number, Number> sigmaSerie;
    private NumberAxis XAxisSigma;
    private NumberAxis YAxisSigma;

    private XYChart.Series<Number, Number> conditionNumberSerie;
    private NumberAxis XAxisConditionNumber;
    private NumberAxis YAxisConditionNumber;

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

    public File[] getFiles() {return files;}
    public void setFiles(File[] files) {this.files = files;}
    public Optional<Integer> getIndiceMapSelected() {return indiceMapSelected;}
    public void setIndiceMapSelected(int selected) {this.indiceMapSelected = Optional.of(selected);}
    public List<GameMap> getMap() {return map;}
    public void setMap(List<GameMap> map) {this.map = map;}
    public GameMap getPreviewGameMap() {return previewGameMap;}
    public void setPreviewGameMap(GameMap previewGameMap) {this.previewGameMap = previewGameMap;}
    public int getRespawnTime() {return respawnTime;}
    public void setRespawnTime(int respawnTime) {this.respawnTime = respawnTime;}
    public int getNbPlayers() {return nbPlayers;}
    public void setNbPlayers(int nbPlayers) {this.nbPlayers = nbPlayers;}
    public double getSpeedPlayers() {return speedPlayers;}
    public void setSpeedPlayers(double speedPlayers) {this.speedPlayers = speedPlayers;}
    public int getMaxTurns() {
        return maxTurns;
    }
    public void setMaxTurns(int maxTurns) {
        this.maxTurns = maxTurns;
    }
    public int getNumberOfGenerations() {
        return numberOfGenerations;
    }
    public void setNumberOfGenerations(int numberOfGenerations) {
        this.numberOfGenerations = numberOfGenerations;
    }
    public List<List<ModelEnum>> getModelsTeams() {return modelsTeams;}
    public void setModelsTeams(List<List<ModelEnum>> modelsTeams) {this.modelsTeams = modelsTeams;}
    public List<List<String>> getNeuralNetworksTeams() {return neuralNetworksTeams;}
    public void setNeuralNetworksTeams(List<List<String>> neuralNetworksTeams) {this.neuralNetworksTeams = neuralNetworksTeams;}
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
    public TransferFunction getTransferFunction() {
        return transferFunction;
    }
    public void setTransferFunction(TransferFunction transferFunction) {
        this.transferFunction = transferFunction;
    }
    public String getNameModel() {
        return nameModel;
    }
    public void setNameModel(String nameModel) {
        this.nameModel = nameModel;
    }
    public boolean isWallCompass() {
        return wallCompass;
    }
    public void setWallCompass(boolean wallCompass) {
        this.wallCompass = wallCompass;
    }
    public NumberAxis getYAxisSigma() {
        return YAxisSigma;
    }
    public void setYAxisSigma(NumberAxis YAxisSigma) {
        this.YAxisSigma = YAxisSigma;
    }
    public NumberAxis getXAxisSigma() {
        return XAxisSigma;
    }
    public void setXAxisSigma(NumberAxis XAxisSigma) {
        this.XAxisSigma = XAxisSigma;
    }
    public XYChart.Series<Number, Number> getSigmaSerie() {
        return sigmaSerie;
    }

    public void setSigmaSerie(XYChart.Series<Number, Number> sigmaSerie) {
        this.sigmaSerie = sigmaSerie;
    }
    public XYChart.Series<Number, Number> getConditionNumberSerie() {
        return conditionNumberSerie;
    }

    public void setConditionNumberSerie(XYChart.Series<Number, Number> conditionNumberSerie) {
        this.conditionNumberSerie = conditionNumberSerie;
    }

    public NumberAxis getXAxisConditionNumber() {
        return XAxisConditionNumber;
    }

    public void setXAxisConditionNumber(NumberAxis XAxisConditionNumber) {
        this.XAxisConditionNumber = XAxisConditionNumber;
    }

    public NumberAxis getYAxisConditionNumber() {
        return YAxisConditionNumber;
    }

    public void setYAxisConditionNumber(NumberAxis YAxisConditionNumber) {
        this.YAxisConditionNumber = YAxisConditionNumber;
    }

    public NumberAxis getYAxisFitness() {
        return YAxisFitness;
    }

    public void setYAxisFitness(NumberAxis YAxisFitness) {
        this.YAxisFitness = YAxisFitness;
    }

    public NumberAxis getXAxisFitness() {
        return XAxisFitness;
    }

    public void setXAxisFitness(NumberAxis XAxisFitness) {
        this.XAxisFitness = XAxisFitness;
    }

    public XYChart.Series<Number, Number> getAverageFitnessSerie() {
        return averageFitnessSerie;
    }

    public void setAverageFitnessSerie(XYChart.Series<Number, Number> averageFitnessSerie) {
        this.averageFitnessSerie = averageFitnessSerie;
    }

    public XYChart.Series<Number, Number> getWorstFitnessSerie() {
        return worstFitnessSerie;
    }

    public void setWorstFitnessSerie(XYChart.Series<Number, Number> worstFitnessSerie) {
        this.worstFitnessSerie = worstFitnessSerie;
    }

    public XYChart.Series<Number, Number> getBestFitnessSerie() {
        return bestFitnessSerie;
    }

    public void setBestFitnessSerie(XYChart.Series<Number, Number> bestFitnessSerie) {
        this.bestFitnessSerie = bestFitnessSerie;
    }

    public int getRecurrentNetworkMemorySize() {
        return recurrentNetworkMemorySize;
    }

    public boolean isRecurrentNetwork() {
        return isRecurrentNetwork;
    }

    public void setRecurrentNetwork(boolean recurrentNetwork) {
        isRecurrentNetwork = recurrentNetwork;
    }

    public void setRecurrentNetworkMemorySize(int recurrentNetworkMemorySize) {
        this.recurrentNetworkMemorySize = recurrentNetworkMemorySize;
    }
}
