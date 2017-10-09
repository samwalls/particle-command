package game.objects;

import engine.common.component.GameObject;
import engine.common.physics.Contact;
import processing.core.PVector;

import static game.MainComponent.game;

public class EnemyProjectile extends Projectile {

    private Game game;

    public EnemyProjectile(PlayArea playArea, Battery battery, Game game) {
        super(playArea, battery);
        this.game = game;
    }

    @Override
    public void onRender() {
        renderParticle();
    }

    @Override
    public void onCollisionExit(GameObject other) {
        if (other == playarea) {
            PVector p = globalPosition();
            if (p.x < 0) {
                game.addSideEvasionScore(false, p.y);
            } else if (p.x > game().width) {
                game.addSideEvasionScore(true, p.y);
            } else if (p.y < 0) {
                game.addTopEvasionScore(p.x);
            }
        }
    }

    @Override
    public void onCollisionEnter(Contact contact) {
        reactToCollision(other(contact));
    }

    @Override
    public void onCollisionStay(Contact contact) {
        reactToCollision(other(contact));
    }

    private void reactToCollision(GameObject other) {
        boolean otherIsTrigger = other.collider().isTrigger();
        // direct hit on a turret, destroy it
        if (!otherIsTrigger) {
            if (other instanceof Turret) {
                other.destroy();
                playerBattery.removeTurret((Turret)other);
            }
            if (other instanceof PlayerProjectile) {
                PVector p = globalPosition();
                game.addDirectHitScore(p.x, p.y);
            }
            if (!(other instanceof EnemyProjectile)) {
                // explode if the projectile hit something concrete (that's not a fellow missile)
                triggerExplosion();
            }
        }
        // apply regular forces
        applyForces(other);
    }
}
