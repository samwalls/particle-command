package game.objects;

import engine.common.component.GameObject;
import processing.core.PVector;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static game.MainComponent.game;

/**
 * Simple object which renders twinkling stars within the bounds of the screen.
 */
public class StarField extends GameObject {

    private static final int DEFAULT_AMOUNT = 300;

    private static final int BRIGHTNESS_MIN = 50;
    private static final int PHASE_PERIOD = (int)(game().frameRate * 30f);

    private Map<PVector, Integer> stars;

    public StarField() {
        stars = new HashMap<>();
        for (int i = 0; i < DEFAULT_AMOUNT; i++) {
            PVector p = new PVector(
                    game().random(0, game().width),
                    game().random(0, game().height)
            );
            stars.put(p, new Random().nextInt(PHASE_PERIOD));
        }
    }

    @Override
    public void onRender() {
        game().pushMatrix();
        game().pushStyle();
        for (Map.Entry<PVector, Integer> star : stars.entrySet()) {
            game().stroke(0, 0);
            game().fill(230, 230, 255, alpha(star.getValue()));
            PVector p = star.getKey();
            game().ellipse(p.x, p.y, 3f, 3f);
            int phase = star.getValue();
            if (phase >= PHASE_PERIOD)
                stars.put(p, 0);
            else
                stars.put(p, phase + 1);
        }
        game().popStyle();
        game().popMatrix();
    }

    private int alpha(int v) {
        return (int)((((float)(255 - BRIGHTNESS_MIN)) / 2f) * game().sin(game().TWO_PI * ((float)v) / ((float)PHASE_PERIOD))) + BRIGHTNESS_MIN;
    }
}
