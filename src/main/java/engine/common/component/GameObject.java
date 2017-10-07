package engine.common.component;

import engine.common.event.*;
import engine.common.physics.ColliderType;
import engine.common.physics.ContactState;
import engine.common.physics.PhysicsComponent;
import processing.core.PVector;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static engine.common.component.GameManager.game;

public class GameObject extends Component {

    protected PhysicsComponent physics;

    protected ColliderComponent collider;

    private Map<GameObject, ContactState> contactStateMap;

    private boolean isDestroyed = false;

    // map game events to code supplied be the engine user

    private Consumer<Event> onUpdate = event -> this.onUpdate();

    private Consumer<Event> onRender = event -> {
        // check that this game object is in the relevant render layer
        if (((RenderEvent)event).layer().equalsIgnoreCase(renderLayer())) {
            this.onRender();
        }
    };

    private Consumer<Event> onCollisionEnter = event -> {
        CollisionEnterEvent e = (CollisionEnterEvent) event;
        // only do something if the event applies to this object
        if (e.contact.A() == this) {
            onCollisionEnter(e.contact);
            // call on the other object as well
            e.contact.B().onCollisionEnter(e.contact.copy());
        }
    };

    private Consumer<Event> onCollisionStay = event -> {
        CollisionStayEvent e = (CollisionStayEvent) event;
        // only do something if the event applies to this object
        if (e.contact.A() == this) {
            onCollisionStay(e.contact);
            // call on the other object as well
            e.contact.B().onCollisionStay(e.contact.copy());
        }
    };

    private Consumer<Event> onCollisionExit = event -> {
        CollisionExitEvent e = (CollisionExitEvent) event;
        // only do something if the event applies to this object
        if (e.A == this) {
            onCollisionExit(e.B);
            e.B.onCollisionExit(e.A);
        }
    };

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

    public boolean isDestroyed() {
        return isDestroyed;
    }

    /**
     * Destroy this GameObject, un-mapping all of it's events and removing it from the update loop, this cannot be undone.
     * All children of this object will be given to the parent, if a parent exists.
     */
    public void destroy() {
        isDestroyed = true;
        removeEventHandlers();
        game().remove(this);
        giveChildrenToParent();
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
        game().on(UpdateEvent.class, onUpdate);
        game().on(RenderEvent.class, onRender);
        game().on(CollisionEnterEvent.class, onCollisionEnter);
        game().on(CollisionStayEvent.class, onCollisionStay);
        game().on(CollisionExitEvent.class, onCollisionExit);
    }

    private void removeEventHandlers() {
        game().removeEvent(onUpdate);
        game().removeEvent(onRender);
        game().removeEvent(onCollisionEnter);
        game().removeEvent(onCollisionStay);
        game().removeEvent(onCollisionExit);
    }

    private void giveChildrenToParent() {
        for (RelativeTransform c : children)
            c.parent = parent;
    }
}