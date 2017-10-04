package engine.common.physics;

import engine.common.component.GameObject;
import processing.core.PVector;

public class BoxContact extends Contact {

    public BoxContact(PVector normal, float penetration, GameObject A, GameObject B) {
        super(normal, penetration, A, B);
    }

    @Override
    public void resolve() {
        // TODO implement this
    }
}
