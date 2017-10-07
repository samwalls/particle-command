package engine.common.component;

import engine.common.physics.*;
import processing.core.PVector;

import static engine.common.component.GameManager.game;

public class ColliderComponent extends Component {

    private ColliderType type = ColliderType.NONE;

    private AABB boundingBox = new AABB();

    /**
     * When isTrigger is true, the object does not physically respond to contacts, collision events like
     * onCollisionEnter, onCollisionStay, and onCollisionExit are still performed however. This makes the collider act
     * as a trigger region rather than a physical collider.
     */
    private boolean isTrigger = false;

    public ColliderComponent(Component parent) throws IllegalArgumentException {
        super(parent);
        this.addChild(boundingBox);
    }

    public ColliderComponent(Component parent, ColliderType type) {
        this(parent);
        this.type = type;
    }

    public ColliderComponent() {
        this(null, ColliderType.NONE);
    }

    public float outerRadius() {
        return boundingBox.outerRadius();
    }

    public float innerRadius() {
        return boundingBox.innerRadius();
    }

    public AABB getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(AABB boundingBox) {
        this.boundingBox = boundingBox;
        this.addChild(this.boundingBox);
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
        if (a == b)
            return null;
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
                return circleContactingCircle(circle, other);
            case BOX:
                return circleContactingBox(circle, other);
            case NONE: default:
                // do nothing
                return null;
        }
    }

    private static Contact[] boxIsContacting(GameObject box, GameObject other) {
        switch (other.collider.type) {
            case BOX:
                return boxContactingBox(box, other);
            case CIRCLE:
                return circleContactingBox(other, box);
            case NONE: default:
                // do nothing
                return null;
        }
    }

    private static Contact[] circleContactingCircle(GameObject circle, GameObject other) {
        PVector displacement = circle.physics().globalPosition().sub(other.physics().globalPosition());
        float r1 = circle.collider.outerRadius();
        float r2 = other.collider.outerRadius();
        boolean coinciding = r1 + r2 >= displacement.mag();
        if (coinciding) {
            // the normal points towards the other object
            PVector contactNormal = other.physics().globalPosition();
            contactNormal.sub(circle.physics().globalPosition());
            contactNormal.normalize();
            float interpenetration = r1 + r2 - displacement.mag();
            return new Contact[] {
                    new CircleCollision(contactNormal, interpenetration, circle, other, 0.2f),
                    new CircleContact(contactNormal, interpenetration, circle, other),
            };
        }
        return null;
    }

    private static Contact[] circleContactingBox(GameObject circle, GameObject box) {
        // the following case is adapted from https://yal.cc/rectangle-circle-intersection-test/ to suit the
        // needs of the engine
        PVector pC = circle.physics().globalPosition();
        PVector pB = box.physics().globalPosition();
        float boxWidth = box.collider.boundingBox.width();
        float boxHeight = box.collider.boundingBox.height();
        PVector nearestPoint = new PVector(
                game().max(pB.x - boxWidth / 2f, game().min(pC.x, pB.x + boxWidth / 2f)),
                game().max(pB.y - boxHeight / 2f, game().min(pC.y, pB.y + boxHeight / 2f))
        );
        PVector separation = nearestPoint.copy().sub(pC);
        float distance = separation.mag();
        if (distance <= circle.collider.outerRadius())
            return new Contact[] {
                    // TODO use custom CircleBoxContact and CircleCollision for higher accuracy
                    new CircleCollision(separation.copy().normalize(), circle.collider.outerRadius() - distance, circle, box, 0.2f),
                    new CircleContact(separation.copy().normalize(), circle.collider.outerRadius() - distance, circle, box)
            };
        else
            return null;
    }

    private static Contact[] boxContactingBox(GameObject box, GameObject other) {
        // this method is adapted from http://hamaluik.com/posts/simple-aabb-collision-using-minkowski-difference/ to
        // suit the needs of the engine.
        // md: the "Minkowski difference"
        AABB md = new AABB();
        // TODO this assumes the AABBs are in the same reference frame, they may not be..
        float width = box.collider.boundingBox.width() + other.collider.boundingBox.width();
        float height = box.collider.boundingBox.height() + other.collider.boundingBox.height();
        PVector boxMin = box.collider.boundingBox.globalPosition().sub(new PVector (box.collider.boundingBox.width() / 2f, box.collider.boundingBox.height() / 2f));
        PVector otherMax = other.collider.boundingBox.globalPosition().add(new PVector (other.collider.boundingBox.width() / 2f, other.collider.boundingBox.height() / 2f));
        md.min = boxMin.copy().sub(otherMax);
        md.max.x = md.min.x + width;
        md.max.y = md.min.y + height;
        if (md.min.x <= 0 && md.max.x >= 0 && md.min.y <= 0 && md.max.y >= 0) {
            return new Contact[] {
                    // TODO implement BoxCollision and BoxContact
                    new BoxContact(new PVector(), 0, box, other),
            };
        }
        return null;
    }
}
