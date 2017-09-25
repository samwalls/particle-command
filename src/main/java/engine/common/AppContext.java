package engine.common;

import processing.core.PApplet;

/**
 * Singleton to contain the applet context for the game being implemented.
 */
public class AppContext {

    public static PApplet app;

    private AppContext() {
    }

    public static PApplet app() {
        return app;
    }

    public static void setContext(PApplet context) {
        AppContext.app = context;
    }
}
