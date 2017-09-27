package engine.common.physics;

import engine.common.GameObject;
import processing.core.PVector;

public abstract class Contact {

    protected GameObject contactA, contactB;

    protected PVector normal = new PVector();
    protected PVector relativeVelocity = new PVector();

    protected float penetration = 0;

    public Contact(PVector normal, float penetration, GameObject contactA, GameObject contactB) {
        this.normal = normal;
        this.penetration = penetration;
        this.contactA = contactA;
        this.contactB = contactB;
        relativeVelocity = contactA.physics().getVelocity().copy().sub(contactB.physics().getVelocity());
    }

    public abstract void resolve();

    public GameObject contactA() {
        return contactA;
    }

    public GameObject contactB() {
        return contactB;
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