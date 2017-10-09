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

/**
 * The building blocks upon which games are created. This class provides easy out-of-the box support for hooking into
 * render, update, and collision events. As well as this, the object provides access to its current state of the physics
 * and collider - and a <i>transform</i> for defining position, rotation etc..
 * <br><br>
 * As GameObject is a {@link Component} it also contains the notion of the {@link RelativeTransform} - i.e. that
 * other GameObjects can be added as children, which inherit the parent's transform properties.
 * @see {@link RelativeTransform}
 */
public class GameObject extends Component {

    protected PhysicsComponent physics;

    protected ColliderComponent collider;

    /**
     * The mapping of other GameObjects to the contact state between it and this.
     */
    private Map<GameObject, ContactState> contactStateMap;

    /**
     * Has this GameObject been called to be removed?
     */
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
        // the physics component *must* share the transform instance of this object
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

    /**
     * This GameObject's physics configuration.
     */
    public PhysicsComponent physics() {
        return physics;
    }

    /**
     * This GameObject's collider configuration.
     */
    public ColliderComponent collider() {
        return collider;
    }

    /**
     * @return true if this GameObject has been called to be destroyed.
     */
    public boolean isDestroyed() {
        return isDestroyed;
    }

    /**
     * Destroy this GameObject, un-mapping all of it's events and removing it from the update loop, this cannot be undone.
     */
    public void destroy() {
        isDestroyed = true;
        game().remove(this);
        removeEventHandlers();
        System.out.println(this.toString() + " was destroyed");
    }

    //******** PACKAGE-LOCAL METHODS ********//

    /**
     * @return the mapping of other GameObjects to the contact state between them and this.
     */
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
}