import java.util.ArrayDeque;

static final float MASS_MIN = 0.001;
static final float MASS_MAX = 10;

GameManager game = new GameManager();
ArrayList<GravityObject> gravityObjects = new ArrayList();

private float mousePressX, mousePressY;

void setup() {
  fullScreen();
  //size(1940, 1080); 
  initStar();
}

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
  game.add(g);
  gravityObjects.add(g);
}

void draw() {
  background(0, 0, 0);
  for (GameObject g : game.gameObjects()) {
    g.update();
    // apply physics updates
    g.physics().integrate();
    // render changes
    g.render();
  }
}

public void initStar() {
  GravityObject star = new GravityObject(100000000f);
  star.physics().setKinematic(false);
  star.physics().setPosition(new PVector(width/2, height/2));
  game.add(star);
  gravityObjects.add(star);
}