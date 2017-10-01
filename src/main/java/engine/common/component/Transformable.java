package engine.common.component;

import processing.core.PVector;

public interface Transformable {

    /**
     * @return a position vector
     */
    PVector position();

    /**
     * Set the position of this transformable object.
     * @param position the position to set to
     */
    void setPosition(PVector position);

    /**
     * @return the rotation around the z-axis.
     */
    float rotation();

    /**
     * Set the rotation around the z-axis.
     * @param rotation the desired rotation
     */
    void setRotation(float rotation);

    /**
     * @return a vector of the scale in x and y
     */
    public abstract PVector scale();

    /**
     * Set the scale in x and y.
     * @param scale a vector of the desired scale in x and y
     */
    public abstract void setScale(PVector scale);
}
