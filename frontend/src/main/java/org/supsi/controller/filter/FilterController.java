package org.supsi.controller.filter;

import org.supsi.model.filters.FilterModel;
import org.supsi.model.filters.IFilterModel;
import org.supsi.model.image.ImageModel;
import org.supsi.model.info.ILoggerModel;
import org.supsi.model.info.LoggerModel;
import org.supsi.view.filter.FilterUpdateListener;
import org.supsi.view.filter.IFilterEvent;

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
public class FilterController implements  FilterUpdateListener {

    /* self reference */
    private static FilterController myself;

    /* instance field*/
    private final IFilterModel model = FilterModel.getInstance();
    private final ILoggerModel loggerModel = LoggerModel.getInstance();

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
    protected FilterController() {}

    /**
     * Handles the addition of a new filter to the filter list at the top position,
     * updating the model's filter pipeline.
     *
     * @param filter The filter to add to the list.
     */
    @Override
    public void onFilterAdded(String filter) {
        if(ImageModel.getInstance().getImageName() == null){
            loggerModel.addWarning("ui_no_image_added");
        }
        loggerModel.addDebug("ui_filter_added");
        model.addFilterToPipeline(filter);
    }

    /**
     * Moves a filter within the filter list from one index to another, updating
     * the model's filter pipeline accordingly.
     *
     * @param fromIndex The current index of the filter to move.
     * @param toIndex   The target index to move the filter to.
     */
    @Override
    public void onFilterMoved(int fromIndex, int toIndex) {
        loggerModel.addDebug("ui_filter_moved");
        model.moveFilter(fromIndex, toIndex);
    }

    @Override
    public void onFiltersActivated() {
        loggerModel.addInfo("ui_pipeline_processed");
        model.processFilters();
    }

    /**
     * Registers the given view as a listener for filter update events, enabling it
     * to receive notifications on changes in the filter list.
     *
     * @param view The IFilteredListView instance to register as an event publisher.
     */
    @Override
    public void addEventPublisher(IFilterEvent view) {
        view.registerListener(this);
    }

    @Override
    public void onFilterRemoved(int index) {
        loggerModel.addDebug("ui_filter_removed");
        model.removeFilter(index);
    }
}
