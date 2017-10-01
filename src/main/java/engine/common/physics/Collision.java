package engine.common.physics;

import engine.common.component.GameObject;
import processing.core.PVector;

import static engine.common.component.GameManager.game;

public abstract class Collision extends Contact {

    protected float restitution = 0;

    public Collision(PVector normal, float penetration, GameObject a, GameObject b, float restitution) {
        super(normal, penetration, a, b);
        // clip to [0,1]
        this.restitution = game().max(0, game().min(restitution, 1));
    }

    public Collision(PVector normal, float penetration, GameObject a, GameObject b) {
        this(normal, penetration, a, b, 1);
    }
}