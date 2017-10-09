package game;

import engine.common.component.GameManager;
import game.objects.*;
import processing.core.PApplet;

public class MainComponent extends GameManager {

    public static final int FRAME_RATE = 60;

    public static void main(String[] args) {
        PApplet.main(MainComponent.class.getName());
    }

    private Game game;

    public void settings() {
//        fullScreen();
//        size(1920, 1080);
        size(1200, 1080);
    }

    public void setup() {
        noCursor();
        frameRate(FRAME_RATE);
        game = new Game();
    }

    public void mouseReleased() {
        game.onClick();
    }

    public void keyPressed() {
        game.onKeyPress();
    }

    public void draw() {
        // clear
        background(0, 0, 0);
        // update, render
        updateAll();
    }
}
