package engine.common.component;

public class RelativeTransformCycleException extends IllegalArgumentException {

    public RelativeTransformCycleException(String message) {
        super(message);
    }

    public RelativeTransformCycleException(String message, Exception cause) {
        super(message, cause);
    }
}
