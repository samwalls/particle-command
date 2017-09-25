public class GravityObject extends GameObject {
  
  static final float WALL_ELASTICITY = 1;
  static final float WALL_FRICTION_COEFFICIENT = 0;
  
  static final float GRAVITY_COEFFICIENT = 1;
  
  private ArrayList<GravityObject> interactiveObjects = new ArrayList(); 
  
  public GravityObject(float mass, ColliderType colliderType) {
    super(colliderType);
    physics().setMass(mass);
  }
  
  public void setInteractiveObjects(ArrayList<GravityObject> interactiveObjects) {
    this.interactiveObjects = interactiveObjects;
  }
  
  @Override
  public void update() {
    super.update();
    resolveCollisions();
    attractToOthers();
  }
  
  @Override
  public void render() {
    super.render();
    renderDebug();
  }
  
  private void renderDebug() {
    renderVelocityDebug();
    renderAccelerationDebug();
  }
  
  private void renderVelocityDebug() {
    stroke(255, 0, 0);
    PVector v = new PVector(
      physics().getVelocity().x,
      physics().getVelocity().y
    );
    v.mult(10);
    renderRelativeLine(v);
  }
  
  private void renderAccelerationDebug() {
    stroke(0, 255, 0);
    PVector a = new PVector(
      physics().getAcceleration().x,
      physics().getAcceleration().y
    );
    a.mult(1000);
    renderRelativeLine(a);
  }
  
  private void resolveCollisions() {
    PVector p = physics().getPosition();
    float radius = size() / 2;
    boolean isXOver = p.x <= radius || p.x >= width - radius;
    boolean isYOver = p.y <= radius || p.y >= height - radius;
    if (isXOver || isYOver) {
      PVector contactPosition = new PVector(
        p.x <= radius ? radius : (p.x >= width - radius ? width - radius : p.x),
        p.y <= radius ? radius : (p.y >= height - radius ? height - radius : p.y)
      );
      PVector reflectiveVelocity = new PVector(physics().getPosition().x, physics().getPosition().y);
      PVector v = physics().getVelocity();
      physics().setPosition(contactPosition);
      // the floor produces an inelastic collision (slight dampening in outcome velocity)
      reflectiveVelocity.sub(contactPosition);
      PVector friction = new PVector();
      if (isXOver) {
        reflectiveVelocity.x *= -WALL_ELASTICITY;
        friction.y = v.y * WALL_FRICTION_COEFFICIENT;
      }
      if (isYOver) {
        reflectiveVelocity.y *= -WALL_ELASTICITY;
        friction.x = v.x * WALL_FRICTION_COEFFICIENT;
      }
      friction.mult(-1);
      // apply friction
      //physics().applyForce(friction);
      reflectiveVelocity.mult(2);
      // only apply bounce if the force is above a threshold
      //if (reflectiveVelocity.mag() >= 0.5)
      physics().applyForce(reflectiveVelocity, ForceType.VELOCITY);
    }
  }
  
  private void attractToOthers() {
    PVector p = physics().getPosition();
    // apply basic "gravity"
    for (GameObject other : interactiveObjects) {
      if (other != this) {
        PVector gravity = new PVector(p.x, p.y);
        gravity.sub(new PVector(other.physics().getPosition().x, other.physics().getPosition().y));
        float distanceFromMouse = gravity.mag();
        gravity.mult(distanceFromMouse <= 10 ? 0 : -GRAVITY_COEFFICIENT/pow(distanceFromMouse, 2));
        //g.applyForce(new PVector(0, 1), ForceType.ACCELERATION);
        physics().applyForce(gravity, ForceType.ACCELERATION);
      }
    }
  }
}