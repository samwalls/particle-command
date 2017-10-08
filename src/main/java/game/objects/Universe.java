package game.objects;

import engine.common.component.AABB;
import engine.common.component.GameObject;
import engine.common.physics.ColliderType;

import static game.MainComponent.game;

public class Universe extends GameObject {

    private static final float WIDTH_PADDING = 1000f;
    private static final float HEIGHT_PADDING = 800f;

    public Universe() {
        // the universe acts only as a trigger
        collider.setIsTrigger(true);
        collider.setType(ColliderType.BOX);
        collider.setBoundingBox(AABB.box(game().width + WIDTH_PADDING, game().height + HEIGHT_PADDING));
    }

    @Override
    public void onCollisionExit(GameObject other) {
        // objects that fall out of the universe get destroyed
        other.destroy();
    }
}
