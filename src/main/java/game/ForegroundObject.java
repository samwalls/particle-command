package game;

import engine.common.component.GameObject;
import processing.core.PVector;

import static engine.common.component.GameManager.game;

public class ForegroundObject extends GameObject {

    @Override
    public String renderLayer() {
        return "foreground";
    }

    @Override
    public void onRender() {
        game().pushMatrix();
        game().stroke(255, 255, 0);
        PVector p = position();
        game().line(0, 0, p.x, p.y);
        game().popMatrix();
    }
}
