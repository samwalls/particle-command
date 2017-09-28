package engine.common.event;

import java.util.function.Consumer;

public interface EventConsumer {

    void on(Class<? extends Event> type, Consumer<Event> consumer);
}
