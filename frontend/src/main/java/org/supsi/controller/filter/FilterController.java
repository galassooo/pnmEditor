package org.supsi.controller.filter;

import org.supsi.view.filter.FilterEvent;
import org.supsi.model.event.EventManager;
import org.supsi.model.event.EventSubscriber;
import org.supsi.model.filters.FilterModel;
import org.supsi.model.filters.IFilterModel;
import org.supsi.model.image.ImageModel;
import org.supsi.model.info.ILoggerModel;
import org.supsi.model.info.LoggerModel;

/**
 * Controller class responsible for managing filters and handling filter-related events.
 */
public class FilterController {

    private static FilterController myself;
    private final IFilterModel model;
    private final ILoggerModel loggerModel;

    /**
     * Constructs the controller and subscribes to filter-related events.
     */
    protected FilterController() {
        model = FilterModel.getInstance();
        loggerModel  = LoggerModel.getInstance();

        EventSubscriber subscriber = EventManager.getSubscriber();
        subscriber.subscribe(FilterEvent.FilterAddRequested.class,
                this::onFilterAdded);
        subscriber.subscribe(FilterEvent.FilterMoveRequested.class,
                this::onFilterMoved);
        subscriber.subscribe(FilterEvent.FilterExecutionRequested.class,
                this::onFiltersActivated);
        subscriber.subscribe(FilterEvent.FilterRemoveRequested.class,
                this::onFilterRemoved);
    }

    /**
     * Returns the singleton instance of this controller.
     *
     * @return Singleton instance of {@link FilterController}.
     */
    public static FilterController getInstance(){
        if(myself == null){
            myself = new FilterController();
        }
        return myself;
    }

    /**
     * Handles the event for adding a new filter to the top of the filter list.
     *
     * @param event Event containing the filter to be added.
     */
    public void onFilterAdded(FilterEvent.FilterAddRequested event) {
        if(ImageModel.getInstance().getImageName() == null){
            loggerModel.addWarning("ui_no_image_added");
        }
        loggerModel.addDebug("ui_filter_added");
        model.addFilterToPipeline(event.filterName());
    }

    /**
     * Handles the event for moving a filter in the filter list.
     *
     * @param event Event containing the source and destination indexes.
     */
    public void onFilterMoved(FilterEvent.FilterMoveRequested event) {
        loggerModel.addDebug("ui_filter_moved");
        model.moveFilter(event.fromIndex(), event.toIndex());
    }

    /**
     * Handles the event for executing all filters in the pipeline.
     *
     * @param event Event triggering the execution of filters.
     */
    public void onFiltersActivated(FilterEvent.FilterExecutionRequested event) {
        loggerModel.addInfo("ui_pipeline_processed");
        model.processFilters();
    }

    /**
     * Handles the event for removing a filter from the filter list.
     *
     * @param event Event containing the index of the filter to be removed.
     */
    public void onFilterRemoved(FilterEvent.FilterRemoveRequested event) {
        loggerModel.addDebug("ui_filter_removed");
        model.removeFilter(event.index());
    }
}
