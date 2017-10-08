package game.objects;

import engine.common.component.GameObject;
import game.RenderLayer;

import static game.MainComponent.game;

public class Pointer extends GameObject {

    private static final float CROSSHAIR_RADIUS = 20f;
    private static final float CROSSHAIR_THICKNESS = 5f;

    @Override
    public String renderLayer() {
        return RenderLayer.MOUSE.toString();
    }

    @Override
    public void onRender() {
        game().pushStyle();
        float x = game().mouseX;
        float y = game().mouseY;
        game().stroke(255, 255, 255, 127);
        game().strokeWeight(CROSSHAIR_THICKNESS);
        game().line(x - CROSSHAIR_RADIUS, y, x + CROSSHAIR_RADIUS, y);
        game().line(x, y - CROSSHAIR_RADIUS, x, y + CROSSHAIR_RADIUS);
        game().popStyle();
    }
}
