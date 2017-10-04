package game;

import engine.common.component.AABB;
import engine.common.physics.ColliderType;
import processing.core.PVector;

import static engine.common.component.GameManager.game;

public class BoxObject extends BasicObject {

    private static final float width = 40, height = 40;

    public BoxObject(float mass, ColliderType colliderType) {
        super(mass, colliderType);
        collider.setBoundingBox(AABB.box(width, height));
    }

    public BoxObject(float mass) {
        this(mass, ColliderType.BOX);
    }

    @Override
    public void onRender() {
        PVector position = globalPosition();
        game().pushMatrix();
        game().translate(position.x, position.y);
        game().rotate(globalRotation());
        AABB bb = collider.getBoundingBox();
        game().stroke(0, 0);
        game().fill(colour.x, colour.y, colour.z);
        game().rect(bb.min.x, bb.min.y, bb.width(), bb.height());
        game().popMatrix();
    }
}
