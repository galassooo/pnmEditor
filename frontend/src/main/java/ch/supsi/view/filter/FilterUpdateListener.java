package ch.supsi.view.filter;

public interface FilterUpdateListener {

    void onFilterAdded(String filter);

    void onFilterMoved(int fromIndex, int toIndex);
}
