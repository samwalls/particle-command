package engine.common.event;

import processing.core.PApplet;

import java.util.*;
import java.util.function.Consumer;

public class EventManager implements EventEmitter, EventConsumer {

    private Map<Class<? extends Event>, List<Consumer<Event>>> registeredEvents;

    public EventManager() {
        registeredEvents = new HashMap<>();
    }

    public void on(Class<? extends Event> type, Consumer<Event> consumer) {
        // register an event
        if (!registeredEvents.containsKey(type)) {
            registeredEvents.put(type, new ArrayList<>());
        }
        registeredEvents.get(type).add(consumer);
    }

    public void emit(Event event) {
        List<Consumer<Event>> consumers = registeredEvents.get(event.getClass());
        if (consumers == null)
            return;
        for (Consumer<Event> consumer : consumers)
            consumer.accept(event);
    }
}
