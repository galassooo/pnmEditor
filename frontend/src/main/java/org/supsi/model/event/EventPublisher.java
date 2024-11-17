package org.supsi.model.event;

/**
 * Represents a generic interface for publishing events.
 * Allows notifying subscribers or listeners about specific events.
 */
public interface EventPublisher {

    /**
     * Publishes the specified event to all registered handlers or listeners.
     *
     * @param event the event to be published
     * @param <T>   the type of the event
     */
    <T> void publish(T event);
}
