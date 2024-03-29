package engine.common.physics;

import engine.common.component.GameObject;
import processing.core.PVector;

/**
 * All the information required for a (potentially interpenetrating) contact between two GameObjects.
 */
public class Contact {

    protected GameObject A, B;

    protected PVector normal = new PVector();
    protected PVector relativeVelocity = new PVector();

    protected float penetration = 0;

    public Contact(PVector normal, float penetration, GameObject A, GameObject B) {
        this.normal = normal;
        this.penetration = penetration;
        this.A = A;
        this.B = B;
        relativeVelocity = A.physics().getVelocity().copy().sub(B.physics().getVelocity());
    }

    public Contact copy() {
        return new Contact(normal.copy(), penetration, A, B);
    }

    /**
     * Swap A and B.
     * @return this object
     */
    public Contact swap() {
        GameObject C = A;
        A = B;
        B = C;
        return this;
    }

    /**
     * Resolve the contact, should be overriden to add behaviour
     */
    public void resolve() { }

    public GameObject A() {
        return A;
    }

    public GameObject B() {
        return B;
    }

    public PVector getNormal() {
        return normal.copy();
    }

    public PVector getRelativeVelocity() {
        return relativeVelocity.copy();
    }

    public float getSeparatingVelocity() {
        return relativeVelocity.dot(relativeVelocity.copy().normalize());
    }

    public float getPenetration() {
        return penetration;
    }
}