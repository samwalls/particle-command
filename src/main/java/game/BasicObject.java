package game;

import engine.common.physics.ForceType;
import engine.common.component.GameObject;
import engine.common.physics.ColliderType;
import engine.common.physics.Contact;
import processing.core.PVector;

import java.util.*;

import static engine.common.component.GameManager.game;

public class BasicObject extends GameObject {

    private static final int TRAIL_PERIOD = 1;
    private static final int TRAIL_MAX = 50;

    static final float WALL_ELASTICITY = 1;

    static final float GRAVITY_COEFFICIENT = 1;

    private static final float topBound = game().height / 2f - 100;
    private static final float bottomBound = -game().height / 2f + 100;
    private static final float leftBound = -game().width / 2f + 300;
    private static final float rightBound = game().width / 2f - 300;

    private List<BasicObject> interactiveObjects = new ArrayList<>();

    private ArrayDeque<PVector> trailPositions = new ArrayDeque();
    private int trailFrameCounter = 0;

    // map of things this object is touching, to the number of frames they have been touching for
    private Map<GameObject, Integer> touching = new HashMap<>();

    protected PVector colour;

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
    public String renderLayer() {
        return "particle";
    }

    @Override
    public void onRender() {
//        renderTrail();
        renderParticle();
        renderDebug();
    }

    @Override
    public void onCollisionEnter(Contact contact) {
        colour = new PVector(255, 0, 0);
    }

    @Override
    public void onCollisionStay(Contact contact) {
        GameObject other = contact.A() != this ? contact.A() : contact.B();
        if (!touching.containsKey(other))
            touching.put(other, 0);
        touching.put(other, touching.get(other) + 1);
        colour = new PVector(0, 255, 0);
    }

    @Override
    public void onCollisionExit(GameObject other) {
        touching.remove(other);
        if (touching.size() <= 0)
            colour = new PVector(0, 0, 255);
    }

    private void renderParticle() {
//        renderTrail();
        PVector p = globalPosition();
        // draw the particle as a circle
        game().pushMatrix();
        game().fill(colour.x, colour.y, colour.z);
        game().stroke(0, 0);
        float size = collider.outerRadius() * 2;
        game().ellipse(p.x, p.y, size, size);
        game().rotate(globalRotation());
        game().popMatrix();
        renderTouching();
    }

    private void renderTouching() {
        for (Map.Entry<GameObject, Integer> entry : touching.entrySet()) {
            game().pushMatrix();
            game().stroke(255, 0 , 0, entry.getValue());
            game().strokeWeight(2);
            PVector p1 = globalPosition();
            PVector p2 = entry.getKey().globalPosition();
            game().line(p1.x, p1.y, p2.x, p2.y);
            game().popMatrix();
        }
    }

    private void renderTrail() {
        game().pushMatrix();
        PVector p = globalPosition();
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
                    float radius = collider.outerRadius();
                    float trailWidth = 0.8f * (radius - physics().getVelocity().mag() * (i / radius));
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
        game().text(position().toString(), globalPosition().x, globalPosition().y);
    }

    private void resolveCollisions() {
        PVector p = position();
        float radius = collider.innerRadius();
        boolean left = p.x <= leftBound + radius;
        boolean right = p.x >= rightBound - radius;
        boolean bottom = p.y <= bottomBound + radius;
        boolean top = p.y >= topBound - radius;
        if (left || right || top || bottom) {
            PVector contactPosition = new PVector(
                    left ? leftBound + radius : (right ? rightBound - radius : p.x),
                    bottom ? bottomBound + radius : (top ? topBound - radius : p.y)
            );
            PVector reflectiveVelocity = p.copy().sub(contactPosition);
            setPosition(contactPosition);
            // the floor produces an inelastic collision (slight dampening in outcome velocity)
            if (left || right) {
                reflectiveVelocity.x *= -WALL_ELASTICITY;
            }
            if (top || bottom) {
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
        physics().applyForce(toRotationalFrame(new PVector(0, GRAVITY_COEFFICIENT)), ForceType.ACCELERATION);
    }
}