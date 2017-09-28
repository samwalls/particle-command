package engine.common.event;

import java.util.function.Consumer;

public interface IEventManager {

    void emit(Event event);
}
