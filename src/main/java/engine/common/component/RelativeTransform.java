package engine.common.component;

import processing.core.PVector;

import java.util.*;

import static engine.common.component.GameManager.game;

public abstract class RelativeTransform implements Transformable {

    private RelativeTransform parent, root;

    private Set<RelativeTransform> children;

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

    public void setParent(RelativeTransform parent) throws RelativeTransformCycleException {
        for (RelativeTransform ptr = this; ptr != null; ptr = ptr.parent()) {
            if (parent == ptr)
                throw new IllegalArgumentException("attempt to set parent on relative transform would cause cycle");
        }
        this.parent = parent;
        for (RelativeTransform ptr = this; ptr.parent() != null; ptr = ptr.parent())
            if (ptr.parent() == null)
                this.root = ptr;
    }

    public void addChild(RelativeTransform child) throws RelativeTransformCycleException {
        try {
            child.setParent(this);
        } catch (RelativeTransformCycleException e) {
            throw new RelativeTransformCycleException("attempt to set parent in child to-be-added would cause cycle", e);
        }
        children.add(child);
    }

    public void removeChild(RelativeTransform child) {
        if (children.contains(child)) {
            children.remove(child);
            child.parent = null;
            child.root = null;
        }
    }

    /**
     * @return the position relative to the parent (if any, otherwise, should be the same as position())
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

    private boolean isRoot() {
        return parent == null;
    }
}
