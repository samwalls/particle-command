package engine.common.physics;

import engine.common.GameObject;
import processing.core.PVector;

public abstract class Contact {

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

    public abstract void resolve();

    public GameObject A() {
        return A;
    }

    public GameObject B() {
        return B;
    }

    public PVector getNormal() {
        return normal;
    }

    public PVector getRelativeVelocity() {
        return relativeVelocity;
    }

    public float getPenetration() {
        return penetration;
    }
}