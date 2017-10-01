package engine.common.component;

import engine.common.event.*;
import engine.common.physics.ColliderType;
import engine.common.physics.ContactState;
import engine.common.physics.PhysicsComponent;
import processing.core.PVector;

import java.util.HashMap;
import java.util.Map;

import static engine.common.component.GameManager.game;

public class GameObject extends Component {

    private static final float MAX_SIZE = 100f;

    protected PhysicsComponent physics;

    protected ColliderType colliderType;

    private Map<GameObject, ContactState> contactStateMap;

    public GameObject(GameObject parent, ColliderType colliderType) {
        super(parent);
        physics = new PhysicsComponent(parent);
        physics.setTransform(transform);
        this.colliderType = colliderType;
        this.contactStateMap = new HashMap<>();
        game().add(this);
        setupEventHandlers();
    }

    public GameObject(ColliderType colliderType) {
        this(null, colliderType);
    }

    public GameObject() {
        this(ColliderType.NONE);
    }

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

    //******** PACKAGE-LOCAL METHODS ********//

    Map<GameObject, ContactState> getContactStateMap() {
        return contactStateMap;
    }

    //******** PRIVATE METHODS ********//

    private void setupEventHandlers() {
        // map game events to code supplied be the engine user
        game().on(UpdateEvent.class, event -> this.onUpdate());

        game().on(RenderEvent.class, event -> this.onRender());

        game().on(CollisionEnterEvent.class, event -> {
            CollisionEnterEvent e = (CollisionEnterEvent) event;
            // only do something if the event applies to this object
            if (e.contact.A() == this) {
                onCollisionEnter(e.contact);
                // call on the other object as well
                e.contact.B().onCollisionEnter(e.contact.copy());
            }
        });

        game().on(CollisionStayEvent.class, event -> {
            CollisionStayEvent e = (CollisionStayEvent) event;
            // only do something if the event applies to this object
            if (e.contact.A() == this) {
                onCollisionStay(e.contact);
                // call on the other object as well
                e.contact.B().onCollisionStay(e.contact.copy());
            }
        });

        game().on(CollisionExitEvent.class, event -> {
            CollisionExitEvent e = (CollisionExitEvent) event;
            // only do something if the event applies to this object
            if (e.A == this) {
                onCollisionExit(e.B);
                e.B.onCollisionExit(e.A);
            }
        });
    }
}