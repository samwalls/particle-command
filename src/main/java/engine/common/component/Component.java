package engine.common.component;

import engine.common.physics.Contact;
import processing.core.PVector;

public class Component extends RelativeTransform implements Drawable, Updatable, Collidable {

    public static final String DEFAULT_RENDER_LAYER = "default";

    protected Transform transform;

    public Component(Component parent) throws RelativeTransformCycleException {
        super(parent);
        transform = new Transform();
    }

    //******** PUBLIC METHODS ********//

    public void copyFrom(Component other) {
        setPosition(other.position());
        setRotation(other.rotation());
        setScale(other.scale());
    }

    /*
     * These event methods should be overridden to insert behaviour.
     */

    @Override
    public String renderLayer() {
        return DEFAULT_RENDER_LAYER;
    }

    @Override
    public void onRender() { }

    @Override
    public void onUpdate() { }

    @Override
    public void onCollisionEnter(Contact contact) { }

    @Override
    public void onCollisionStay(Contact contact) { }

    @Override
    public void onCollisionExit(GameObject other) { }

    /**
     * @return the coordinates
     */
    public PVector position() {
        return transform.position();
    }

    public void setPosition(PVector position) {
        transform.setPosition(position);
    }

    @Override
    public float rotation() {
        return transform.rotation();
    }

    @Override
    public void setRotation(float rotation) {
        transform.setRotation(rotation);
    }

    @Override
    public PVector scale() {
        return transform.scale();
    }

    @Override
    public void setScale(PVector scale) {
        transform.setScale(scale);
    }

    protected void setTransform(Transform transform) {
        this.transform = transform;
    }
}
