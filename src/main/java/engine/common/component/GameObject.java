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

    protected PhysicsComponent physics;

    protected ColliderComponent collider;

    private Map<GameObject, ContactState> contactStateMap;

    public GameObject(GameObject parent, ColliderType colliderType) {
        super(parent);
        physics = new PhysicsComponent(parent);
        physics.setTransform(transform);
        this.collider = new ColliderComponent(this, colliderType);
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

    public PhysicsComponent physics() {
        return physics;
    }

    public ColliderComponent collider() {
        return collider;
    }

    //******** PROTECTED METHODS ********//

    protected void renderRelativeLine(PVector v) {
        PVector o = globalPosition();
        PVector p = o.copy().add(v);
        game().line(o.x, o.y, p.x, p.y);
    }

    //******** PACKAGE-LOCAL METHODS ********//

    Map<GameObject, ContactState> getContactStateMap() {
        return contactStateMap;
    }

    //******** PRIVATE METHODS ********//

    private void setupEventHandlers() {
        // map game events to code supplied be the engine user
        game().on(UpdateEvent.class, event -> this.onUpdate());

        game().on(RenderEvent.class, event -> {
            // check that this game object is in the relevant render layer
            if (((RenderEvent)event).layer().equalsIgnoreCase(renderLayer())) {
                this.onRender();
            }
        });

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