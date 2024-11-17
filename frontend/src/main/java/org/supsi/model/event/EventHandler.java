package org.supsi.model.event;

@FunctionalInterface
public interface EventHandler<T> {
    void handle(T event);
}