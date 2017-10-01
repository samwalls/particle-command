package engine.common.component;

public interface Drawable {

    /**
     * @return the label of the render layer which this object resides in.
     */
    String renderLayer();

    /**
     * Specifies to the game how to render this object, when necessary.
     */
    void onRender();
}
