package org.supsi.model.filters;

import javafx.collections.ObservableList;

import java.util.List;
import java.util.Map;

public interface IFilterModel {
    ObservableList<String> getFilterPipeline();
    ObservableList<String> getLastApplied();
    void addFilterToPipeline(String filter);
    void processFilters();
    void moveFilter(int fromIndex, int toIndex);

    Map<String, String> getFiltersKeyValues();

    void removeFilter(int index);
}

