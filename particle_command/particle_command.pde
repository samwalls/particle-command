import java.util.ArrayDeque;

void setup() {
  fullScreen();
  //size(1940, 1080);
  
  GravityObject star = new GravityObject(1000000f);
  star.physics().setKinematic(false);
  star.physics().setPosition(new PVector(width/2, height/2));
  gameObjects.add(star);
  gravityObjects.add(star);
}

static final float MASS_MIN = 0.01;
static final float MASS_MAX = 10;

ArrayList<GameObject> gameObjects = new ArrayList();
ArrayList<GravityObject> gravityObjects = new ArrayList();

private float mousePressX, mousePressY;

void mousePressed() {
  mousePressX = mouseX;
  mousePressY = mouseY;
}

void mouseReleased() {
  GravityObject g = new GravityObject(random(MASS_MIN, MASS_MAX));
  g.physics().setPosition(new PVector(mousePressX, mousePressY));
  // make the object move with respect to the change in position since mouse press
  g.physics().applyForce(new PVector(mouseX - mousePressX, mouseY - mousePressY).mult(0.01), ForceType.VELOCITY);
  g.setInteractiveObjects(gravityObjects);
  gameObjects.add(g);
  gravityObjects.add(g);
}

void draw() {
  background(0, 0, 0);
  for (GameObject g : gameObjects) {
    g.update();
    // apply physics updates
    g.physics().integrate();
    // render changes
    g.render();
  }
}