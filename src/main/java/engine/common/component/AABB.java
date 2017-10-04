package engine.common.component;

import processing.core.PVector;

import static engine.common.component.GameManager.game;

/**
 * Simple Component type for a 2D axis-aligned bounding box.
 */
public class AABB extends Component {

    public PVector min = new PVector();
    public PVector max = new PVector();

    public AABB(Component parent) throws RelativeTransformCycleException {
        super(parent);
    }

    public AABB() {
        super(null);
    }

    public static AABB circle(float radius) {
        float side = radius * game().sin(game().QUARTER_PI);
        AABB result = new AABB();
        result.min.x = -side;
        result.min.y = -side;
        result.max.x = side;
        result.max.y = side;
        return result;
    }

    public float width() {
        return max.x - min.x;
    }

    public float height() {
        return max.y - min.y;
    }

    public float innerRadius() {
        return game().max(width(), height()) / 2f;
    }

    public float outerRadius() {
        return (game().sqrt(width() * width() + height() * height())) / 2f;
    }
}
