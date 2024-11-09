package org.supsi.view.filter;

public interface IFilterEvent {
    void registerListener(FilterUpdateListener listener);
    void deregisterListener(FilterUpdateListener listener);
}
