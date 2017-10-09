package engine.common.event;

/**
 * Types that can emit events must adhere to this interface.
 */
public interface EventEmitter {

    void emit(Event event);
}
