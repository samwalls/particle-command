package engine.common.component;

import engine.common.event.*;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.*;
import java.util.function.Consumer;

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
        cleanup();
        game().emit(new UpdateEvent());
        contactResolver.resolveAll();
        for (GameObject g : all())
            g.physics().integrate();
        renderAll();
    }

    public void on(Class<? extends Event> type, Consumer<Event> consumer) {
        eventManager.on(type, consumer);
    }

    /**
     * Emit the specified event
     * @param event the event to emit to listeners of the type of event emitted
     */
    public void emit(Event event) {
        eventManager.emit(event);
    }

    public void removeEvent(Consumer<Event> consumer) {
        eventManager.remove(consumer);
    }

    public Iterable<GameObject> all() {
        return gameObjects;
    }

    public void setRenderingLayers(List<String> layers) {
        this.renderLayers = layers;
        defaultRenderLayerDefined = false;
        for (String layer : layers)
            if (layer.equalsIgnoreCase(Component.DEFAULT_RENDER_LAYER))
                defaultRenderLayerDefined = true;
    }

    public Collection<String> renderingLayers() {
        return Collections.unmodifiableCollection(renderLayers);
    }

    public PVector rotate(PVector v, float rotation) {
        return new PVector(
                v.x * cos(rotation) - v.y * sin(rotation),
                v.x * sin(rotation) + v.y * cos(rotation)
        );
    }

    //******** PACKAGE-LOCAL METHODS ********//

    void add(GameObject g) {
        gameObjectsToAdd.add(g);
    }

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