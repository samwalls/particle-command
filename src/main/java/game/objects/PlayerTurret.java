package game.objects;

import engine.common.component.GameObject;
import engine.common.physics.Contact;
import engine.common.physics.ForceType;
import game.RenderLayer;
import processing.core.PVector;

import static game.MainComponent.game;

public class PlayerTurret extends Turret {

    private boolean isOperational = true;

    private static final float DEFAULT_HEALTH = 1000f;

    /**
     * The amount of an explosion's "power" (see power()) contributed to damage per frame in contact with the explosion
     */
    private static final float EXPLOSION_DAMAGE_COEFFICIENT = 1f/100f;

    private static final float HEALTH_DISPLAY_PADDING = 50f;
    private static final float HEALTH_BAR_WIDTH = 50f;
    private static final float HEALTH_BAR_HEIGHT = 5f;

    private float health;

    private Battery battery;

    public PlayerTurret(PlayArea playArea, Battery battery) {
        super(playArea);
        this.battery = battery;
        this.health = DEFAULT_HEALTH;
    }

    @Override
    public String renderLayer() {
        return RenderLayer.TURRET.toString();
    }

    @Override
    public void onRender() {
        game().pushMatrix();
        PVector p = globalPosition();
        float factor = health / DEFAULT_HEALTH;
        if (factor <= Float.MIN_VALUE)
            game().fill(0, 0, 0, turretColour.getAlpha());
        else
            game().fill(turretColour.getRed() * (1f - factor), turretColour.getGreen() * factor, turretColour.getBlue() * factor, turretColour.getAlpha());
        game().rect(p.x - TURRET_WIDTH / 2f, p.y - TURRET_HEIGHT / 2f, TURRET_WIDTH, TURRET_HEIGHT);
        game().popMatrix();
        renderHealth();
    }

    @Override
    public void onUpdate() {
        if (health <= 0) {
            isOperational = false;
            battery.removeTurret(this);
            health = 0f;
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

    private GameObject other(Contact contact) {
        return contact.B() == this ? contact.A() : contact.B();
    }

    public PlayerProjectile fire(Battery battery, float initialSeparation, float speed) {
        PlayerProjectile projectile = makeProjectile(battery);
        PVector p = globalPosition();
        PVector mouse = new PVector(game().mouseX, game().mouseY);
        PVector normal = mouse.sub(p).normalize();
        projectile.setPosition(position());
        projectile.physics().applyForce(normal.copy().mult(initialSeparation), ForceType.DISPLACEMENT);
        projectile.physics().applyForce(normal.copy().mult(speed), ForceType.VELOCITY);
        return projectile;
    }

    public boolean isOperational() {
        return isOperational;
    }

    private void reactToCollision(GameObject other) {
        if (other instanceof Explosion) {
            if (isOperational)
                health -= ((Explosion)other).power() * EXPLOSION_DAMAGE_COEFFICIENT;
        }
    }

    private PlayerProjectile makeProjectile(Battery battery) {
        PlayerProjectile p = new PlayerProjectile(playArea, battery, new PVector(game().mouseX, game().mouseY));
        // the projectile is then a sibling of this
        if (parent() != null)
            parent().addChild(p);
        return p;
    }

    private void renderHealth() {
        game().pushMatrix();
        PVector p = globalPosition();
        game().translate(p.x, p.y + HEALTH_DISPLAY_PADDING);
        game().pushStyle();
        // red bar goes behind
        game().fill(255, 0, 0);
        game().rect(-HEALTH_BAR_WIDTH / 2f, 0, HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);
        // green bar goes on top
        float point = HEALTH_BAR_WIDTH * (health / DEFAULT_HEALTH);
        game().fill(0, 255, 0);
        game().rect(-HEALTH_BAR_WIDTH / 2f, 0, point, HEALTH_BAR_HEIGHT);
        game().popStyle();
        game().popMatrix();
    }
}
