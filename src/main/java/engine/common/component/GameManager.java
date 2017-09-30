package engine.common.component;

import engine.common.event.*;
import processing.core.PApplet;

import java.util.*;
import java.util.function.Consumer;

/**
 * Singleton to help manage global states in the implemented game.
 */
public class GameManager extends PApplet {

    private static GameManager instance;

    private EventManager eventManager;
    private List<GameObject> gameObjects = new ArrayList<>();

    private ContactResolver contactResolver;

    public GameManager() {
        setInstance(this);
        eventManager = new EventManager();
        contactResolver = new ContactResolver();
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
        contactResolver.resolveAll();
        for (GameObject g : all())
            g.physics().integrate();
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

    public Iterable<GameObject> all() {
        return Collections.unmodifiableCollection(gameObjects);
    }

    //******** PACKAGE-LOCAL METHODS ********//

    void add(GameObject go) {
        gameObjects.add(go);
    }
}