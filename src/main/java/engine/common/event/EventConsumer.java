package engine.common.event;

import java.util.function.Consumer;

/**
 * Types that can handle events must adhere to this interface.
 */
public interface EventConsumer {

    void on(Class<? extends Event> type, Consumer<Event> consumer);
}
