package ch.supsi.business.state;

/**
 * Interface defining the possible states and operations of the image editor.
 * Provides methods to query the current state and determine which operations
 * are allowed in that state.
 */
public interface EditorState {
    /**
     * Checks if filters can be applied to the current image.
     *
     * @return true if filters can be applied, false otherwise
     */
    boolean canApplyFilters();

    /**
     * Checks if the current image can be saved.
     *
     * @return true if the image can be saved, false otherwise
     */
    boolean canSave();

    /**
     * Checks if the current image can be saved with a new name/path.
     *
     * @return true if the image can be saved as, false otherwise
     */
    boolean canSaveAs();

    /**
     * Checks if a new filter can be added to the filter pipeline.
     *
     * @return true if a filter can be added, false otherwise
     */
    boolean canAddFilter();

    /**
     * Checks if the current image can be exported to a different format.
     *
     * @return true if the image can be exported, false otherwise
     */
    boolean canExport();

    /**
     * Checks if the UI needs to be refreshed to reflect recent changes.
     *
     * @return true if a refresh is required, false otherwise
     */
    boolean isRefreshRequired();

    /**
     * Checks if there are unsaved changes to the current image.
     *
     * @return true if there are pending changes, false otherwise
     */
    boolean areChangesPending();
}