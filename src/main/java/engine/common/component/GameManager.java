package engine.common.component;

import engine.common.event.*;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Singleton to help manage global states in the implemented game.
 */
public class GameManager extends PApplet {

    private static GameManager instance;

    private EventManager eventManager;
    private List<GameObject> gameObjects = new ArrayList<>();
    private List<GameObject> gameObjectsToAdd = new ArrayList<>();
    private List<GameObject> gameObjectsToRemove = new ArrayList<>();
    private List<String> renderLayers = new ArrayList<>();
    private boolean defaultRenderLayerDefined = false;

    private ContactResolver contactResolver;

    public GameManager() {
        setInstance(this);
        eventManager = new EventManager();
        contactResolver = new ContactResolver();
    }

    /**
     * @return the singleton instance of the GameManager
     */
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

    /**
     * Update all objects, handle contacts, and render everything.
     */
    public void updateAll() {
        cleanup();
        game().emit(new UpdateEvent());
        contactResolver.resolveAll();
        all().forEach( g -> g.physics().integrate());
        renderAll();
    }

    /**
     * Map an event handle to the game's event manager.
     * @param type the type of event to map from
     * @param consumer the event handler
     */
    public void on(Class<? extends Event> type, Consumer<Event> consumer) {
        eventManager.on(type, consumer);
    }

    /**
     * Emit the specified event in the game's event manager.
     * @param event the event to emit to listeners of the type of event emitted
     */
    public void emit(Event event) {
        eventManager.emit(event);
    }

    /**
     * Remove an event mapping from this game's event manager.
     * @param consumer the event handler instance
     */
    public void removeEvent(Consumer<Event> consumer) {
        eventManager.remove(consumer);
    }

    /**
     * @return an iterable stream over all GameObjects in the game
     */
    public Stream<GameObject> all() {
        // any that have to be added in the next update will be instantly removed as well
        return Stream.concat(gameObjects.stream(), gameObjectsToAdd.stream());
    }

    /**
     * Set the list of rendering layer identifiers (and hence the order).
     * @param layers the list of rendering layers
     */
    public void setRenderingLayers(List<String> layers) {
        this.renderLayers = layers;
        defaultRenderLayerDefined = false;
        for (String layer : layers)
            if (layer.equalsIgnoreCase(Component.DEFAULT_RENDER_LAYER))
                defaultRenderLayerDefined = true;
    }

    /**
     * @return the ordered collection of rendering layer identifiers
     */
    public Collection<String> renderingLayers() {
        return Collections.unmodifiableCollection(renderLayers);
    }

    /**
     * Rotate the given vector (creating a copy) by the target rotation (radians).
     * @param v the vector to rotate
     * @param rotation the target rotation in radians
     * @return a new vector, equal to the given vector, rotated by the target rotation
     */
    public PVector rotate(PVector v, float rotation) {
        return new PVector(
                v.x * cos(rotation) - v.y * sin(rotation),
                v.x * sin(rotation) + v.y * cos(rotation)
        );
    }

    //******** PACKAGE-LOCAL METHODS ********//

    /**
     * Add a GameObject to the game.
     * @param g the GameObject to add
     */
    void add(GameObject g) {
        gameObjectsToAdd.add(g);
    }

    /**
     * Remove a GameObject from the game.
     * @param g the GameObject to remove
     */
    void remove(GameObject g) {
        gameObjectsToRemove.add(g);
    }

    //******** PRIVATE METHODS ********//

    private void renderAll() {
        if (renderLayers == null) {
            game().emit(new RenderEvent());
            return;
        }
        // if no default layer is defined, render it first
        if (!defaultRenderLayerDefined)
            game().emit(new RenderEvent());
        for (String layer : renderLayers)
            game().emit(new RenderEvent(layer));
    }

    /**
     * Remove all latent GameObjects that have to be added, and likewise removed.
     */
    private void cleanup() {
        // add objects that need to be added, remove those that need to be removed
        if (gameObjectsToAdd.size() > 0) {
            gameObjects.addAll(gameObjectsToAdd);
            gameObjectsToAdd.clear();
        }
        if (gameObjectsToRemove.size() > 0) {
            gameObjects.removeAll(gameObjectsToRemove);
            gameObjectsToRemove.clear();
        }
    }
}