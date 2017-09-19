public class PhysicsComponent {
  
  private PVector position = new PVector();
  private PVector velocity = new PVector();
  private PVector acceleration = new PVector();
  
  private float mass = 0;
  
  private boolean isKinematic = true;
  
  private boolean integrated = true;
  
  public void integrate() {
    if (isKinematic && !integrated) {
      position.add(velocity);
      velocity.add(acceleration);
      integrated = true;
    }
  }
  
  public void applyForce(PVector f, ForceType type) {
    if (f == null) {
      return;
    }
    if (integrated) {
      acceleration.x = 0;
      acceleration.y = 0;
      integrated = false;
    }
    switch(type) {
      case FORCE:
        // applying acceleration via force
        acceleration.add(f.div(mass));
      case ACCELERATION:
        // add acceleration
        acceleration.add(f);
      case IMPULSE:
        // instantaneous change in momentum as f, results in a change in velocity dependant on f and the mass
        velocity.add(f.div(mass));
      case VELOCITY:
        // add velocity
        velocity.add(f);      
    }
  }
  
  public void applyForce(PVector v) {
    applyForce(v, ForceType.FORCE);
  }
  
  public PVector getPosition() {
    return position;    
  }
  
  public void setPosition(PVector position) {
    if (position != null)
      this.position = position;
  }
  
  public PVector getVelocity() {
    return velocity;
  }
  
  public PVector getAcceleration() {
    // acceleration is not modifiable, create new vector
    return new PVector(acceleration.x, acceleration.y);
  }
  
  public float getMass() {
    return mass;
  }
  
  public void setMass(float mass) {
    this.mass = mass;
  }
  
  public boolean isKinematic() {
    return isKinematic;
  }
  
  public void setKinematic(boolean isKinematic) {
    this.isKinematic = isKinematic;
  }
}