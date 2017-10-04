package game;

import engine.common.component.AABB;
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
        collider.setBoundingBox(AABB.circle(10f));
        physics().setKinematic(true);
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
        PVector p = globalPosition();
        float size = collider.outerRadius() * 2;
        game().ellipse(p.x, p.y, size, size);
        game().popMatrix();
        game().pushMatrix();
        game().stroke(0, 255, 0);
        game().translate(p.x, p.y);
        game().rotate(globalRotation());
        game().line(0, 0, 0, 100);
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

    private void attractToCentre(GameObject other) {
        PVector normal = globalPosition().sub(other.globalPosition()).normalize();
        other.physics().applyForce(normal.mult(1), ForceType.ACCELERATION);
    }
}
