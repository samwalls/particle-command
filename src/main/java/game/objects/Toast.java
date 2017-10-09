package game.objects;

import engine.common.component.GameObject;
import game.RenderLayer;
import processing.core.PVector;

import static game.MainComponent.game;

/**
 * Toasts pop up some text for a fixed amount of time, and then disappear.
 */
public class Toast extends GameObject {

    private String text;
    private int duration;
    private PVector position;
    private int textSize;
    private int textAlignment;
    private int delta;

    /**
     * @param text the text on the toast
     * @param duration the duration in frames for the toast
     */
    public Toast(String text, int duration, PVector position, int textSize, int textAlignment) {
        this.text = text;
        this.duration = duration;
        this.position = position;
        this.textSize = textSize;
        this.textAlignment = textAlignment;
    }

    @Override
    public String renderLayer() {
        return RenderLayer.TEXT.toString();
    }

    @Override
    public void onRender() {
        // if the toast has extended it's duration, destroy it
        if (delta++ >= duration) {
            destroy();
        }
        game().pushMatrix();
        game().pushStyle();
        game().fill(255, 255, 255);
        game().stroke(0, 0);
        game().textSize(textSize);
        game().textAlign(textAlignment);
        game().text(text, position.x, position.y);
        game().popStyle();
        game().popMatrix();
    }
}
