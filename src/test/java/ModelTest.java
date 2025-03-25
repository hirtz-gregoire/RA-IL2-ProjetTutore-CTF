import engine.agent.Action;
import engine.map.GameMap;
import engine.agent.Agent;
import engine.object.GameObject;
import ia.model.Model;
import ia.model.NeuralNetworks.MLP.MLP;
import ia.model.NeuralNetworks.MLP.Sigmoid;
import ia.model.NeuralNetworks.ModelNeuralNetwork;
import ia.model.NeuralNetworks.NNFileLoader;
import ia.model.Random;
import ia.perception.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ModelTest {

    // Méthode pour fournir toutes les implémentations de Model
    private static List<Model> getAllModelImplementations() {
        List<Model> models = new ArrayList<>();
        models.add(new Random());
        return models;
    }

    @ParameterizedTest
    @MethodSource("getAllModelImplementations")
    public void testGetActionReturnsValidAction(Model model) {
        // Arrange
        GameMap dummyMap = new GameMap(); // Replace with actual mock or dummy object if required
        List<Agent> dummyAgents = new ArrayList<>();
        List<GameObject> dummyObjects = new ArrayList<>();

        // Act
        Action action = model.getAction(null, dummyMap, dummyAgents, dummyObjects);

        // Assert
        assertNotNull(action, "The action should not be null for model: " + model.getClass().getName());
        assertTrue(action.rotationRatio() >= -1.0 && action.rotationRatio() <= 1.0,
                "The rotation ratio should be within the range [-1.0, 1.0] for model: " + model.getClass().getName());
        assertTrue(action.speedRatio() >= -1.0 && action.speedRatio() <= 1.0,
                "The speed ratio should be within the range [-1.0, 1.0] for model: " + model.getClass().getName());
    }

    @ParameterizedTest
    @MethodSource("getAllModelImplementations")
    public void testGetActionThrowsExceptionForNullInputs(Model model) {
        // Act & Assert for null map
        Exception mapException = assertThrows(IllegalArgumentException.class, () -> {
            model.getAction(null, null, new ArrayList<>(), new ArrayList<>());
        });
        assertEquals("map is null", mapException.getMessage(),
                "Exception message mismatch for model: " + model.getClass().getName());

        // Act & Assert for null agents
        Exception agentsException = assertThrows(IllegalArgumentException.class, () -> {
            model.getAction(null, new GameMap(), null, new ArrayList<>());
        });
        assertEquals("agents is null", agentsException.getMessage(),
                "Exception message mismatch for model: " + model.getClass().getName());

        // Act & Assert for null objects
        Exception objectsException = assertThrows(IllegalArgumentException.class, () -> {
            model.getAction(null, new GameMap(), new ArrayList<>(), null);
        });
        assertEquals("objects is null", objectsException.getMessage(),
                "Exception message mismatch for model: " + model.getClass().getName());
    }

    @ParameterizedTest
    @MethodSource("provideModel")
    public void saveAndLoadModelFromFile(Model model,String output) {
        System.out.println(model.toString());
        assertEquals(model.toString(),output);
        NNFileLoader.saveModel(model,"temp");
        try {
            Model newModel = NNFileLoader.loadModel("temp.ctf");
            System.out.println(newModel.toString());
            assertEquals(newModel.toString(),output);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Stream<Arguments> provideModel(){

        MLP mlp1 = new MLP(new int[]{4, 10, 2}, new Sigmoid());
        List<Perception> perceptions1 = new ArrayList<>();
        perceptions1.add(new FlagCompass(null,new Filter(Filter.TeamMode.ENEMY, Filter.DistanceMode.NEAREST),false));
        perceptions1.add(new TerritoryCompass(null,new Filter(Filter.TeamMode.ALLY, Filter.DistanceMode.NEAREST)));

        MLP mlp2 = new MLP(new int[]{23, 10, 2}, new Sigmoid());

        List<Perception> perceptions2 = new ArrayList<>();
        perceptions2.add(new FlagCompass(null,new Filter(Filter.TeamMode.ENEMY, Filter.DistanceMode.NEAREST),false));
        perceptions2.add(new TerritoryCompass(null,new Filter(Filter.TeamMode.ALLY, Filter.DistanceMode.NEAREST)));
        perceptions2.add(new PerceptionRaycast(null,2,2,80));

        return Stream.of(
                Arguments.of(new ModelNeuralNetwork(mlp1,perceptions1),"ia.perception.FlagCompass;ENEMY;NEAREST;false\n" + "ia.perception.TerritoryCompass;ALLY;NEAREST"),
                Arguments.of(new ModelNeuralNetwork(mlp2,perceptions2),"ia.perception.FlagCompass;ENEMY;NEAREST;false\n" + "ia.perception.TerritoryCompass;ALLY;NEAREST\n" + "ia.perception.PerceptionRaycast;2;2;80"),
                Arguments.of(new ModelNeuralNetwork(mlp2,perceptions2.reversed()),"ia.perception.PerceptionRaycast;2;2;80\n" + "ia.perception.TerritoryCompass;ALLY;NEAREST\n" + "ia.perception.FlagCompass;ENEMY;NEAREST;false")

        );
    }
}
