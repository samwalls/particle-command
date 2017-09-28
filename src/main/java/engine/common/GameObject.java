package engine.common;

import engine.common.event.CollisionEnterEvent;
import engine.common.event.RenderEvent;
import engine.common.event.UpdateEvent;
import engine.common.physics.ColliderType;
import engine.common.physics.Contact;
import engine.common.physics.PhysicsComponent;
import processing.core.PVector;

import static engine.common.GameManager.game;

public class GameObject {

    private static final int MAX_SIZE = 100;

    protected Transform transform = new Transform();

    protected PhysicsComponent physics;

    protected ColliderType colliderType;

    public GameObject(ColliderType colliderType) {
        super();
        physics = new PhysicsComponent(transform);
        this.colliderType = colliderType;

        // map game events to code supplied be the engine user
        game().on(UpdateEvent.class, event -> this.onUpdate());
        game().on(RenderEvent.class, event -> this.onRender());
        game().on(CollisionEnterEvent.class, event -> {
            CollisionEnterEvent e = (CollisionEnterEvent) event;
            // only do something if the event applies to this object
            if (e.contact.A() == this)
                this.onCollisionEnter(e.contact);
        });
        game().add(this);
    }

    public GameObject() {
        this(ColliderType.NONE);
    }

    /**
     * Called during every update step. Should be overriden if one wishes to implement extra update behaviour.
     */
    public void onUpdate() {}

    /**
     * Called during every render step. Should be overriden if one wishes to implement extra rendering behaviour.
     */
    public void onRender() {}

    /**
     * Called before a collision contact is resolved. Should be overriden if one wishes to implement extra behaviour upon
     * entering a collision.
     */
    public void onCollisionEnter(Contact contact) {}

    //******** PUBLIC METHODS ********//

    // public property based on the mass of the object
    public float size() {
        return game().max(1, game().min(0.5f * physics().getMass(), MAX_SIZE));
    }

    // public access to this object's physics
    public PhysicsComponent physics() {
        return physics;
    }

    public ColliderType colliderType() {
        return colliderType;
    }

    //******** PROTECTED METHODS ********//

    protected void renderRelativeLine(PVector v) {
        game().line(physics().getPosition().x, physics().getPosition().y, physics().getPosition().x + v.x, physics().getPosition().y + v.y);
    }
}