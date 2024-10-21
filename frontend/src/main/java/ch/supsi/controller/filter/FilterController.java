package ch.supsi.controller.filter;

import ch.supsi.model.filters.FilterModel;
import ch.supsi.model.filters.IFilterModel;
import ch.supsi.view.filter.FilterUpdateListener;
import ch.supsi.view.filter.IFilteredListView;

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
public class FilterController implements IFilterController, FilterUpdateListener {

    /* self reference */
    private static FilterController myself;

    /* instance field*/
    private final IFilterModel model = FilterModel.getInstance();

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
     * Rotates the current filter list left by 90 degrees.
     */
    @Override
    public void addRotationLeft() {
        model.addRotationLeft();
    }

    /**
     * Rotates the current filter list right by 90 degrees.
     */
    @Override
    public void addRotationRight() {
        model.addRotationRight();
    }

    /**
     * Applies a mirror transformation to the filter list.
     */
    @Override
    public void mirror() {
        model.mirror();
    }

    /**
     * Applies a negative transformation to the filter list.
     */
    @Override
    public void negative() {
        model.negative();
    }

    /**
     * Registers the given view as a listener for filter update events, enabling it
     * to receive notifications on changes in the filter list.
     *
     * @param view The IFilteredListView instance to register as an event publisher.
     */
    @Override
    public void addEventPublisher(IFilteredListView view) {
        view.registerListener(this);
    }

    /**
     * Handles the addition of a new filter to the filter list at the top position,
     * updating the model's filter pipeline.
     *
     * @param filter The filter to add to the list.
     */
    @Override
    public void onFilterAdded(String filter) {
        model.getFilterPipeline().add(0, filter);
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
        if (fromIndex != toIndex) {
            var filterPipeline = model.getFilterPipeline();
            var item = filterPipeline.remove(fromIndex);
            filterPipeline.add(toIndex, item);
        }
    }
}
