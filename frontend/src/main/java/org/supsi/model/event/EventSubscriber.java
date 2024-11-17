package org.supsi.model.event;

public interface EventSubscriber {
    <T> void subscribe(Class<T> eventType, EventHandler<T> handler);
    <T> void unsubscribe(Class<T> eventType, EventHandler<T> handler);
}
