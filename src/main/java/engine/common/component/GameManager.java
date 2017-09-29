package engine.common.component;

import engine.common.event.*;
import engine.common.physics.Collision;
import engine.common.physics.Contact;
import engine.common.physics.ContactState;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Singleton to help manage global states in the implemented game.
 */
public class GameManager extends PApplet {

    private static GameManager instance;

    private EventManager eventManager;
    private List<GameObject> gameObjects = new ArrayList<>();

    public GameManager() {
        eventManager = new EventManager();
        setInstance(this);
    }

    public static GameManager game() {
        return instance;
    }

    /**
     * Sets the singleton instance if it has not been set already.
     */
    public void setInstance(GameManager instance) {
        if (GameManager.instance == null)
            GameManager.instance = instance;
    }

    //******** PUBLIC METHODS ********//

    public void updateAll() {
        game().emit(new UpdateEvent());
        for (GameObject g : gameObjects) {
            // check for collision exits, both before and after resolving contacts
            checkForCollisionExits(g);
            for (Contact c : detectCollisions(g)) {
                // TODO use an iterative process to resolve extra collisions
                c.resolve();
                handleActiveContact(g, c);
            }
            checkForCollisionExits(g);
            g.physics().integrate();
        }
        game().emit(new RenderEvent());
    }

    void on(Class<? extends Event> type, Consumer<Event> consumer) {
        eventManager.on(type, consumer);
    }

    /**
     * Emit the specified event
     * @param event the event to emit to listeners of the type of event emitted
     */
    void emit(Event event) {
        eventManager.emit(event);
    }

    //******** PACKAGE-LOCAL METHODS ********//

    void add(GameObject go) {
        gameObjects.add(go);
    }

    List<GameObject> gameObjects() {
        return gameObjects;
    }

    //******** PRIVATE METHODS ********//

    private ArrayList<Contact> detectCollisions(GameObject g) {
        // TODO - there are many ways of doing this
        ArrayList<Contact> collisions = new ArrayList<>();
        for (GameObject other : gameObjects) {
            if (other == g)
                continue;
            Contact contact = Collision.areContacting(g, other);
            if (contact != null) {
                collisions.add(contact);
            }
        }
        return collisions;
    }

    private void handleActiveContact(GameObject g, Contact contact) {
        GameObject other = contact.B();
        Map<GameObject, ContactState> contactStateMap = g.getContactStateMap();
        // add the other object to the map if it doesn't exist (initial contact state NONE)
        if (!contactStateMap.containsKey(other)) {
            contactStateMap.put(other, ContactState.NONE);
        }
        // emit collision events based on the contact state
        switch (contactStateMap.get(other)) {
            case NONE:
                // the objects were not colliding before
                contactStateMap.put(other, ContactState.ENTER);
                emit(new CollisionEnterEvent(contact));
                break;
            case ENTER:
                // the objects were colliding in the last update and are colliding now
                contactStateMap.put(other, ContactState.STAY);
                emit(new CollisionStayEvent(contact));
                break;
            case STAY:
                // the objects collided indefinitely before beforehand, and are still colliding
                emit(new CollisionStayEvent(contact));
                break;
        }
    }

    private void checkForCollisionExits(GameObject g) {
        Map<GameObject, ContactState> contactStateMap = g.getContactStateMap();
        for (Map.Entry<GameObject, ContactState> entry : contactStateMap.entrySet()) {
            ContactState contactState = entry.getValue();
            // if the object is reported to be involved in a collision, check for collision exit
            boolean contactingBefore = contactState == ContactState.ENTER || contactState == ContactState.STAY;
            // if the objects were contacting previously, and are no longer, emit a collisionExitEvent
            if (contactingBefore && Collision.areContacting(g, entry.getKey()) == null) {
                contactStateMap.put(entry.getKey(), ContactState.NONE);
                emit(new CollisionExitEvent(g, entry.getKey()));
            }
        }
    }
}