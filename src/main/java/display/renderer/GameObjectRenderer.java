package display.renderer;

import engine.Engine;
import engine.Team;
import engine.object.Flag;
import engine.object.GameObject;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class GameObjectRenderer {

    public static void render(GameObject object, Engine engine, Pane root, double cellSize, boolean showBoxCollisions) {
        switch (object){
            case Flag flag -> renderFlag(flag, engine, root, cellSize, showBoxCollisions);
            default -> throw new IllegalStateException("Unexpected value: " + object);
        }
    }

    private static void renderFlag(Flag object, Engine engine, Pane root, double cellSize, boolean showBoxCollisions) {
        Image spriteObject = Team.getObjectSprite(object, cellSize);
        ImageView objetView = new ImageView(spriteObject);
        objetView.setX(object.getCoordinate().x() * cellSize - cellSize / 2);
        objetView.setY(object.getCoordinate().y() * cellSize - cellSize / 2);
        root.getChildren().add(objetView);

        if (object instanceof Flag && !object.getHolded()) {
            Circle safeZone = new Circle();

            double safeZoneRadius = engine.getFlagSafeZoneRadius();
            safeZone.setRadius(safeZoneRadius * cellSize);

            safeZone.setCenterX(object.getCoordinate().x() * cellSize - safeZoneRadius / 2);
            safeZone.setCenterY(object.getCoordinate().y() * cellSize - safeZoneRadius / 2);

            safeZone.setStroke(Color.WHITE);
            safeZone.setFill(Color.TRANSPARENT);
            safeZone.setStrokeWidth(1);

            root.getChildren().add(safeZone);
            if (showBoxCollisions) {
                Circle hitbox = new Circle();

                double hitboxRadius = object.getRadius();
                hitbox.setRadius(hitboxRadius * cellSize);

                hitbox.setCenterX(object.getCoordinate().x() * cellSize - hitboxRadius / 2);
                hitbox.setCenterY(object.getCoordinate().y() * cellSize - hitboxRadius / 2);

                hitbox.setFill(Color.WHITE);
                hitbox.setOpacity(0.6);
                root.getChildren().add(hitbox);
            }
        }
    }
}
