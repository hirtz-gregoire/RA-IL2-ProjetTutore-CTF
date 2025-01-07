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

    public static void render(GameObject object, Engine engine, Pane root, int tailleCase, boolean debug) {
        int tailleObject = tailleCase;
        Image spriteObject = Team.getObjectSprite((Flag) object, tailleObject);
        ImageView objetView = new ImageView(spriteObject);
        objetView.setTranslateX(object.getCoordinate().x() * tailleCase - (double) tailleObject / 2);
        objetView.setTranslateY(object.getCoordinate().y() * tailleCase - (double) tailleObject / 2);
        root.getChildren().add(objetView);

        if (object instanceof Flag && !((Flag) object).getHolded()) {
            Circle safeZone = new Circle();

            double safeZoneRadius = engine.getFlagSafeZoneRadius();
            safeZone.setRadius(safeZoneRadius * tailleCase);

            safeZone.setCenterX(object.getCoordinate().x() * tailleCase - safeZoneRadius / 2);
            safeZone.setCenterY(object.getCoordinate().y() * tailleCase - safeZoneRadius / 2);

            safeZone.setStroke(Color.WHITE);
            safeZone.setFill(Color.TRANSPARENT);
            safeZone.setStrokeWidth(1);

            root.getChildren().add(safeZone);
            if (debug) {
                Circle hitbox = new Circle();

                double hitboxRadius = object.getRadius();
                hitbox.setRadius(hitboxRadius * tailleCase);

                hitbox.setCenterX(object.getCoordinate().x() * tailleCase - hitboxRadius / 2);
                hitbox.setCenterY(object.getCoordinate().y() * tailleCase - hitboxRadius / 2);

                hitbox.setFill(Color.WHITE);
                hitbox.setOpacity(0.6);
                root.getChildren().add(hitbox);
            }
        }
    }
}
