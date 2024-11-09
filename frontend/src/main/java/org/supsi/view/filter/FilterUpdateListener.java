package org.supsi.view.filter;

public interface FilterUpdateListener {


    void onFilterAdded(String filter);

    void onFilterMoved(int fromIndex, int toIndex);

    void onFiltersActivated();

    void addEventPublisher(IFilterEvent view);

    void onFilterRemoved(int index);
}
