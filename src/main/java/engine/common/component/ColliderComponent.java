package engine.common.component;

import engine.common.physics.CircleCollision;
import engine.common.physics.CircleContact;
import engine.common.physics.ColliderType;
import engine.common.physics.Contact;
import processing.core.PVector;

public class ColliderComponent extends Component {

    private ColliderType type = ColliderType.NONE;

    /**
     * When isTrigger is true, the object does not physically respond to contacts, collision events like
     * onCollisionEnter, onCollisionStay, and onCollisionExit are still performed however. This makes the collider act
     * as a trigger region rather than a physical collider.
     */
    private boolean isTrigger = false;

    public ColliderComponent(Component parent) throws IllegalArgumentException {
        super(parent);
    }

    public ColliderComponent(Component parent, ColliderType type) {
        this(parent);
        this.type = type;
    }

    public ColliderComponent() {
        this(null, ColliderType.NONE);
    }

    public ColliderType type() {
        return type;
    }

    public void setType(ColliderType type) {
        this.type = type;
    }

    /**
     * @return true if this collider does not correspond to physical collision resolution, false otherwise
     */
    public boolean isTrigger() {
        return isTrigger;
    }

    /**
     * Set this collider's isTrigger state.
     * @param isTrigger the desired isTrigger state
     */
    public void setIsTrigger(boolean isTrigger) {
        this.isTrigger = isTrigger;
    }

    /**
     * @param a the object to check if it is in contact with b
     * @param b the object to check if it is in contact with a
     * @return an array of all contacts to be resolved due to a and b - null if the objects are not contacting - the array
     * may also be empty, this means that the objects did in fact contact, but there are no contacts to be resolved.
     */
    public static Contact[] areContacting(GameObject a, GameObject b) {
        switch (a.collider.type()) {
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
        switch (other.collider.type()) {
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
                            new CircleCollision(contactNormal, interpenetration, circle, other, 0.5f),
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
