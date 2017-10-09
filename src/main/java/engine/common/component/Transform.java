package engine.common.component;

import processing.core.PVector;

/**
 * Contains all useful elements of a 2D transform: translation, rotation and scaling. Note that scaling is not
 * implemented anywhere else in the engine as of yet.
 */
public class Transform implements Transformable {

    private PVector position = new PVector();

    private PVector scale = new PVector(1, 1);

    // rotation around Z axis (radians)
    private float rotation = 0f;

    @Override
    public PVector position() {
        return position.copy();
    }

    @Override
    public void setPosition(PVector position) {
        this.position = position.copy();
    }

    @Override
    public float rotation() {
        return rotation;
    }

    @Override
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    @Override
    public PVector scale() {
        return scale.copy();
    }

    @Override
    public void setScale(PVector scale) {
        this.scale = scale.copy();
    }
}