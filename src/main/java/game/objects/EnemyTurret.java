package game.objects;

import engine.common.physics.ColliderType;
import engine.common.physics.ForceType;
import processing.core.PVector;

import java.util.Random;

import static game.MainComponent.game;

public class EnemyTurret extends Turret {

    // the number of frames that need to pass for a complete
    private static final float ROTATION_PERIOD = game().frameRate * 50f;

    // the number of frames that need to pass for an opportunity to fire
    private static final float FIRE_PERIOD = game().frameRate;

    private static final float FIRE_SPEED_MIN = 0f, FIRE_SPEED_MAX = 5f;
    private static final float FIRE_CONE_ANGLE =  180f * (game().PI / 180f);

    private final float spreadWidth = game().width - 200f;

    private Battery playerBattery;
    private float fireProbability = 0;
    private int fireDelta = 0;

    private int rotationDelta = 0;

    public EnemyTurret(PlayArea playArea, Battery battery, float fireProbability) {
        super(playArea);
        this.fireProbability = fireProbability;
        this.playerBattery = battery;
        // the enemy turrets should not collide with each other
        collider.setType(ColliderType.NONE);
        collider.setIsTrigger(true);
        physics.setKinematic(false);
    }

    @Override
    public void onRender() {
        PVector p = globalPosition();
        game().ellipse(p.x, p.y, 40, 40);
    }

    @Override
    public void onUpdate() {
        PVector p = position();
        // move back and forth accross the width
        setPosition(new PVector(-game().sin(period()) * spreadWidth / 2f, p.y));
        rotationDelta++;
        if (fireDelta++ >= FIRE_PERIOD) {
            potentiallyFire();
            fireDelta = 0;
        }
    }

    private float period() {
        if (rotationDelta >= ROTATION_PERIOD)
            rotationDelta = 0;
        return game().TWO_PI * (((float)rotationDelta) / ROTATION_PERIOD);
    }

    private void potentiallyFire() {
        if (fireProbability > 0 && new Random().nextInt((int)(1f/fireProbability)) == 0) {
            Projectile p = new EnemyProjectile(playArea, playerBattery);
            parent.addChild(p);
            p.setPosition(position());
            p.physics().applyForce(new PVector(0, 5f), ForceType.DISPLACEMENT);
            PVector v = new PVector(0, 1);
            float coneHalfAngle = FIRE_CONE_ANGLE / 2f;
            v = game().rotate(v, game().random(-coneHalfAngle, coneHalfAngle));
            v.mult(game().random(FIRE_SPEED_MIN, FIRE_SPEED_MAX));
            System.out.println(v);
            p.physics().applyForce(v, ForceType.VELOCITY);
        }
    }
}
