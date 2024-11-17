package org.supsi.model.event;

/**
 * Represents a generic interface for subscribing to and unsubscribing from events.
 * Allows registering and deregistering event handlers for specific event types.
 */
public interface EventSubscriber {

    /**
     * Subscribes to a specific type of event with the provided handler.
     *
     * @param eventType the class of the event type to subscribe to
     * @param handler   the handler to execute when the event occurs
     * @param <T>       the type of the event
     */
    <T> void subscribe(Class<T> eventType, EventHandler<T> handler);

    /**
     * Unsubscribes from a specific type of event for the provided handler.
     *
     * @param eventType the class of the event type to unsubscribe from
     * @param handler   the handler to remove from the subscription list
     * @param <T>       the type of the event
     */
    <T> void unsubscribe(Class<T> eventType, EventHandler<T> handler);
}
