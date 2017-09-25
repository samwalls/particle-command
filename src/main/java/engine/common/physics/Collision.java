package engine.common.physics;

import engine.common.GameObject;
import processing.core.PVector;

import static engine.common.AppContext.app;

public abstract class Collision extends Contact {

    protected float restitution = 0;

    public Collision(PVector normal, float penetration, GameObject a, GameObject b, float restitution) {
        super(normal, penetration, a, b);
        // clip to [0,1]
        this.restitution = app().max(0, app().min(restitution, 1));
    }

    public Collision(PVector normal, float penetration, GameObject a, GameObject b) {
        this(normal, penetration, a, b, 1);
    }

    public static Contact areContacting(GameObject a, GameObject b) {
        switch (a.colliderType()) {
            case CIRCLE:
                return circleIsContacting(a, b);
            case BOX:
                return boxIsContacting(a, b);
            case NONE:
            default:
                // do nothing
                return null;
        }
    }

    private static Contact circleIsContacting(GameObject circle, GameObject other) {
        switch (other.colliderType()) {
            case CIRCLE:
                PVector displacement = new PVector(circle.physics().getPosition().x, circle.physics().getPosition().y);
                displacement.sub(other.physics().getPosition());
                // dot product is less than zero if the velocities are pointing towards each other
                boolean coinciding = 2 * displacement.mag() <= circle.size() + other.size();
                if (coinciding) {
                    float r1 = circle.size() / 2f;
                    float r2 = circle.size() / 2f;
                    PVector contactPoint = new PVector(
                            (circle.physics().getPosition().x * r2 + other.physics().getPosition().x * r1),
                            (circle.physics().getPosition().y * r2 + other.physics().getPosition().y * r1)
                    );
                    contactPoint.div(r1 + r2);
                    PVector contactNormal = new PVector(circle.physics().getPosition().x, circle.physics().getPosition().y);
                    contactNormal.sub(other.physics().getPosition());
                    contactNormal.normalize();
                    return new CircleCollision(contactNormal, r1 + r2 - displacement.mag(), circle, other);
                }
                return null;
            case BOX:
                // TODO
                return null;
            case NONE:
            default:
                // do nothing
                return null;
        }
    }

    private static Contact boxIsContacting(GameObject box, GameObject other) {
        // TODO
        return null;
    }
}