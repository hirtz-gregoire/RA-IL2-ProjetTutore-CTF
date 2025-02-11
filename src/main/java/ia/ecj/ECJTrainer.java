package ia.ecj;

import display.model.LearningModel;
import ec.Evolve;
import engine.map.GameMap;
import ia.model.NeuralNetworks.MLP.MLP;
import ia.perception.*;

import java.util.ArrayList;
import java.util.List;

public class ECJTrainer {
    public void train(LearningModel model) {
        //Récupération des perceptions
        List<Perception> perceptions = new ArrayList<>();
        if(model.isNearestAllyFlagCompass())
            perceptions.add(new NearestAllyFlagCompass(null, null, false));
        if (model.isNearestEnnemyFlagCompass())
            perceptions.add(new NearestEnemyFlagCompass(null, null, false));
        if (model.isTerritoryCompass())
            perceptions.add(new TerritoryCompass(null, null));
        for (List<Integer> raycast : model.getRaycasts())
            perceptions.add(new PerceptionRaycast(null, raycast.get(0), raycast.get(1), raycast.get(2)));

        int genomeSize = MLP.getNumberOfWeight(model.getLayersNeuralNetwork());

        ECJParams params = new ECJParams(genomeSize, model.getMap().getMapPath(), model.getSpeedPlayers(),180, model.getNbPlayers(), model.getRespawnTime(), model.getLayersNeuralNetwork(), perceptions, model.getModelEnemy(), model.getNeuralNetworkTeam());

        Evolve.main(List.of("-p","params="+params.toString()).toArray(new String[0]));
    }
}
