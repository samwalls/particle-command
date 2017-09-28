package engine.common;

import engine.common.event.*;
import engine.common.physics.Collision;
import engine.common.physics.Contact;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;
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
            for (Contact c : detectCollisions(g)) {
                // TODO implement onCollisionStay and onCollisionExit - will require cache of collisions for each gameObject, somewhere...
                c.resolve();
                game().emit(new CollisionEnterEvent(c));
            }
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
}