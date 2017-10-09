package game.objects;

import engine.common.component.AABB;
import engine.common.component.GameObject;
import engine.common.physics.ColliderType;
import game.RenderLayer;

import java.awt.*;

import static engine.common.component.GameManager.game;

/**
 * Objects that choose to interact with the play area can do so.
 */
public class PlayArea extends GameObject {

    private static final float HEIGHT_PADDING = 50f;

    private static final Color atmosphereColour = new Color(30, 180, 255, 127);

    public PlayArea() {
        // will not move
        physics.setKinematic(false);
        // only acts as a trigger
        collider.setIsTrigger(true);
        collider.setType(ColliderType.BOX);
        collider.setBoundingBox(AABB.box(game().width, game().height + HEIGHT_PADDING));
    }

    @Override
    public String renderLayer() {
        return RenderLayer.BACKGROUND.toString();
    }

    @Override
    public void onRender() {
        game().pushMatrix();
        float width = game().width;
        float height = game().height;
        game().fill(atmosphereColour.getRGB(), atmosphereColour.getAlpha());
        game().rect(0, 0, width, height);
        game().popMatrix();
    }
}
