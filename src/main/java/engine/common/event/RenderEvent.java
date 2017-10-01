package engine.common.event;

import engine.common.component.Component;

public class RenderEvent extends Event {

    private String layer;

    public RenderEvent(String layer) {
        this.layer = layer == null ? Component.DEFAULT_RENDER_LAYER : layer;
    }

    public RenderEvent() {
        this(null);
    }

    public String layer() {
        return layer;
    }
}
