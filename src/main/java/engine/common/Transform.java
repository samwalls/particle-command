package engine.common;

import processing.core.PVector;

public class Transform {

    public PVector position = new PVector();

    public PVector scale = new PVector(1, 1);

    // rotation around Z axis (radians)
    public float rotation = 0f;
}