package game.objects;

import engine.common.component.AABB;
import engine.common.component.GameObject;
import engine.common.physics.ColliderType;
import game.RenderLayer;
import processing.core.PVector;

import java.awt.*;

import static game.MainComponent.game;

public class Floor extends GameObject {

    public static final float FLOOR_HEIGHT = 250f;
    private static final float FLOOR_SIDE_PADDING = 300f;

    private Color groundColour = new Color(100, 100, 100, 255);

    public Floor() {
        // will not move
        physics.setKinematic(false);
        physics.setMass(100000f);
        collider.setType(ColliderType.BOX);
        collider.setBoundingBox(AABB.box(game().width + FLOOR_SIDE_PADDING, FLOOR_HEIGHT));
    }

    @Override
    public String renderLayer() {
        return RenderLayer.FLOOR.toString();
    }

    @Override
    public void onRender() {
        game().pushMatrix();
        PVector p = globalPosition();
        game().fill(groundColour.getRGB(), groundColour.getAlpha());
        game().rect(0, p.y - FLOOR_HEIGHT / 2f, game().width, FLOOR_HEIGHT);
        game().popMatrix();
    }
}
