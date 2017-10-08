package game.objects;

import engine.common.physics.ForceType;
import processing.core.PVector;

import static game.MainComponent.game;

public class PlayerTurret extends Turret {

    private boolean isOperational = true;

    public PlayerTurret(PlayArea playArea) {
        super(playArea);
    }

    @Override
    public void onRender() {
        game().pushMatrix();
        PVector p = globalPosition();
        game().fill(turretColour.getRGB(), turretColour.getAlpha());
        game().rect(p.x - TURRET_WIDTH / 2f, p.y - TURRET_HEIGHT / 2f, TURRET_WIDTH, TURRET_HEIGHT);
        game().popMatrix();
    }

    public void fire(Battery battery, float initialSeparation, float speed) {
        Projectile projectile = makeProjectile(battery);
        PVector p = globalPosition();
        PVector mouse = new PVector(game().mouseX, game().mouseY);
        PVector normal = mouse.sub(p).normalize();
        projectile.setPosition(position());
        projectile.physics().applyForce(normal.copy().mult(initialSeparation), ForceType.DISPLACEMENT);
        projectile.physics().applyForce(normal.copy().mult(speed), ForceType.VELOCITY);
    }

    public boolean isOperational() {
        return isOperational;
    }

    private Projectile makeProjectile(Battery battery) {
        Projectile p = new PlayerProjectile(playArea, battery, new PVector(game().mouseX, game().mouseY));
        // the projectile is then a sibling of this
        if (parent() != null)
            parent().addChild(p);
        return p;
    }
}
