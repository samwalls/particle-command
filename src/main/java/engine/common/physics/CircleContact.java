package engine.common.physics;

import engine.common.component.GameObject;
import processing.core.PVector;

public class CircleContact extends Contact {

    public CircleContact(PVector normal, float penetration, GameObject A, GameObject B) {
        super(normal, penetration, A, B);
    }

    @Override
    public void resolve() {
        // adapted from "Game Engine Physics Development" - Millington, section 7.2.2
        if (penetration <= 0)
            return;
        float totalInverseMass = A.physics().getInverseMass() + B.physics().getInverseMass();
        if (totalInverseMass <= 0)
            return;
        // TODO perhaps allow for some interpenetration (softness?)
        PVector displacementPerInverseMass = normal.copy().mult(-penetration / totalInverseMass);
        A.physics().applyForce(displacementPerInverseMass.copy().mult(A.physics().getInverseMass()), ForceType.DISPLACEMENT);
        B.physics().applyForce(displacementPerInverseMass.copy().mult(-B.physics().getInverseMass()), ForceType.DISPLACEMENT);
    }
}
