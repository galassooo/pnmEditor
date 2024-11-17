package org.supsi.model.event;

import java.util.*;

/**
 * Manages the subscription and publication of events in the application.
 * Acts as a singleton, implementing both {@link EventPublisher} and {@link EventSubscriber}.
 * Allows components to subscribe to specific event types and publish events to notify subscribers.
 */
public class EventManager implements EventPublisher, EventSubscriber {

    private static final EventManager instance = new EventManager();
    private final Map<Class<?>, List<EventHandler<?>>> handlers;

    /**
     * Constructs a new {@code EventManager} instance.
     * Initializes the internal map for storing event handlers.
     */
    protected EventManager() {
        handlers = new HashMap<>();
    }

    /**
     * Retrieves the instance of {@link EventPublisher} for publishing events.
     * This ensures the caller only has access to the methods related to publishing.
     *
     * @return the {@link EventPublisher} instance
     */
    public static EventPublisher getPublisher() {
        return instance; //cast di se stesso -> espone solo i metodi di un publisher
        // ma gestisce tutto
    }

    /**
     * Retrieves the instance of {@link EventSubscriber} for subscribing to events.
     * This ensures the caller only has access to the methods related to subscribing.
     *
     * @return the {@link EventSubscriber} instance
     */
    public static EventSubscriber getSubscriber() {
        return instance; //cast di se stesso -> espone solo i metodi di un subscriber
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void unsubscribe(Class<T> eventType, EventHandler<T> handler) {
        var eventHandlers = handlers.get(eventType);
        if (eventHandlers != null) {
            eventHandlers.remove(handler);
        }
    }
    /**
     * {@inheritDoc}
     */
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