package game;

import engine.common.component.GameObject;
import engine.common.component.GameManager;
import engine.common.physics.ForceType;
import game.objects.*;
import processing.core.PApplet;
import processing.core.PVector;

import java.awt.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MainComponent extends GameManager {

    public static final int FRAME_RATE = 60;

    public static void main(String[] args) {
        PApplet.main(MainComponent.class.getName());
    }

    private float mousePressX, mousePressY;

    private GameObject parent;
    private Universe universe;
    private PlayArea playArea;
    private Floor floor;
    private Turret turret;
    private Pointer mousePointer;

    public void settings() {
//        fullScreen();
        size(1920, 1080);
    }

    public void setup() {
        noCursor();
        frameRate(FRAME_RATE);
        game().setRenderingLayers(Arrays.stream(RenderLayer.values()).map(RenderLayer::toString).collect(Collectors.toList()));
        universe = new Universe();
        playArea = new PlayArea();
        floor = new Floor();
        floor.setPosition(new PVector(0, height / 2f));
        turret = new Turret();
        turret.setPosition(new PVector(-200f, height / 2f - Floor.FLOOR_HEIGHT));
        mousePointer = new Pointer();
        parent = new GameObject();
        parent.setPosition(new PVector(width / 2f, height / 2f));
        parent.addChild(universe);
        parent.addChild(playArea);
        parent.addChild(floor);
        parent.addChild(turret);
        parent.addChild(mousePointer);
    }

    public void mousePressed() {
        mousePressX = mouseX;
        mousePressY = mouseY;
    }

    public void mouseDragged() {
        PVector mouseDelta = new PVector(mouseX - mousePressX, mouseY - mousePressY);
        mouseDelta.mult(1f);
    }

    public void mouseReleased() {
        createProjectile(mousePressX, mousePressY, new PVector(mouseX - mousePressX, mouseY - mousePressY).mult(0.1f));
    }

    public void keyPressed() {
    }

    public void draw() {
        // clear
        background(0, 0, 0);
        // update, render
        updateAll();
    }

    private Projectile createProjectile(float x, float y, PVector v) {
        Projectile p = new Projectile(playArea, 10f, 0.01f, 0.0001f);
        parent.addChild(p);
        p.setPosition(playArea.toReferenceFrame(new PVector(x, y)));
        p.physics().applyForce(p.toRotationalFrame(v.copy()), ForceType.VELOCITY);
        return p;
    }
}
