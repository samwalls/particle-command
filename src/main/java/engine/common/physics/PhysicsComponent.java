package engine.common.physics;

import engine.common.component.Component;
import engine.common.component.Transform;
import processing.core.PVector;

/**
 * The component type which handles all physics based interactions, including forces and kinematics.
 */
public class PhysicsComponent extends Component {

    // we do not store values for "force", instead we simply keep acceleration and velocity (and the position, defined by
    // the inhereted Transform in Component) - this is much simpler to conceptualize.

    private PVector velocity = new PVector();
    private PVector acceleration = new PVector();

    private float mass = 0;
    private float inverseMass = Float.POSITIVE_INFINITY;

    private boolean isKinematic = true;

    private boolean integrated = true;

    public PhysicsComponent(Component parent) {
        super(parent);
    }

    /**
     * Use the acceleration and kinematic quantities to derive the object's next position.
     */
    public void integrate() {
        if (isKinematic) {
            // we're using "semi-implicit Euler integration" here
            // i.e. it's important to integrate the velocity before we integrate the position
            // https://gafferongames.com/post/integration_basics/
            velocity.add(acceleration);
            setPosition(position().add(velocity));
            integrated = true;
            acceleration.x = 0;
            acceleration.y = 0;
        }
    }

    /**
     * Apply a "force" vector to this object, in the manner defined by the force type (see {@link ForceType}). Note that
     * strictly speaking this is an overloading of the term "force" - however, this is a convention modelled after
     * Unity3D's method of applying various kinds of forces.
     * <br><br>
     * Internally, the force is resolved into the necessary change in position, velocity, or acceleration - these are the
     * fundamental aspects of the
     * @param force
     * @param type
     */
    public void applyForce(PVector force, ForceType type) {
        if (force == null || !isKinematic) {
            return;
        }
        if (integrated) {
            integrated = false;
        }
        // apply the "force" vector differently depending on the implied type
        PVector f = new PVector(force.x, force.y);
        switch (type) {
            case FORCE:
                // applying acceleration via force
                acceleration.add(f.div(mass));
                break;
            case ACCELERATION:
                // add acceleration
                acceleration.add(f);
                break;
            case IMPULSE:
                // instantaneous change in momentum as f, results in a change in velocity dependant on f and the mass
                velocity.add(f.div(mass));
                break;
            case VELOCITY:
                // add velocity
                velocity.add(f);
                break;
            case DISPLACEMENT:
                // instantaneously add to the position
                setPosition(position().add(f));
        }
    }

    /**
     * Apply a newtonian force on this object - i.e. the resulting acceleration is proportional to both the force and
     * the mass of the object.
     * @param v the force vector to apply
     */
    public void applyForce(PVector v) {
        applyForce(v, ForceType.FORCE);
    }

    /**
     * @return the position of this object (relative to it's parent), as defined by it's transform.
     */
    public PVector getPosition() {
        return transform.position();
    }

    /**
     * @return the velocity of this object (relative to it's parent).
     */
    public PVector getVelocity() {
        return velocity.copy();
    }

    public void setVelocity(PVector v) {
        if (v == null)
            return;
        velocity = v.copy();
    }

    public PVector getAcceleration() {
        // acceleration is read-only, create new vector
        return acceleration.copy();
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
        if (mass == 0f) {
            this.inverseMass = Float.POSITIVE_INFINITY;
        } else {
            this.inverseMass = 1 / mass;
        }
    }

    public float getInverseMass() {
        return inverseMass;
    }

    public void setInverseMass(float inverseMass) {
        this.inverseMass = inverseMass;
        if (inverseMass == 0) {
            this.mass = Float.POSITIVE_INFINITY;
        } else {
            this.mass = 1 / inverseMass;
        }
    }

    public PVector getMomentum() {
        PVector v = new PVector(velocity.x, velocity.y);
        v.mult(getMass());
        return v;
    }

    /**
     * @return true if this object reacts to forces.
     */
    public boolean isKinematic() {
        return isKinematic;
    }

    /**
     * Set this physics configuration's kinematic state
     * @param isKinematic the value to set this physics configuration's kinematic state to
     */
    public void setKinematic(boolean isKinematic) {
        this.isKinematic = isKinematic;
    }
}