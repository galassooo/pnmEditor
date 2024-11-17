package org.supsi.model.filters;

import javafx.collections.ObservableList;

import java.util.List;
import java.util.Map;

/**
 * Defines the behavior of a filter model for managing and applying image filters.
 * Provides methods to manage the filter pipeline and interact with available filters.
 */
public interface IFilterModel {

    /**
     * Retrieves the current pipeline of filters as an observable list.
     *
     * @return an {@link ObservableList} of filter names in the pipeline
     */
    ObservableList<String> getFilterPipeline();

    /**
     * Retrieves the list of the most recently applied filters as an observable list.
     *
     * @return an {@link ObservableList} of recently applied filter names
     */
    ObservableList<String> getLastApplied();

    /**
     * Adds a filter to the pipeline.
     *
     * @param filter the name of the filter to add
     */
    void addFilterToPipeline(String filter);

    /**
     * Processes the filters in the pipeline and applies them to the current image.
     */
    void processFilters();

    /**
     * Moves a filter from one position to another in the pipeline.
     *
     * @param fromIndex the current index of the filter
     * @param toIndex   the new index for the filter
     */
    void moveFilter(int fromIndex, int toIndex);

    /**
     * Retrieves a map of all available filters with their corresponding translated names.
     *
     * @return a {@link Map} where keys are filter identifiers and values are translated filter names
     */
    Map<String, String> getFiltersKeyValues();

    /**
     * Removes a filter from the pipeline.
     *
     * @param index the index of the filter to remove
     */
    void removeFilter(int index);
}
