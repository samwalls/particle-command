package game;

import engine.common.component.GameObject;
import engine.common.component.GameManager;
import engine.common.physics.ForceType;
import game.objects.*;
import processing.core.PApplet;
import processing.core.PVector;

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
    private Battery battery;
    private Pointer mousePointer;
    private EnemyTurret enemyTurret;

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
        mousePointer = new Pointer();
        parent = new GameObject();
        parent.setPosition(new PVector(width / 2f, height / 2f));
        parent.addChild(universe);
        parent.addChild(playArea);
        parent.addChild(floor);
        parent.addChild(mousePointer);
        setupBattery(4);
        enemyTurret = new EnemyTurret(playArea, battery, 0.1f);
        enemyTurret.setPosition(new PVector(0, -height / 2f));
        parent.addChild(enemyTurret);
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
        PlayerTurret closest = battery.closestTurret();
        if (closest != null)
            closest.fire(battery, Projectile.DEFAULT_RADIUS + Turret.TURRET_HEIGHT / 2f + 5f, 30f);
    }

    public void keyPressed() {
    }

    public void draw() {
        // clear
        background(0, 0, 0);
        // update, render
        updateAll();
    }

    private void setupBattery(int nTurrets) {
        battery = new Battery(null);
        parent.addChild(battery);
        for (int i = 0; i < nTurrets; i++) {
            PlayerTurret turret = new PlayerTurret(playArea, battery);
            // distribute the turrets evenly among the ground
            float x = -game().width / 2f + (i + 0.5f) * (game().width / nTurrets);
            float y = height / 2f - Floor.FLOOR_HEIGHT / 2f - Turret.TURRET_HEIGHT / 2f;
            turret.setPosition(new PVector(x, y));
            parent.addChild(turret);
            battery.addTurret(turret);
        }
    }
}
