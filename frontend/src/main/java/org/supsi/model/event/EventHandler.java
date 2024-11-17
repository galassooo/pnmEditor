package org.supsi.model.event;

/**
 * Represents a functional interface for handling events.
 * Allows defining custom event handling logic for a specific type of event.
 *
 * @param <T> the type of event to be handled
 */
@FunctionalInterface
public interface EventHandler<T> {

    /**
     * Handles the specified event.
     *
     * @param event the event to handle
     */
    void handle(T event);
}