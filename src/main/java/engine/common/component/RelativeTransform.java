package engine.common.component;

import processing.core.PVector;

public abstract class RelativeTransform implements Transformable {

    private RelativeTransform parent;

    public RelativeTransform(RelativeTransform parent) throws IllegalArgumentException {
        setParent(parent);
    }

    /**
     * @return the parent of this transformable
     */
    public RelativeTransform parent() {
        return parent;
    }

    public void setParent(RelativeTransform parent) throws IllegalArgumentException {
        for (RelativeTransform ptr = this; ptr != null; ptr = ptr.parent()) {
            if (parent == ptr)
                throw new IllegalArgumentException("attempt to set parent on relative transform would cause cycle");
        }
        this.parent = parent;
    }

    /**
     * @return the position relative to the parent (if any, otherwise, should be the same as position())
     */
    public PVector globalPosition() {
        PVector accumulator = position().copy();
        for (RelativeTransform ptr = parent(); ptr != null; ptr = ptr.parent()) {
            accumulator.add(ptr.position());
        }
        return accumulator;
    }

    /**
     * @return the rotation relative to the parent (if any, otherwise, should be the same as rotation())
     */
    public float globalRotation() {
        float accumulator = rotation();
        for (RelativeTransform ptr = parent(); ptr != null; ptr = ptr.parent()) {
            accumulator += ptr.rotation();
        }
        return accumulator;
    }

    /**
     * @return the scale relative to the parent (if any, otherwise, should be the same as scale())
     */
    public PVector globalScale() {
        PVector accumulator = scale().copy();
        for (RelativeTransform ptr = parent(); ptr != null; ptr = ptr.parent()) {
            accumulator.x *= ptr.scale().x;
            accumulator.y *= ptr.scale().y;
        }
        return accumulator;
    }
}
