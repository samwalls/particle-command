package game.objects;

import engine.common.component.GameObject;
import engine.common.physics.ColliderType;
import engine.common.physics.Contact;
import game.MainComponent;
import game.RenderLayer;
import processing.core.PVector;

import java.awt.*;

import static game.MainComponent.game;

public class Explosion extends GameObject {

    private static final float BLAST_MIN = 5f, BLAST_MAX = 1000f;

    /**
     * number of frames an explosion takes to complete
     */
    private static final float BLAST_PERIOD = MainComponent.FRAME_RATE * 2;

    /**
     * the amount of "power" (see power()) contributed towards outwards force in an explosion
     */
    private static final float PRESSURE_COEFFICIENT = 0.1f;

    private Color explosionColour;

    private float energy;

    /**
     * updates passed since the explosion was created
     */
    private int delta = 0;

    public Explosion(float energy, Color explosionColour) {
        // clip energy to [0, 1000]
        this.energy = game().max(BLAST_MIN, game().min(energy, BLAST_MAX));
        this.explosionColour = explosionColour;
        physics.setKinematic(false);
        collider.setIsTrigger(true);
        collider.setType(ColliderType.CIRCLE);
        updateColliderZone();
    }

    @Override
    public void onUpdate() {
        // expand/shrink the collider area
        updateColliderZone();
        // get rid of the explosion if it's finished
        if (delta++ >= BLAST_PERIOD)
            destroy();
    }

    @Override
    public void onRender() {
        game().pushMatrix();
        game().pushStyle();
        game().fill(explosionColour.getRGB(), 255f * game().cos(period()));
        game().stroke(0, 0);
        PVector p = globalPosition();
        float radius = collider.getBoundingBox().outerRadius();
        game().ellipse(p.x, p.y, radius * 2, radius * 2);
        game().popStyle();
        game().popMatrix();
    }

    @Override
    public String renderLayer() {
        // some explosions may render in front of or behind missiles, this is desired
        return RenderLayer.PROJECTILE.toString();
    }

    @Override
    public void onCollisionEnter(Contact contact) {
        applyForces(contact);
    }

    @Override
    public void onCollisionStay(Contact contact) {
        applyForces(contact);
    }

    public float power() {
        return energy * game().cos(period());
    }

    private void applyForces(Contact contact) {
        GameObject other = contact.B() == this ? contact.A() : contact.B();
        if (!other.collider().isTrigger()) {
            PVector pressure = other.globalPosition().sub(globalPosition());
            float distance = pressure.mag();
            pressure.div(game().max(Float.MIN_VALUE, distance * distance));
            pressure.mult(power() * PRESSURE_COEFFICIENT);
            other.physics().applyForce(pressure);
        }
    }

    private float period() {
        // delta == 0 ? 0
        // delta == BLAST_PERIOD ? PI/2
        return game().HALF_PI * (((float)delta) / BLAST_PERIOD);
    }

    private void updateColliderZone() {
        collider.getBoundingBox().updateSize(energy * game().sin(period()));
    }
}
