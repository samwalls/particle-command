package game.objects;

import engine.common.component.AABB;
import engine.common.component.GameObject;
import engine.common.physics.ColliderType;
import engine.common.physics.Contact;
import engine.common.physics.ForceType;
import game.RenderLayer;
import processing.core.PVector;

import java.awt.*;
import java.util.ArrayDeque;

import static game.MainComponent.game;

public class Projectile extends GameObject {

    public static final float DEFAULT_RADIUS = 10f;
    private static final float DEFAULT_MASS = 1f;

    private static final int TRAIL_PERIOD = 1;
    private static final int TRAIL_MAX = 20;

    private static final float EXPLOSION_ENERGY = 250f;

    private PlayArea playarea;

    private float gravity = 0.1f;

    private float dragK1, dragK2;

    private ArrayDeque<PVector> trailPositions = new ArrayDeque<>();
    private int trailFrameCounter = 0;
    private PVector lastTrailPos = new PVector();

    private Color projectileColour = new Color(255, 0, 0, 255);

    private Battery battery;

    public Projectile(PlayArea playArea, Battery battery, ColliderType colliderType, float radius, float mass, float dk1, float dk2) {
        this.playarea = playArea;
        this.battery = battery;
        physics().setMass(mass);
        collider().setType(colliderType);
        collider().setBoundingBox(AABB.circle(radius));
        this.dragK1 = dk1;
        this.dragK2 = dk2;
    }

    public Projectile(PlayArea playArea, Battery battery, float mass, float dk1, float dk2) {
        this(playArea, battery, ColliderType.CIRCLE, DEFAULT_RADIUS, mass, dk1, dk2);
    }

    public Projectile(PlayArea playArea, Battery battery) {
        this(playArea, battery, DEFAULT_MASS, 0, 0);
    }

    @Override
    public String renderLayer() {
        return RenderLayer.PROJECTILE.toString();
    }

    @Override
    public void onRender() {
        renderTrail();
//        renderParticle();
    }

    @Override
    public void onCollisionEnter(Contact contact) {
        reactToCollision(other(contact));
    }

    @Override
    public void onCollisionStay(Contact contact) {
        reactToCollision(other(contact));
    }

    public void triggerExplosion() {
        // create a new explosion
        Explosion e = new Explosion(EXPLOSION_ENERGY, projectileColour);
        // set the explosion as a sibling of this, and put where this projectile was
        parent.addChild(e);
        e.setPosition(position());
        // remove the projectile
        destroy();
    }

    private GameObject other(Contact contact) {
        return contact.B() == this ? contact.A() : contact.B();
    }

    private void reactToCollision(GameObject other) {
        // direct hit on a turret, destroy it
        if (battery.contains(other)) {
            other.destroy();
            battery.removeTurret((Turret)other);
        }
        if (!other.collider().isTrigger())
            triggerExplosion();
        applyForces(other);
    }

    private void applyForces(GameObject g) {
        // play area triggers regular forces
        if (g == playarea) {
            applyGravity();
            applyDrag();
        }
    }

    private void applyGravity() {
        physics().applyForce(toRotationalFrame(new PVector(0, gravity)), ForceType.ACCELERATION);
    }

    private void applyDrag() {
        // adapted as appropriate from the physics lecture slides
        PVector v = physics().getVelocity();
        float speed = v.mag();
        float dragCoefficient =
                dragK1 * speed +
                dragK2 * speed * speed;
        PVector drag = v.copy().normalize().mult(-dragCoefficient);
        physics().applyForce(drag);
    }

    private void renderParticle() {
        game().pushMatrix();
        PVector p = globalPosition();
        float radius = collider.getBoundingBox().outerRadius();
        game().translate(p.x, p.y);
        game().fill(projectileColour.getRGB(), projectileColour.getAlpha());
        game().strokeWeight(0);
        game().ellipse(0, 0, radius * 2, radius * 2);
        game().popMatrix();
    }

    private void renderTrail() {
        game().pushMatrix();
        PVector p = globalPosition();
        game().stroke(0);
        game().strokeWeight(1);
        game().fill(projectileColour.getRGB());
        if (++trailFrameCounter % TRAIL_PERIOD == 0) {
            trailPositions.push(new PVector(p.x, p.y));
            trailFrameCounter = 0;
        }
        renderTrailSegments();
        while (trailPositions.size() > TRAIL_MAX)
            trailPositions.removeLast();
        game().popMatrix();
    }

    private void renderTrailSegments() {
        if (trailPositions.size() >= 2) {
            PVector a = null, b;
            float i = 0;
            float alpha = 255;
            for (PVector trailPoint : trailPositions) {
                b = trailPoint;
                if (a != null) {
                    PVector strokeColour = new PVector(
                            projectileColour.getRed(),
                            projectileColour.getGreen(),
                            projectileColour.getBlue()
                    );
//                    strokeColour.mult(10f / i);
                    alpha /= 0.5 * i;
                    game().stroke(strokeColour.x, strokeColour.y, strokeColour.z, alpha);
                    float radius = collider.outerRadius();
//                    float trailWidth = 1.5f * radius * game().exp(-i / 100 * physics.getVelocity().mag());
                    float trailWidth = (radius - physics().getVelocity().mag() * (i / (radius * radius)));
                    game().strokeWeight(trailWidth >= 0 ? trailWidth : 0);
                    game().line(a.x, a.y, b.x, b.y);
                }
                a = b;
                i++;
            }
        }
    }

}
