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

    // static helpers to create circles and boxes of the correct size given certain properties

    public static AABB circle(float radius) {
        AABB result = new AABB();
        result.updateSize(radius);
        return result;
    }

    public static AABB box(float width, float height) {
        AABB result = new AABB();
        result.updateSize(width, height);
        return result;
    }

    /**
     * Adjust the size of the bounding box to suit a single radius
     * @param radius the radius of the AABB
     */
    public void updateSize(float radius) {
        float side = radius * game().sin(game().QUARTER_PI);
        min.x = -side;
        min.y = -side;
        max.x = side;
        max.y = side;
    }

    /**
     * Adjust the size of the bounding box to suit a width and height
     * @param width the width of the AABB
     * @param height the height of the AABB
     */
    public void updateSize(float width, float height) {
        min.x = -width / 2f;
        min.y = -height / 2f;
        max.x = width / 2f;
        max.y = height / 2f;
    }

    /**
     * @return the width of the bounding box
     */
    public float width() {
        return max.x - min.x;
    }

    /**
     * @return the height of the bounding box
     */
    public float height() {
        return max.y - min.y;
    }

    /**
     * @return the inner radius, defined as the maximum circle that fits within the bounding box
     */
    public float innerRadius() {
        return game().max(width(), height()) / 2f;
    }

    /**
     * @return the outer radius, defined as the minimum circle that encompasses the entire bounding box
     */
    public float outerRadius() {
        return (game().sqrt(width() * width() + height() * height())) / 2f;
    }
}
