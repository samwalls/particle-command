package game.objects;

import processing.core.PVector;

import java.awt.*;

public class PlayerProjectile extends Projectile {

    // distance to the target that must be reached before the projectile explodes
    private static final float TARGET_THRESHOLD_MINIMUM = 15f;

    private PVector target;

    private float initialDistance = -1;

    public PlayerProjectile(PlayArea playArea, Battery battery, PVector target) {
        super(playArea, battery);
        this.projectileColour = new Color(0, 255, 255, 255);
        this.target = target;
    }

    @Override
    public void onUpdate() {
        if (initialDistance == -1)
            initialDistance = target.copy().sub(globalPosition()).mag();
        // explode if we hit the target
        // the requirement for exploding is defined as a radius around the original target
        // (proportional to the distance away to account for distant targets)
        float threshold = TARGET_THRESHOLD_MINIMUM + (0.00001f * initialDistance * initialDistance);
        if (target.copy().sub(globalPosition()).mag() <= threshold)
            triggerExplosion();
    }
}
