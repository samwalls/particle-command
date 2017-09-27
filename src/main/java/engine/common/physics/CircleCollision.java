package engine.common.physics;

import engine.common.ForceType;
import engine.common.GameObject;
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
        System.out.println("collision: " + penetration + " in direction | " + normal);
        resolveImpulse();
//        resolveInterpenetration();
    }

    private void resolveImpulse() {
        // adapted from "Game Engine Physics Development" - Millington, section 7.2, pages 109-110
        float totalInverseMass = contactA.physics().getInverseMass() + contactB.physics().getInverseMass();
        // if the total mass is infinite we don't need to do anything
        if (totalInverseMass <= 0)
            return;
        float separatingVelocity = relativeVelocity.dot(contactA.physics().getPosition().sub(contactB.physics().getPosition()).normalize());
        // if the objects are moving away from each other (or not moving) we don't need to do anything
        if (separatingVelocity >= 0)
            return;
        float newSeparatingVelocity = -separatingVelocity * restitution;
        float deltaSpeed = newSeparatingVelocity - separatingVelocity;
        float impulse = deltaSpeed / totalInverseMass;
        PVector impulsePerInverseMass = normal.copy().mult(impulse);
        contactA.physics().applyForce(impulsePerInverseMass.copy(), ForceType.IMPULSE);
        contactB.physics().applyForce(impulsePerInverseMass.copy().mult(-1f), ForceType.IMPULSE);
    }

    private void resolveInterpenetration() {
        // TODO
        if (penetration <= 0)
            return;
        float totalInverseMass = contactA.physics().getInverseMass() + contactB.physics().getInverseMass();
        if (totalInverseMass <= 0)
            return;
        PVector displacementPerInverseMass = normal.copy().mult(-penetration / totalInverseMass);
        contactA.physics().applyForce(displacementPerInverseMass.copy().mult(contactA.physics().getInverseMass()), ForceType.DISPLACEMENT);
        contactB.physics().applyForce(displacementPerInverseMass.copy().mult(-contactB.physics().getInverseMass()), ForceType.DISPLACEMENT);
    }
}