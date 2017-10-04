package game;

import engine.common.component.AABB;
import engine.common.component.GameObject;
import engine.common.physics.ForceType;
import engine.common.component.GameManager;
import engine.common.physics.ColliderType;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class MainComponent extends GameManager {

    public static void main(String[] args) {
        PApplet.main(MainComponent.class.getName());
    }

    static final float MASS_MIN = 1f;
    static final float MASS_MAX = 100;

    private float mousePressX, mousePressY;

    private BasicObject star;

    private GameObject parentObject;

    public void settings() {
        fullScreen();
        size(displayWidth, displayHeight);
    }

    public void setup() {
        frameRate(60);
        parentObject = new GameObject();
        parentObject.setPosition(new PVector(width / 2f, height / 2f));
//        for (int i = 0; i < 150; i++) {
//            GameObject g = new BasicObject(100, ColliderType.CIRCLE);
//            float radius = 20f;
//            g.collider().setBoundingBox(AABB.circle(radius));
//            g.setParent(parentObject);
//            g.setPosition(new PVector(random(-width/2f + radius, width/2f - radius), random(height/2f + radius, height/2f - radius)));
//        }
        GameObject gravityRegion = new GravityRegionObject();
        gravityRegion.setParent(parentObject);
        gravityRegion.collider().setBoundingBox(AABB.box(100f, 100f));
        game().setRenderingLayers(Arrays.asList(
                "background",
                "particle",
                "foreground"
        ));
    }

    private PVector parentPreviousPosition = new PVector();

    public void mousePressed() {
        mousePressX = mouseX;
        mousePressY = mouseY;
        parentPreviousPosition = parentObject.position();
    }

    public void mouseDragged() {
        PVector mouseDelta = new PVector(mouseX - mousePressX, mouseY - mousePressY);
        mouseDelta.mult(1f);
//        parentObject.setPosition(parentPreviousPosition.copy().add(mouseDelta));
    }

    public void mouseReleased() {
        GameObject g;
        if (mouseButton == LEFT) {
            g = new BasicObject(random(MASS_MIN, MASS_MAX), ColliderType.CIRCLE);
            g.collider().setBoundingBox(AABB.circle(20f));
        } else if (mouseButton == RIGHT)
            g = new BoxObject(random(MASS_MIN, MASS_MAX));
        else {
            return;
        }
        g.setParent(parentObject);
        g.setPosition(g.toRotationalFrame(g.toReferenceFrame(new PVector(mousePressX, mousePressY))));
        // make the object move with respect to the change in position since mouse press
        g.physics().applyForce(new PVector(mouseX - mousePressX, mouseY - mousePressY).mult(0.1f), ForceType.VELOCITY);
    }

    public void keyPressed() {
        float rot = 0.01f;
        if (key == 'q') {
            parentObject.setRotation(parentObject.rotation() + rot);
        } else if (key == 'e') {
            parentObject.setRotation(parentObject.rotation() - rot);
        }
        System.out.println("global rotation: " + parentObject.globalRotation() + " | local rotation: " + parentObject.rotation());
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
    }
}
