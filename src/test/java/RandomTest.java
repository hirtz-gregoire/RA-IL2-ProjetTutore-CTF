import engine.agent.Action;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;
import ia.model.Random;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RandomTest {

    @Test
    public void testMultipleRandomActions() {
        // Arrange
        Random randomModel = new Random();
        GameMap dummyMap = new GameMap();
        List<Agent> dummyAgents = new ArrayList<>();
        List<GameObject> dummyObjects = new ArrayList<>();
        int numberOfTests = 100;

        // Act & Assert
        for (int i = 0; i < numberOfTests; i++) {
            Action action = randomModel.getAction(null, dummyMap, dummyAgents, dummyObjects);
            assertNotNull(action, "The action should not be null");
            assertTrue(action.getRotationRatio() >= -1.0 && action.getRotationRatio() <= 1.0,
                    "The rotation ratio should be within the range [-1.0, 1.0]");
            assertTrue(action.getSpeedRatio() >= -1.0 && action.getSpeedRatio() <= 1.0,
                    "The speed ratio should be within the range [-1.0, 1.0]");
        }
    }
}
