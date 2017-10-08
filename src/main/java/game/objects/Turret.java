package game.objects;

import engine.common.component.AABB;
import engine.common.component.GameObject;
import engine.common.physics.ColliderType;

import java.awt.*;

public class Turret extends GameObject {

    public static final float TURRET_WIDTH = 30f;
    public static final float TURRET_HEIGHT = 50f;

    private static float MASS = 100f;

    protected Color turretColour = new Color(140, 150, 140, 255);

    protected PlayArea playArea;

    public Turret(PlayArea playArea) {
        this.playArea = playArea;
        physics.setMass(MASS);
        physics.setKinematic(false);
        collider.setType(ColliderType.BOX);
        collider.setBoundingBox(AABB.box(TURRET_WIDTH, TURRET_HEIGHT));
    }
}
