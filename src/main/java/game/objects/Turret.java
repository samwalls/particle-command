package game.objects;

import engine.common.component.AABB;
import engine.common.component.GameObject;
import engine.common.physics.ColliderType;
import engine.common.physics.ForceType;
import processing.core.PVector;

import java.awt.*;

import static game.MainComponent.game;

public class Turret extends GameObject {

    private static float MASS = 100f;

    private static final float TURRET_ROTATION_MIN = - game().QUARTER_PI;
    private static final float TURRET_ROTATION_MAX = game().QUARTER_PI;

    private static final float TURRET_WIDTH = 30f;
    private static final float TURRET_HEIGHT = 50f;

    private Color turretColour = new Color(140, 150, 140, 255);

    private float turretRotation = 0;

    public Turret() {
        physics.setMass(MASS);
        physics.setKinematic(false);
        collider.setType(ColliderType.BOX);
        collider.setBoundingBox(AABB.box(TURRET_WIDTH, TURRET_HEIGHT));
    }

    @Override
    public void onRender() {
        game().pushMatrix();
        PVector p = globalPosition();
        game().fill(turretColour.getRGB(), turretColour.getAlpha());
        game().rect(p.x - TURRET_WIDTH / 2f, p.y - TURRET_HEIGHT / 2f, TURRET_WIDTH, TURRET_HEIGHT);
        game().popMatrix();
    }

    public void setProjectileBearing(float rotation) {
        this.turretRotation = game().min(TURRET_ROTATION_MIN, game().max(rotation, TURRET_ROTATION_MAX));
    }

    public void emit(float initialSeparation, float speed) {
        Projectile p = makeProjectile();
        p.physics().applyForce(game().rotate(new PVector(0, -initialSeparation), turretRotation), ForceType.DISPLACEMENT);
        p.physics().applyForce(game().rotate(new PVector(0, -speed), turretRotation), ForceType.VELOCITY);
    }

    private Projectile makeProjectile() {
        // TODO
        Projectile p = new Projectile(null);
        // the projectile is then a sibling of this
        parent().addChild(p);
        return p;
    }
}
