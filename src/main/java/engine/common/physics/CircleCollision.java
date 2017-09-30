package engine.common.physics;

import engine.common.component.GameObject;
import processing.core.PVector;

public class CircleCollision extends Collision {

    public CircleCollision(PVector normal, float penetration, GameObject a, GameObject b, float restitution) {
        super(normal, penetration, a, b, restitution);
    }

    public CircleCollision(PVector normal, float penetration, GameObject a, GameObject b) {
        super(normal, penetration, a, b);
    }

    @Override
    public void resolve() {
        // adapted from "Game Engine Physics Development" - Millington, section 7.2, pages 109-110
        float totalInverseMass = A.physics().getInverseMass() + B.physics().getInverseMass();
        // if the total mass is infinite we don't need to do anything
        if (totalInverseMass <= 0)
            return;
        float separatingVelocity = relativeVelocity.dot(A.physics().getPosition().sub(B.physics().getPosition()).normalize());
        // if the objects are moving away from each other (or not moving) we don't need to do anything
        if (separatingVelocity >= 0)
            return;
        float newSeparatingVelocity = -separatingVelocity * restitution;
        float deltaSpeed = newSeparatingVelocity - separatingVelocity;
        float impulse = deltaSpeed / totalInverseMass;
        PVector impulsePerInverseMass = normal.copy().mult(impulse);
        A.physics().applyForce(impulsePerInverseMass.copy().mult(-1f), ForceType.IMPULSE);
        B.physics().applyForce(impulsePerInverseMass.copy(), ForceType.IMPULSE);
    }
}