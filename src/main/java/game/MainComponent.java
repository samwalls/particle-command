package game;

import engine.common.physics.ForceType;
import engine.common.component.GameManager;
import engine.common.physics.ColliderType;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MainComponent extends GameManager {

    public static void main(String[] args) {
        PApplet.main(MainComponent.class.getName());
    }

    static final float MASS_MIN = 0.001f;
    static final float MASS_MAX = 50;

    private List<BasicObject> basicObjects = new ArrayList<>();

    private float mousePressX, mousePressY;

    private BasicObject star;

    public void settings() {
        fullScreen();
//        size(1940, 1080);
    }

    public void setup() {
        frameRate(60);
//        initStar();
    }

    public void mousePressed() {
        mousePressX = mouseX;
        mousePressY = mouseY;
    }

    public void mouseReleased() {
        BasicObject g = new BasicObject(random(MASS_MIN, MASS_MAX), ColliderType.CIRCLE);
        g.physics().setPosition(new PVector(mousePressX, mousePressY));
        // make the object move with respect to the change in position since mouse press
        g.physics().applyForce(new PVector(mouseX - mousePressX, mouseY - mousePressY).mult(0.1f), ForceType.VELOCITY);
        g.setInteractiveObjects(basicObjects);
        basicObjects.add(g);
    }

    public void draw() {
        // clear
        background(0, 0, 0);
        // update, render
        game().updateAll();
    }

    private void initStar() {
        star = new BasicObject(1000f, ColliderType.CIRCLE);
        star.physics().setKinematic(false);
        star.physics().setPosition(new PVector(width / 2, height / 2));
        basicObjects.add(star);
    }
}
