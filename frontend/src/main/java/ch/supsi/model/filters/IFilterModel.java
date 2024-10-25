package ch.supsi.model.filters;

import javafx.collections.ObservableList;

import java.util.List;

public interface IFilterModel {
    ObservableList<String> getFilterPipeline();
    ObservableList<String> getLastApplied();
    void addFilterToPipeline(String filter);
    void processFilters();
    void moveFilter(int fromIndex, int toIndex);

    List<String> getAllFilters();

    void removeFilter(int index);
}

