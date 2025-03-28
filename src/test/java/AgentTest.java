import engine.agent.Agent;
import engine.agent.Action;
import engine.map.GameMap;
import engine.object.Flag;
import engine.object.GameObject;
import engine.Team;
import engine.Vector2;
import ia.model.Model;
import ia.model.Random;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AgentTest {

    @Test
    public void testConstructorValidArguments() {
        // Arrange
        Vector2 coordinate = new Vector2(0, 0);
        Team team = Team.RED;
        Model model = new Random();
        Optional<Flag> flag = Optional.empty();

        // Act
        Agent agent = new Agent(coordinate, 1.0, 10, 5, 15, team, flag, model, 1);

        // Assert
        assertNotNull(agent);
        assertEquals(team, agent.getTeam());
        assertEquals(1.0, agent.getRadius());
    }

    @Test
    public void testConstructorThrowsExceptionForNullCoordinate() {
        Team team = Team.RED;
        Model model = new Random();
        Optional<Flag> flag = Optional.empty();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Agent(null, 1.0, 10, 5, 15, team, flag, model, 1);
        });

        assertEquals("Coordinate cannot be null", exception.getMessage());
    }

    @Test
    public void testConstructorThrowsExceptionForNullTeam() {
        Vector2 coordinate = new Vector2(0, 0);
        Model model = new Random();
        Optional<Flag> flag = Optional.empty();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Agent(coordinate, 1.0, 10, 5, 15, null, flag, model, 1);
        });

        assertEquals("Team cannot be null", exception.getMessage());
    }

    @Test
    public void testConstructorThrowsExceptionForNegativeRadius() {
        Vector2 coordinate = new Vector2(0, 0);
        Team team = Team.RED;
        Model model = new Random();
        Optional<Flag> flag = Optional.empty();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Agent(coordinate, -1.0, 10, 5, 15, team, flag, model, 1);
        });

        assertEquals("Radius cannot be negative", exception.getMessage());
    }

    @Test
    public void testConstructorThrowsExceptionForNegativeSpeed() {
        Vector2 coordinate = new Vector2(0, 0);
        Team team = Team.RED;
        Model model = new Random();
        Optional<Flag> flag = Optional.empty();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Agent(coordinate, 1.0, -10, 5, 15, team, flag, model, 1);
        });

        assertEquals("Speed cannot be negative", exception.getMessage());
    }

    @Test
    public void testGetActionDelegatesToModel() {
        // Arrange
        Vector2 coordinate = new Vector2(0, 0);
        Team team = Team.BLUE;
        Model model = new Random();
        Optional<Flag> flag = Optional.empty();
        Agent agent = new Agent(coordinate, 1.0, 10, 5, 15, team, flag, model, 1);

        GameMap dummyMap = new GameMap(); // Replace with a mock or dummy object
        List<Agent> dummyAgents = new ArrayList<>();
        List<GameObject> dummyObjects = new ArrayList<>();

        // Act
        Action action = agent.getAction(null, dummyMap, dummyAgents, dummyObjects);

        // Assert
        assertNotNull(action);
        assertTrue(action.rotationRatio() >= -1.0 && action.rotationRatio() <= 1.0,
                "Rotation ratio should be in range [-1.0, 1.0]");
        assertTrue(action.speedRatio() >= -1.0 && action.speedRatio() <= 1.0,
                "Speed ratio should be in range [-1.0, 1.0]");
    }
}
