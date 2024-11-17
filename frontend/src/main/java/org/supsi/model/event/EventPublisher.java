package org.supsi.model.event;

public interface EventPublisher {
    <T> void publish(T event);
}
