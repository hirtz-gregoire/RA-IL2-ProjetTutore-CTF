package ia.ecj;

import display.model.LearningModel;
import ec.Evolve;
import ia.model.NeuralNetworks.MLP.MLP;
import ia.perception.*;
import org.ejml.All;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ECJTrainer {
    public void train(LearningModel model) {
        //Récupération des perceptions
        List<Perception> perceptions = new ArrayList<>();
        if(model.isNearestAllyFlagCompass())
            perceptions.add(new FlagCompass(null, new Filter(Filter.TeamMode.ALLY, Filter.DistanceMode.NEAREST), false));
        if (model.isNearestEnnemyFlagCompass())
            perceptions.add(new FlagCompass(null, new Filter(Filter.TeamMode.ENEMY, Filter.DistanceMode.NEAREST), false));
        if (model.isTerritoryCompass())
            perceptions.add(new TerritoryCompass(null, new Filter(Filter.TeamMode.ALLY, Filter.DistanceMode.NEAREST)));
        if(model.isWallCompass())
            perceptions.add(new WallCompass(null, new Filter(Filter.TeamMode.ANY, Filter.DistanceMode.NEAREST)));
        for (List<Integer> raycast : model.getRaycasts())
            perceptions.add(new PerceptionRaycast(null, raycast.get(0), raycast.get(1), raycast.get(2)));

        int genomeSize = MLP.getNumberOfWeight(model.getLayersNeuralNetwork());

        ECJParams params = new ECJParams(genomeSize, model.getMap().getMapPath(), model.getSpeedPlayers(),180, model.getNbPlayers(), model.getRespawnTime(), model.getLayersNeuralNetwork(), perceptions, model.getModelsTeam(), model.getNeuralNetworkTeam());

        String serializedParams;

        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(params);
            oos.close();

            serializedParams = Base64.getEncoder().encodeToString(baos.toByteArray());
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        Thread thread = new Thread(() -> {
            Evolve.main(List.of(
                            "-file","ressources/params/params.params",
                            "-p","pop.subpop.0.species.genome-size="+ genomeSize,
                            "-p","params="+ serializedParams,
                            "-p", "generations=" + model.getNumberOfGenerations())
                    .toArray(new String[0])
            );
        });
        thread.start();
    }
}
