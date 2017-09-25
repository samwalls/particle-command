package game;

import engine.common.AppContext;
import engine.common.ForceType;
import engine.common.GameManager;
import engine.common.physics.ColliderType;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

/**
 *
 */
public class MainComponent extends PApplet {

    public static void main(String[] args) {
        PApplet.main(MainComponent.class.getName());
    }

    static final float MASS_MIN = 0.001f;
    static final float MASS_MAX = 50;

    public MainComponent() {
        AppContext.setContext(this);
    }

    private GameManager game = new GameManager();
    private ArrayList<GravityObject> gravityObjects = new ArrayList();

    private float mousePressX, mousePressY;

    private GravityObject star;

    public void settings() {
        fullScreen();
//        size(1940, 1080);
    }

    public void setup() {
        initStar();
    }

    public void mousePressed() {
        mousePressX = mouseX;
        mousePressY = mouseY;
    }

    public void mouseReleased() {
        GravityObject g = new GravityObject(random(MASS_MIN, MASS_MAX), ColliderType.CIRCLE);
        g.physics().setPosition(new PVector(mousePressX, mousePressY));
        // make the object move with respect to the change in position since mouse press
        g.physics().applyForce(new PVector(mouseX - mousePressX, mouseY - mousePressY).mult(0.1f), ForceType.VELOCITY);
        g.setInteractiveObjects(gravityObjects);
        game.add(g);
        gravityObjects.add(g);
    }

    public void draw() {
        // clear
        background(0, 0, 0);
        // update, render
        game.updateAll();
    }

    private void initStar() {
        star = new GravityObject(1000f, ColliderType.CIRCLE);
        star.physics().setKinematic(false);
        star.physics().setPosition(new PVector(width / 2, height / 2));
        game.add(star);
        gravityObjects.add(star);
    }
}
