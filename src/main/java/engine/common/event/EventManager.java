package engine.common.event;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventManager implements EventEmitter, EventConsumer {

    private Map<Class<? extends Event>, List<Consumer<Event>>> registeredEvents;

    // an idea by "Kevin DiTraglia" on this stack overflow question
    // https://stackoverflow.com/questions/18448671/how-to-avoid-concurrentmodificationexception-while-removing-elements-from-arr
    private Map<Class<? extends Event>, List<Consumer<Event>>> toRemove;

    public EventManager() {
        registeredEvents = new HashMap<>();
        toRemove = new HashMap<>();
    }

    public void on(Class<? extends Event> type, Consumer<Event> consumer) {
        // register an event
        if (!registeredEvents.containsKey(type)) {
            // TODO CopyOnWriteArrayList might be inneficient in comparison to a normal ArrayList, look into it
            registeredEvents.put(type, new CopyOnWriteArrayList<>());
        }
        registeredEvents.get(type).add(consumer);
    }

    public void emit(Event event) {
        cleanup();
        for (Consumer<Event> consumer : registeredEvents.get(event.getClass()))
            consumer.accept(event);
    }

    public void remove(Consumer<Event> event) {
        // add to a list to be removed later, as to avoid ConcurrentModificationExceptions
        registeredEvents.entrySet().stream().filter(entry -> entry.getValue().contains(event)).forEach(entry -> {
            if (!toRemove.containsKey(entry.getKey())) {
                toRemove.put(entry.getKey(), new ArrayList<>());
            }
            toRemove.get(entry.getKey()).add(event);
        });
    }

    private void cleanup() {
        for (Map.Entry<Class<? extends Event>, List<Consumer<Event>>> entry : toRemove.entrySet()) {
            if (!registeredEvents.containsKey(entry.getKey()))
                continue;
            registeredEvents.get(entry.getKey()).removeAll(toRemove.get(entry.getKey()));
        }
    }
}
