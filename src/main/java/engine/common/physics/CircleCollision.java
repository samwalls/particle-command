package engine.common.physics;

import engine.common.ForceType;
import engine.common.GameObject;
import processing.core.PVector;

import static engine.common.AppContext.app;

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
        PVector pA = new PVector(contactA.physics().getPosition().x, contactA.physics().getPosition().y);
        PVector pB = new PVector(contactB.physics().getPosition().x, contactB.physics().getPosition().y);
        PVector vA = new PVector(contactA.physics().getVelocity().x, contactA.physics().getVelocity().y);
        PVector vB = new PVector(contactB.physics().getVelocity().x, contactB.physics().getVelocity().y);
        float mA = contactA.physics().getMass();
        float mB = contactB.physics().getMass();

        // apply velocity to object A

        PVector displacement = new PVector(pA.x, pA.y);
        displacement.sub(pB);

        PVector relativeVelocity = new PVector(vA.x, vA.y);
        relativeVelocity.sub(vB.x, vB.y);

        PVector velocity = new PVector(displacement.x, displacement.y);
        velocity.mult(-((1 + restitution) * mB / (mA + mB)) * ((relativeVelocity.dot(displacement)) / app().pow(displacement.mag(), 2)));

        contactA.physics().applyForce(velocity, ForceType.VELOCITY);
    }
}