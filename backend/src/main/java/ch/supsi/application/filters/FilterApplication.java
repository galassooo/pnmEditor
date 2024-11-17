package ch.supsi.application.filters;

import ch.supsi.application.image.WritableImage;
import ch.supsi.application.state.EditorStateManager;
import ch.supsi.business.filter.FilterFactory;
import ch.supsi.business.filter.FilterManager;
import ch.supsi.business.filter.chain.command.FilterCommand;
import ch.supsi.business.state.BusinessEditorState;

import java.util.List;

/**
 * The {@link FilterApplication} class provides functionalities for managing and applying filters
 * to images. It serves as an interface for interacting with the filter pipeline, allowing filters
 * to be added, removed, and processed, while updating the application state.
 */
public class FilterApplication {

    private static FilterApplication myself;
    private final List<String> allFilters;
    private final FilterPipelineInterface model;
    private final EditorStateManager stateManager;

    /**
     * Private constructor to enforce the singleton pattern.
     */
    private FilterApplication() {
        allFilters = FilterFactory.getFilters();
        model = FilterManager.getInstance();
        stateManager = BusinessEditorState.getInstance();
    }

    /**
     * Returns the singleton instance of the {@code FilterApplication}.
     *
     * @return the singleton instance of the {@code FilterApplication}.
     */
    public static FilterApplication getInstance() {
        if (myself == null) {
            myself = new FilterApplication();
        }
        return myself;
    }

    /**
     * Retrieves a list of all available filters.
     *
     * @return a read-only {@link List} of all available filter names.
     */
    public List<String> getAllAvailableFilters() {
        return List.copyOf(allFilters);
    }

    /**
     * Adds a filter to the filter pipeline by its name.
     *
     * @param filterName the name of the filter to add.
     * @throws IllegalArgumentException if the filterName does not correspond to a valid filter.
     */
    public void addFilterToPipeline(String filterName) {
        FilterCommand filter = FilterFactory.getFilter(filterName);
        model.addFilter(filter);
        stateManager.onFilterAdded();
    }

    /**
     * Processes the filter pipeline on the given image.
     *
     * @param image the image to apply the filters on.
     * @throws IllegalStateException if the filter pipeline is empty or cannot be processed.
     */
    public void processFilterPipeline(WritableImage image) {
        model.executePipeline(image);
        stateManager.onFilterProcessed();
    }

    /**
     * Adds a filter to a specific index in the filter pipeline.
     *
     * @param filterName the name of the filter to add.
     * @param index      the position in the pipeline where the filter should be added.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public void add(String filterName, int index) {
        FilterCommand filter = FilterFactory.getFilter(filterName);
        model.addFilter(filter, index);
        stateManager.onFilterAdded();
    }

    /**
     * Removes a filter from the filter pipeline at a specific index.
     *
     * @param index the position of the filter to remove.
     * @return the name of the removed filter.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public String remove(int index) {
        if (model.getSize() == 1) {
            stateManager.onFiltersRemoved();
        }
        return model.remove(index); //not nullable -> se errore lancia ex
    }
}
