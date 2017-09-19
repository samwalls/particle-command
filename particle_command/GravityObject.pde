public class GravityObject extends GameObject {
  
  static final float WALL_DAMPENING = 0.9;
  static final float WALL_FRICTION_COEFFICIENT = 0.01;
  
  static final float GRAVITY_COEFFICIENT = 100;
  
  private ArrayList<GravityObject> interactiveObjects = new ArrayList(); 
  
  public GravityObject(float mass) {
    super();
    setMass(mass);
  }
  
  public void setInteractiveObjects(ArrayList<GravityObject> interactiveObjects) {
    this.interactiveObjects = interactiveObjects;
  }
  
  @Override
  public void update() {
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
      getVelocity().x,
      getVelocity().y
    );
    v.mult(10);
    renderRelativeLine(v);
  }
  
  private void renderAccelerationDebug() {
    stroke(0, 255, 0);
    PVector a = new PVector(
      getAcceleration().x,
      getAcceleration().y
    );
    a.mult(1000);
    renderRelativeLine(a);
  }
  
  private void resolveCollisions() {
    PVector p = getPosition();
    float radius = size() / 2;
    boolean isXOver = p.x <= radius || p.x >= width - radius;
    boolean isYOver = p.y <= radius || p.y >= height - radius;
    if (isXOver || isYOver) {
      PVector contactPosition = new PVector(
        p.x <= radius ? radius : (p.x >= width - radius ? width - radius : p.x),
        p.y <= radius ? radius : (p.y >= height - radius ? height - radius : p.y)
      );
      PVector reflectiveVelocity = new PVector(getPosition().x, getPosition().y);
      PVector v = getVelocity();
      setPosition(contactPosition);
      // the floor produces an inelastic collision (slight dampening in outcome velocity)
      reflectiveVelocity.sub(contactPosition);
      PVector friction = new PVector();
      if (isXOver) {
        reflectiveVelocity.x *= -WALL_DAMPENING;
        friction.y = v.y * WALL_FRICTION_COEFFICIENT;
      }
      if (isYOver) {
        reflectiveVelocity.y *= -WALL_DAMPENING;
        friction.x = v.x * WALL_FRICTION_COEFFICIENT;
      }
      friction.mult(-1);
      // apply friction
      applyForce(friction);
      reflectiveVelocity.mult(2);
      // only apply bounce if the force is above a threshold
      //if (reflectiveVelocity.mag() >= 0.5)
      applyForce(reflectiveVelocity, ForceType.VELOCITY);
    }
  }
  
  private void attractToOthers() {
    PVector p = getPosition();
    // apply basic "gravity"
    for (GameObject other : interactiveObjects) {
      if (other != this) {
        PVector gravity = new PVector(p.x, p.y);
        gravity.sub(new PVector(other.getPosition().x, other.getPosition().y));
        float distanceFromMouse = gravity.mag();
        gravity.mult(distanceFromMouse <= 10 ? 0 : -GRAVITY_COEFFICIENT/pow(distanceFromMouse, 2));
        //g.applyForce(new PVector(0, 1), ForceType.ACCELERATION);
        applyForce(gravity, ForceType.ACCELERATION);
      }
    }
  }
}