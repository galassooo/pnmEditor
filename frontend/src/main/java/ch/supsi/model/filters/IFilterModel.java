package ch.supsi.model.filters;

import javafx.collections.ObservableList;

public interface IFilterModel {
    void addRotationLeft();

    ObservableList<String> getFilterPipeline();

    void mirror();

    void addRotationRight();

    void negative();
}

