package engine.common.event;

import engine.common.component.GameObject;

public class CollisionExitEvent extends Event {

    public GameObject A, B;

    public CollisionExitEvent(GameObject A, GameObject B) {
        this.A = A;
        this.B = B;
    }
}

