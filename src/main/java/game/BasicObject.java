package game;

import engine.common.physics.ForceType;
import engine.common.component.GameObject;
import engine.common.physics.ColliderType;
import engine.common.physics.Contact;
import processing.core.PVector;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import static engine.common.component.GameManager.game;

public class BasicObject extends GameObject {

    private static final int TRAIL_PERIOD = 1;
    private static final int TRAIL_MAX = 50;

    static final float WALL_ELASTICITY = 1;

    static final float GRAVITY_COEFFICIENT = 1;

    private List<BasicObject> interactiveObjects = new ArrayList<>();

    private ArrayDeque<PVector> trailPositions = new ArrayDeque();
    private int trailFrameCounter = 0;

    private PVector colour;

    public BasicObject(float mass, ColliderType colliderType) {
        super(colliderType);
        physics().setMass(mass);
        this.colour = new PVector(
            game().random(0, 255),
            game().random(0, 255),
            game().random(0, 255)
        );
    }

    public void setInteractiveObjects(List<BasicObject> interactiveObjects) {
        this.interactiveObjects = interactiveObjects;
    }

    @Override
    public void onUpdate() {
        resolveCollisions();
        attractToOthers();
    }

    @Override
    public void onRender() {
//        renderTrail();
        renderParticle();
    }

    @Override
    public void onCollisionEnter(Contact contact) {
        colour = new PVector(255, 0, 0);
    }

    @Override
    public void onCollisionStay(Contact contact) {
        colour = new PVector(0, 255, 0);
    }

    @Override
    public void onCollisionExit(GameObject other) {
        colour = new PVector(0, 0, 255);
    }

    private void renderParticle() {
        PVector p = physics.getPosition();
        // draw the particle as a circle
        game().pushMatrix();
        game().fill(colour.x, colour.y, colour.z);
        float radius = size() / 2;
        game().ellipse(p.x, p.y, radius * 2, radius * 2);
        game().rotate(transform.rotation);
        game().popMatrix();
    }

    private void renderTrail() {
        game().pushMatrix();
        PVector p = physics().getPosition();
        game().stroke(0);
        game().strokeWeight(1);
        game().fill(colour.x, colour.y, colour.z);
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
            for (PVector trailPoint : trailPositions) {
                b = trailPoint;
                if (a != null) {
                    PVector strokeColour = new PVector(colour.x, colour.y, colour.z);
                    strokeColour.mult(5f / i);
                    game().stroke(strokeColour.x, strokeColour.y, strokeColour.z);
                    float trailWidth = 0.8f * (size() - physics().getVelocity().mag() * (i / size()));
                    game().strokeWeight(trailWidth >= 0 ? trailWidth : 0);
                    game().line(a.x, a.y, b.x, b.y);
                }
                a = b;
                i++;
            }
        }
    }

    private void renderDebug() {
        renderVelocityDebug();
        renderAccelerationDebug();
        renderInformationDebug();
    }

    private void renderVelocityDebug() {
        game().stroke(255, 0, 0);
        PVector v = new PVector(
                physics().getVelocity().x,
                physics().getVelocity().y
        );
        v.mult(10);
        renderRelativeLine(v);
    }

    private void renderAccelerationDebug() {
        game().stroke(0, 255, 0);
        PVector a = new PVector(
                physics().getAcceleration().x,
                physics().getAcceleration().y
        );
        a.mult(100000);
        renderRelativeLine(a);
    }

    private void renderInformationDebug() {
        game().fill(255, 255, 255);
        game().text(physics().getMass(), physics().getPosition().x, physics().getPosition().y);
    }

    private void resolveCollisions() {
        PVector p = physics().getPosition();
        float radius = size() / 2;
        boolean isXOver = p.x <= radius || p.x >= game().width - radius;
        boolean isYOver = p.y <= radius || p.y >= game().height - radius;
        if (isXOver || isYOver) {
            PVector contactPosition = new PVector(
                    p.x <= radius ? radius : (p.x >= game().width - radius ? game().width - radius : p.x),
                    p.y <= radius ? radius : (p.y >= game().height - radius ? game().height - radius : p.y)
            );
            PVector reflectiveVelocity = physics().getPosition().copy().sub(contactPosition);
            physics().setPosition(contactPosition);
            // the floor produces an inelastic collision (slight dampening in outcome velocity)
            if (isXOver) {
                reflectiveVelocity.x *= -WALL_ELASTICITY;
            }
            if (isYOver) {
                reflectiveVelocity.y *= -WALL_ELASTICITY;
            }
            physics().applyForce(reflectiveVelocity, ForceType.VELOCITY);
        }
    }

    private void attractToOthers() {
        PVector p = physics().getPosition();
        // apply basic "gravity"
//        for (GameObject other : interactiveObjects) {
//            if (other != this) {
//                game().stroke(0, 0, 255);
//                PVector displacement = new PVector(other.physics().getPosition().x - p.x, other.physics().getPosition().y - p.y);
//                PVector gravity = new PVector(displacement.x, displacement.y);
//                gravity.normalize();
//                gravity.mult(displacement.mag() <= 1 ? 0 : (GRAVITY_COEFFICIENT * physics().getMass() * other.physics().getMass()) / game().pow(displacement.mag(), 2));
//                physics().applyForce(gravity);
//            }
//        }
//        physics().applyForce(new PVector(0, GRAVITY_COEFFICIENT), ForceType.ACCELERATION);
    }
}