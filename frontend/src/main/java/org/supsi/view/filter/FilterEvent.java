package org.supsi.view.filter;

/**
 * Represents a sealed interface for filter-related events in the application.
 * Defines the possible events that can occur when managing filters, such as adding, removing, moving, or executing filters.
 */
public sealed interface FilterEvent {
    record FilterAddRequested(String filterName) implements FilterEvent {}
    record FilterRemoveRequested(int index) implements FilterEvent {}
    record FilterMoveRequested(int fromIndex, int toIndex) implements FilterEvent {}
    record FilterExecutionRequested() implements FilterEvent {}
}

