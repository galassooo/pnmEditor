package ch.supsi.model.filters;

import javafx.collections.ObservableList;

public interface IFilterModel {
    ObservableList<String> getFilterPipeline();
    void addFilterToPipeline(String filter);
    void processFilters();
}

