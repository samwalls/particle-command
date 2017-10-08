package game;

import engine.common.component.Component;

/**
 * An enum which defines the rendering layer order.
 */
public enum RenderLayer {
    BACKGROUND("background"),
    DEFAULT(Component.DEFAULT_RENDER_LAYER),
    FLOOR("floor"),
    PROJECTILE("projectile"),
    MOUSE("mouse"),
    ;

    private final String layerName;

    RenderLayer(String layerName) {
        this.layerName = layerName;
    }

    @Override
    public String toString() {
        return layerName;
    }
}
