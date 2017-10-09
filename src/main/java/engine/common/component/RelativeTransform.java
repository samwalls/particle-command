package engine.common.component;

import processing.core.PVector;

import java.util.*;

import static engine.common.component.GameManager.game;

/**
 * An abstract base class which implemented the notion of inherited transform properties. E.g. all children have a global
 * translation of the parent's global translation plus their own.
 * <br><br>
 * RelativeTransforms have links to their parent, as well as a list of their children.
 */
public abstract class RelativeTransform implements Transformable {

    /**
     * The last known root of this RelativeTransform's hierarchy.
     */
    protected RelativeTransform root;

    /**
     * This RelativeTransform's parent (if it exists).
     */
    protected RelativeTransform parent;

    /**
     * This RelativeTransform's set of children.
     */
    protected Set<RelativeTransform> children;

    public RelativeTransform(RelativeTransform parent) throws RelativeTransformCycleException {
        setParent(parent);
        children = new HashSet<>();
    }

    /**
     * @return the parent of this transformable
     */
    public RelativeTransform parent() {
        return parent;
    }

    /**
     * Add a child to this RelativeTransform, the child's parent will also be set to this.
     * @param child the child to add
     * @throws RelativeTransformCycleException if adding a child would cause a cyclical relationship
     */
    public void addChild(RelativeTransform child) throws RelativeTransformCycleException {
        try {
            child.setParent(this);
        } catch (RelativeTransformCycleException e) {
            throw new RelativeTransformCycleException("attempt to set parent in child to-be-added would cause cycle", e);
        }
        children.add(child);
    }

    private void setParent(RelativeTransform parent) throws RelativeTransformCycleException {
        for (RelativeTransform ptr = this; ptr != null; ptr = ptr.parent()) {
            if (parent == ptr)
                throw new IllegalArgumentException("attempt to set parent on relative transform would cause cycle");
        }
        this.parent = parent;
        for (RelativeTransform ptr = this; ptr != null; ptr = ptr.parent())
            if (ptr.parent() == null)
                this.root = ptr;
    }

    public void removeChild(RelativeTransform child) {
        if (children.contains(child)) {
            children.remove(child);
            child.parent = null;
            child.root = null;
        }
    }

    /**
     * @param v the vector to transform
     * @return the input vector, translated as if it were created as a child of this object (the global position still
     * produces the same result, but it's translated in terms of this object's parent)
     */
    public PVector toReferenceFrame(PVector v) {
        PVector parentReferenceFrame = isRoot() ? new PVector() : parent().globalPosition();
        return v.copy().sub(parentReferenceFrame);
    }

    /**
     * @param v the global-space vector to transform
     * @return the input vector, rotated appropriately as if it were created as a child of this object (the global
     * position still produces the same result, but it's translated in terms of this object's parent)
     */
    public PVector toRotationalFrame(PVector v) {
        PVector output = v.copy();
        if (!isRoot()) {
            // subtract this object's parent's rotation to interpret this vector in the rotational frame of this object
            output.x = output.x * game().cos(-parent.globalRotation()) - output.y * game().sin(-parent.globalRotation());
            output.y = output.y * game().sin(-parent.globalRotation()) + output.y * game().cos(-parent.globalRotation());
        }
        return output;
    }

    /**
     * @return the total position relative to the world
     */
    public PVector globalPosition() {
        Deque<RelativeTransform> path = new ArrayDeque<>();
        // construct a path from the root node to this game object
        for (RelativeTransform ptr = this; ptr != null; ptr = ptr.parent()) {
            path.push(ptr);
        }
        if (path.size() <= 0)
            return position();
        PVector accumulator = new PVector();
        // follow the path down to construct the final position
        RelativeTransform ptr = path.pop();
        while (ptr != null) {
            PVector displacement;
            if (ptr.isRoot()) {
                displacement = ptr.position();
            } else {
                // the position needs to account for the parent's rotation (if it has a parent)
                float r = ptr.parent().globalRotation();
                PVector p = ptr.position();
                displacement = new PVector(
                        p.x * game().cos(r) - p.y * game().sin(r),
                        p.x * game().sin(r) + p.y * game().cos(r)
                );
            }
            accumulator.add(displacement);
            if (path.size() <= 0)
                break;
            ptr = path.pop();
        }
        return accumulator;
    }

    /**
     * @return the total rotation relative to the world
     */
    public float globalRotation() {
        float accumulator = rotation();
        for (RelativeTransform ptr = parent(); ptr != null; ptr = ptr.parent()) {
            accumulator += ptr.rotation();
        }
        return accumulator;
    }

    /**
     * @return the total scale relative to the world
     */
    public PVector globalScale() {
        PVector accumulator = scale().copy();
        for (RelativeTransform ptr = parent(); ptr != null; ptr = ptr.parent()) {
            accumulator.x *= ptr.scale().x;
            accumulator.y *= ptr.scale().y;
        }
        return accumulator;
    }

    private boolean isRoot() {
        return parent == null;
    }
}
