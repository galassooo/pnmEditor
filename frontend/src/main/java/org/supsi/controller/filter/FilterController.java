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
 * The FilterController class serves as the controller in the MVC architecture, managing
 * the interaction between the FilterModel and the view (FilterListView).
 * It handles filter actions and updates to synchronize the model and view.
 *<p>
 * Main responsibilities of FilterController include:
 *<p> - Executing filter transformations, such as rotation, mirroring, and inversion.
 *<p> - Observing and reacting to changes in the filter list through the FilterUpdateListener
 *   interface, allowing drag-and-drop or copy-paste operations to update the model.
 *<p> - Registering the view to listen for filter update events, maintaining synchronization
 *   between the UI and the model.
 * <p>
 * This class implements a singleton pattern, ensuring only one instance of the controller
 * is created. Additionally, it follows the observer pattern to notify the model and view
 * of any updates, enhancing modularity and separation of concerns.
 */
public class FilterController {

    /* self reference */
    private static FilterController myself;

    /* instance field*/
    private final IFilterModel model = FilterModel.getInstance();
    private final ILoggerModel loggerModel = LoggerModel.getInstance();
    private final EventSubscriber subscriber = EventManager.getSubscriber();

    /**
     * Returns the singleton instance of FilterController.
     *
     * @return The single instance of FilterController.
     */
    public static FilterController getInstance(){
        if(myself == null){
            myself = new FilterController();
        }
        return myself;
    }
    /* constructor */
    protected FilterController() {
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
     * Handles the addition of a new filter to the filter list at the top position,
     * updating the model's filter pipeline.
     *
     *
     */
    public void onFilterAdded(FilterEvent.FilterAddRequested event) {
        if(ImageModel.getInstance().getImageName() == null){
            loggerModel.addWarning("ui_no_image_added");
        }
        loggerModel.addDebug("ui_filter_added");
        model.addFilterToPipeline(event.filterName());
    }

    /**
     * Moves a filter within the filter list from one index to another, updating
     * the model's filter pipeline accordingly.
     */
    public void onFilterMoved(FilterEvent.FilterMoveRequested event) {
        loggerModel.addDebug("ui_filter_moved");
        model.moveFilter(event.fromIndex(), event.toIndex());
    }

    public void onFiltersActivated(FilterEvent.FilterExecutionRequested event) {
        loggerModel.addInfo("ui_pipeline_processed");
        model.processFilters();
    }

    public void onFilterRemoved(FilterEvent.FilterRemoveRequested event) {
        loggerModel.addDebug("ui_filter_removed");
        model.removeFilter(event.index());
    }
}
