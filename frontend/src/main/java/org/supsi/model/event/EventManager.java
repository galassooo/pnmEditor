package org.supsi.model.event;

import java.util.*;

/**
 * Central event management system implementing a type-safe event bus pattern.
 * This class serves as the core message broker for the application, enabling
 * loose coupling between components through event-based communication.
 *
 * <p>The EventManager follows these key design principles:
 * <ul>
 *   <li>Singleton pattern for centralized event management</li>
 *   <li>Type-safe event handling using generics</li>
 *   <li>Support for multiple handlers per event type</li>
 * </ul>
 *
 * <p>Implementation Details:
 * <ul>
 *   <li>Supports dynamic registration and deregistration of handlers</li>
 *   <li>Provides separate interfaces for publishing and subscribing</li>
 *   <li>Implements error handling for event processing</li>
 * </ul>
 *
 * <p>Usage Example:
 * <pre>{@code
 * // Getting the event manager instances
 * EventPublisher publisher = EventManager.getPublisher();
 * EventSubscriber subscriber = EventManager.getSubscriber();
 *
 * // Subscribing to events
 * subscriber.subscribe(MyEvent.class, event -> {
 *     // Handle the event
 * });
 *
 * // Publishing events
 * publisher.publish(new MyEvent());
 * }</pre>
 *
 * <p>Event Processing Flow:
 * <ol>
 *   <li>Event is published through the publisher interface</li>
 *   <li>EventManager identifies all handlers registered for the event type</li>
 *   <li>Each handler is executed in the order of registration</li>
 *   <li>Exceptions in handlers are caught and logged without affecting other handlers</li>
 * </ol>
 *
 * @see EventPublisher
 * @see EventSubscriber
 * @see EventHandler
 */
public class EventManager implements EventPublisher, EventSubscriber {

    @SuppressWarnings("all") //not final -> reset with reflection for test
    private static EventManager instance = new EventManager();
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
        handlers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void unsubscribe(Class<T> eventType, EventHandler<T> handler) {
        var eventHandlers = handlers.get(eventType);
        eventHandlers.remove(handler); //impossible null on eventHandlers
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