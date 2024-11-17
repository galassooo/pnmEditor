package org.supsi.view.filter;

public sealed interface FilterEvent {
    record FilterAddRequested(String filterName) implements FilterEvent {}
    record FilterRemoveRequested(int index) implements FilterEvent {}
    record FilterMoveRequested(int fromIndex, int toIndex) implements FilterEvent {}
    record FilterExecutionRequested() implements FilterEvent {}
}

