package org.supsi.model.event;

import java.util.*;

public class EventManager implements EventPublisher, EventSubscriber {

    private static final EventManager instance = new EventManager();
    private final Map<Class<?>, List<EventHandler<?>>> handlers;

    protected EventManager() {
        handlers = new HashMap<>();
    }

    public static EventPublisher getPublisher() {
        return instance; //cast di se stesso -> espone solo i metodi di un publisher
        // ma gestisce tutto
    }

    public static EventSubscriber getSubscriber() {
        return instance; //cast di se stesso -> espone solo i metodi di un subscriber
    }

    @Override
    public <T> void subscribe(Class<T> eventType, EventHandler<T> handler) {
        System.out.println("Subscribing handler for: " + eventType.getSimpleName());
        handlers.computeIfAbsent(eventType, k -> {
            var tmp = new ArrayList<EventHandler<?>>();
            tmp.add(handler);
            return tmp;
        });
        System.out.println("Current handlers for " + eventType.getSimpleName() + ": "
                + handlers.get(eventType).size());
    }


    @Override
    public <T> void unsubscribe(Class<T> eventType, EventHandler<T> handler) {
        var eventHandlers = handlers.get(eventType);
        if (eventHandlers != null) {
            eventHandlers.remove(handler);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void publish(T event) {
        System.out.println("Publishing event: " + event.getClass().getSimpleName());
        List<EventHandler<?>> eventHandlers = handlers.getOrDefault(event.getClass(),
                Collections.emptyList());
        System.out.println("Found " + eventHandlers.size() + " handlers");
        eventHandlers.forEach(handler -> {
            try {
                System.out.println("Handling event with: " + handler.getClass().getSimpleName());
                ((EventHandler<T>) handler).handle(event);
            } catch (Exception e) {
                System.err.println("Error handling view event: " + e.getMessage());
            }
        });
    }
}