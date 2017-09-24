public class PhysicsComponent {
  
  private Transform transform;
  private PVector velocity = new PVector();
  private PVector acceleration = new PVector();
  
  private float mass = 0;
  private float inverseMass = Float.POSITIVE_INFINITY;
  
  private boolean isKinematic = true;
  
  private boolean integrated = true;
  
  public PhysicsComponent(Transform transform) {
    this.transform = transform;
  }
  
  public void integrate() {
    if (isKinematic) {
      // we're using "semi-implicit Euler integration" here
      // i.e. it's important to integrate the velocity before we integrate the position
      // https://gafferongames.com/post/integration_basics/
      velocity.add(acceleration);
      transform.position.add(velocity);
      integrated = true;
    }
  }
  
  public void applyForce(PVector force, ForceType type) {
    if (force == null || !isKinematic) {
      return;
    }
    if (integrated) {
      integrated = false;
      acceleration.x = 0;
      acceleration.y = 0;
    }
    PVector f = new PVector(force.x, force.y);
    switch(type) {
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
    }
  }
  
  public void applyForce(PVector v) {
    applyForce(v, ForceType.FORCE);
  }
  
  public PVector getPosition() {
    return new PVector(transform.position.x, transform.position.y);    
  }
  
  public void setPosition(PVector position) {
    if (position == null)
      return;
    transform.position.x = position.x;
    transform.position.y = position.y;
  }
  
  public PVector getVelocity() {
    return new PVector(velocity.x, velocity.y);
  }
  
  public void setVelocity(PVector v) {
    if (v == null)
      return;
    velocity.x = v.x;
    velocity.y = v.y;
  }
  
  public PVector getAcceleration() {
    // acceleration is read-only, create new vector
    return new PVector(acceleration.x, acceleration.y);
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
  
  public boolean isKinematic() {
    return isKinematic;
  }
  
  public void setKinematic(boolean isKinematic) {
    this.isKinematic = isKinematic;
  }
}