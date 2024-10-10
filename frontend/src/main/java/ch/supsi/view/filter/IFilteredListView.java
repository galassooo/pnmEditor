package ch.supsi.view.filter;

public interface IFilteredListView {
    void registerListener(FilterUpdateListener listener);
    void deregisterListener(FilterUpdateListener listener);
}
