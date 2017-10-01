package game;

import engine.common.component.GameObject;
import engine.common.physics.ColliderType;
import engine.common.physics.Contact;
import engine.common.physics.ForceType;
import processing.core.PVector;

import static engine.common.component.GameManager.game;

public class GravityRegionObject extends GameObject {

    public GravityRegionObject() {
        super();
        collider.setIsTrigger(true);
        collider.setType(ColliderType.CIRCLE);
    }

    @Override
    public String renderLayer() {
        return "background";
    }

    @Override
    public void onRender() {
        game().pushMatrix();
        game().stroke(0, 0);
        game().fill(255f, 255f, 255f, 127f);
        PVector p = position();
        game().ellipse(p.x, p.y, size(), size());
        game().popMatrix();
    }

    @Override
    public void onCollisionEnter(Contact contact) {
        attractToCentre(contact.B());
    }

    @Override
    public void onCollisionStay(Contact contact) {
        attractToCentre(contact.B());
    }

    @Override
    public float size() {
        return 250f;
    }

    private void attractToCentre(GameObject other) {
        PVector normal = position().sub(other.position()).normalize();
        other.physics().applyForce(normal.mult(1), ForceType.ACCELERATION);
    }
}
