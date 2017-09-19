private class GameObject extends PhysicsComponent {
  
  private static final int MAX_SIZE = 50;
  
  private static final int TRAIL_PERIOD = 1;
  private static final int TRAIL_MAX = 50;
  
  private int trailFrameCounter = 0;
  
  private PVector colour = new PVector(random(0, 255), random(0, 255), random(0, 255));
  
  private ArrayDeque<PVector> trailPositions = new ArrayDeque();
  
  public GameObject() {
    super();
  }
  
  protected void update() {}
  
  public void render() {
    PVector p = getPosition();
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
    quad(
      p.x - radius, p.y - radius,
      p.x - radius, p.y + radius,
      p.x + radius, p.y + radius,
      p.x + radius, p.y - radius
    );
  }
  
  protected void renderRelativeLine(PVector v) {
    line(getPosition().x, getPosition().y, getPosition().x + v.x, getPosition().y + v.y);
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
          float trailWidth = 0.8 * (size() - getVelocity().mag() * (i / size()));
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
    return max(1, min(getMass(), MAX_SIZE));
  }
}