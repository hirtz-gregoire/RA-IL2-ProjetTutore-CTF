package display.renderer;

import engine.Coordinate;
import ia.perception.*;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public class PerceptionRenderer {
    public static void render(Perception perception, Pane root, Coordinate coordinate) {
        switch (perception){
            case AgentCompass _, NearestAgentCompass _, NearestFlagCompass _, ObjectCompass _, TerritoryCompass _ ->{
                    Line compassLine = new Line(coordinate.x(), coordinate.y(), coordinate.x()+100, coordinate.y()+100);
                    root.getChildren().add(compassLine);
                System.out.println(compassLine);
            }
            case PerceptionRaycast _ -> System.out.println("PerceptionRaycast");
            default -> throw new IllegalStateException("Unexpected value: " + perception.getClass());
        }
    }
}
