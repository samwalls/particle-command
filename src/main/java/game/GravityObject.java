package game;

import engine.common.ForceType;
import engine.common.GameObject;
import engine.common.physics.ColliderType;
import processing.core.PVector;

import java.util.ArrayList;

import static engine.common.AppContext.app;

public class GravityObject extends GameObject {

    static final float WALL_ELASTICITY = 1;
    static final float WALL_FRICTION_COEFFICIENT = 0;

    static final float GRAVITY_COEFFICIENT = 1;

    private ArrayList<GravityObject> interactiveObjects = new ArrayList();

    public GravityObject(float mass, ColliderType colliderType) {
        super(colliderType);
        physics().setMass(mass);
    }

    public void setInteractiveObjects(ArrayList<GravityObject> interactiveObjects) {
        this.interactiveObjects = interactiveObjects;
    }

    @Override
    public void update() {
        super.update();
        resolveCollisions();
        attractToOthers();
    }

    @Override
    public void render() {
        super.render();
        //renderDebug();
    }

    private void renderDebug() {
        renderVelocityDebug();
        renderAccelerationDebug();
        renderInformationDebug();
    }

    private void renderVelocityDebug() {
        app().stroke(255, 0, 0);
        PVector v = new PVector(
                physics().getVelocity().x,
                physics().getVelocity().y
        );
        v.mult(10);
        renderRelativeLine(v);
    }

    private void renderAccelerationDebug() {
        app().stroke(0, 255, 0);
        PVector a = new PVector(
                physics().getAcceleration().x,
                physics().getAcceleration().y
        );
        a.mult(100000);
        renderRelativeLine(a);
    }

    private void renderInformationDebug() {
        app().fill(255, 255, 255);
        app().text(physics().getMass(), physics().getPosition().x, physics().getPosition().y);
    }

    private void resolveCollisions() {
        PVector p = physics().getPosition();
        float radius = size() / 2;
        boolean isXOver = p.x <= radius || p.x >= app().width - radius;
        boolean isYOver = p.y <= radius || p.y >= app().height - radius;
        if (isXOver || isYOver) {
            PVector contactPosition = new PVector(
                    p.x <= radius ? radius : (p.x >= app().width - radius ? app().width - radius : p.x),
                    p.y <= radius ? radius : (p.y >= app().height - radius ? app().height - radius : p.y)
            );
            PVector reflectiveVelocity = new PVector(physics().getPosition().x, physics().getPosition().y);
            PVector v = physics().getVelocity();
            physics().setPosition(contactPosition);
            // the floor produces an inelastic collision (slight dampening in outcome velocity)
            reflectiveVelocity.sub(contactPosition);
            PVector friction = new PVector();
            if (isXOver) {
                reflectiveVelocity.x *= -WALL_ELASTICITY;
                friction.y = v.y * WALL_FRICTION_COEFFICIENT;
            }
            if (isYOver) {
                reflectiveVelocity.y *= -WALL_ELASTICITY;
                friction.x = v.x * WALL_FRICTION_COEFFICIENT;
            }
            friction.mult(-1);
            // apply friction
            //physics().applyForce(friction);
            reflectiveVelocity.mult(2);
            // only apply bounce if the force is above a threshold
            //if (reflectiveVelocity.mag() >= 0.5)
            physics().applyForce(reflectiveVelocity, ForceType.VELOCITY);
        }
    }

    private void attractToOthers() {
        PVector p = physics().getPosition();
        // apply basic "gravity"
        for (GameObject other : interactiveObjects) {
            if (other != this) {
                app().stroke(0, 0, 255);
                PVector displacement = new PVector(other.physics().getPosition().x - p.x, other.physics().getPosition().y - p.y);
                //println("energy " + other + (-0.5 * GRAVITY_COEFFICIENT * physics().getMass() * other.physics().getMass()) / (2 * displacement.mag()) + " | acceleration " + physics().getAcceleration());
                PVector gravity = new PVector(displacement.x, displacement.y);
                gravity.normalize();
                gravity.mult(displacement.mag() <= 1 ? 0 : (GRAVITY_COEFFICIENT * physics().getMass() * other.physics().getMass()) / app().pow(displacement.mag(), 2));
                //g.applyForce(new PVector(0, 1), ForceType.ACCELERATION);
                physics().applyForce(gravity);
            }
        }
    }
}