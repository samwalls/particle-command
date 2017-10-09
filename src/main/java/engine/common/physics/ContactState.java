package engine.common.physics;

/**
 * A set of states for any the contact between any two given GameObjects
 */
public enum ContactState {

    /**
     * The objects are not colliding.
     */
    NONE,

    /**
     * The objects have begun colliding this frame.
     */
    ENTER,

    /**
     * The objects have been colliding indefinitely (for at least two frames)
     */
    STAY
}
