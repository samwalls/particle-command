package engine.common.physics;

import engine.common.component.GameObject;
import processing.core.PVector;

import static engine.common.component.GameManager.game;

public abstract class Collision extends Contact {

    protected float restitution = 0;

    public Collision(PVector normal, float penetration, GameObject a, GameObject b, float restitution) {
        super(normal, penetration, a, b);
        // clip to [0,1]
        this.restitution = game().max(0, game().min(restitution, 1));
    }

    public Collision(PVector normal, float penetration, GameObject a, GameObject b) {
        this(normal, penetration, a, b, 1);
    }

    public static Contact[] areContacting(GameObject a, GameObject b) {
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

    private static Contact[] circleIsContacting(GameObject circle, GameObject other) {
        switch (other.colliderType()) {
            case CIRCLE:
                PVector displacement = circle.physics().getPosition().copy().sub(other.physics().getPosition());
                float r1 = circle.size() / 2f;
                float r2 = other.size() / 2f;
                boolean coinciding = r1 + r2 >= displacement.mag();
                if (coinciding) {
                    // the normal points towards the other object
                    PVector contactNormal = other.physics().getPosition().copy();
                    contactNormal.sub(circle.physics().getPosition());
                    contactNormal.normalize();
                    float interpenetration = r1 + r2 - displacement.mag();
                    return new Contact[] {
                            new CircleCollision(contactNormal, interpenetration, circle, other, 0f),
                            new CircleContact(contactNormal, interpenetration, circle, other),
                    };
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

    private static Contact[] boxIsContacting(GameObject box, GameObject other) {
        // TODO
        return null;
    }
}