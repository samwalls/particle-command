private class GameObject implements Drawable, Updatable {
  
  private static final int MAX_SIZE = 50;
  
  private static final int TRAIL_PERIOD = 1;
  private static final int TRAIL_MAX = 50;
  
  private Transform transform = new Transform();
  
  private PhysicsComponent physics;
  
  private ColliderType colliderType;
  
  private int trailFrameCounter = 0;
  
  private PVector colour = new PVector(random(0, 255), random(0, 255), random(0, 255));
  
  private ArrayDeque<PVector> trailPositions = new ArrayDeque();
  
  public GameObject(ColliderType colliderType) {
    super();
    physics = new PhysicsComponent(transform);
    this.colliderType = colliderType;
  }
  
  public GameObject() {
    this(ColliderType.NONE);
  }
  
  @Override
  public void update() {}
  
  @Override
  public void render() {
    pushMatrix();
    PVector p = physics().getPosition();
    float size = size();
    fill(colour.x, colour.y, colour.z);
    if (++trailFrameCounter % TRAIL_PERIOD == 0) {
      trailPositions.push(new PVector(p.x, p.y));
      trailFrameCounter = 0;
    }
    renderTrail();
    while (trailPositions.size() > TRAIL_MAX)
      trailPositions.removeLast();
    // draw the box
    stroke(0);
    strokeWeight(1);
    float radius = size / 2;
    ellipse(p.x, p.y, radius * 2, radius * 2);
    rotate(transform.rotation);
    popMatrix();
  }
  
  protected void renderRelativeLine(PVector v) {
    line(physics().getPosition().x, physics().getPosition().y, physics().getPosition().x + v.x, physics().getPosition().y + v.y);
  }
  
  private void renderTrail() {
    if (trailPositions.size() >= 2) {
      PVector a = null, b = null;
      float i = 0;
      for (PVector trailPoint : trailPositions) {
        b = trailPoint;
        if (a != null) {
          PVector strokeColour = new PVector(colour.x, colour.y, colour.z);
          strokeColour.mult(5f/i);
          stroke(strokeColour.x, strokeColour.y, strokeColour.z);
          float trailWidth = 0.8 * (size() - physics().getVelocity().mag() * (i / size()));
          strokeWeight(trailWidth >= 0 ? trailWidth : 0);
          line(a.x, a.y, b.x, b.y);
        }
        a = b;
        i++;
      }
    }
  }
  
  // public property based on the mass of the object
  public float size() {
    return max(1, min(physics().getMass(), MAX_SIZE));
  }
  
  // public access to this object's physics
  public PhysicsComponent physics() {
    return physics;
  }
  
  public ColliderType colliderType() {
    return colliderType;
  }
}