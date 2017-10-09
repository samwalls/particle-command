package engine.common.physics;

/**
 * The various types a collider can take on to translate it's bounding box into collision detections.
 */
public enum ColliderType {

    /**
     * The object does not collide with anything.
     */
    NONE,

    /**
     * The object's bounding box defines a circular collider.
     */
    CIRCLE,

    /**
     * The object's bounding box defines a rectangular collider.
     */
    BOX
}