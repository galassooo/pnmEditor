package ch.supsi.application.state;

import ch.supsi.business.state.BusinessEditorState;
import ch.supsi.business.state.EditorState;
import ch.supsi.business.state.StateChangeEvent;

/**
 * Provides an application-level implementation of editor state management.
 * This class handles state queries and manages state change listeners by delegating
 * operations to the business logic.
 * <p>
 * Implements the Singleton pattern to ensure a single instance is used throughout the application.
 */
public class StateApplication implements EditorState, StateChangeEvent {

    private static StateApplication myself;

    private final EditorStateManager businessEditorState;

    /**
     * Private constructor to enforce Singleton pattern.
     * Initializes the business layer's editor state manager.
     */
    private StateApplication() {
        businessEditorState = BusinessEditorState.getInstance();
    }

    /**
     * Retrieves the Singleton instance of the {@link StateApplication}.
     *
     * @return the Singleton instance of this class
     */
    public static StateApplication getInstance() {
        if (myself == null) {
            myself = new StateApplication();
        }
        return myself;
    }

    /**
     * Checks if filters can be applied in the current state.
     *
     * @return {@code true} if filters can be applied, {@code false} otherwise
     */
    @Override
    public boolean canApplyFilters() {
        return businessEditorState.canApplyFilters();
    }

    /**
     * Checks if the current state allows saving the file.
     *
     * @return {@code true} if saving is allowed, {@code false} otherwise
     */
    @Override
    public boolean canSave() {
        return businessEditorState.canSave();
    }

    /**
     * Checks if the current state allows saving the file under a new name.
     *
     * @return {@code true} if "Save As" is allowed, {@code false} otherwise
     */
    @Override
    public boolean canSaveAs() {
        return businessEditorState.canSaveAs();
    }

    /**
     * Checks if a new filter can be added in the current state.
     *
     * @return {@code true} if adding a filter is allowed, {@code false} otherwise
     */
    @Override
    public boolean canAddFilter() {
        return businessEditorState.canAddFilter();
    }

    /**
     * Checks if the current state allows exporting the file.
     *
     * @return {@code true} if exporting is allowed, {@code false} otherwise
     */
    @Override
    public boolean canExport() {
        return businessEditorState.canExport();
    }

    /**
     * Checks if a refresh operation is required in the current state.
     *
     * @return {@code true} if a refresh is required, {@code false} otherwise
     */
    @Override
    public boolean isRefreshRequired() {
        return businessEditorState.isRefreshRequired();
    }

    /**
     * Checks if there are pending changes in the current state that need to be saved or processed.
     *
     * @return {@code true} if there are pending changes, {@code false} otherwise
     */
    @Override
    public boolean areChangesPending() {
        return businessEditorState.areChangesPending();
    }

    /**
     * Registers a listener to be notified of state changes.
     *
     * @param listener the listener to register
     */
    @Override
    public void registerStateListener(StateChangeListener listener) {
        ((StateChangeEvent) businessEditorState).registerStateListener(listener);
    }

    /**
     * Deregisters a previously registered state change listener.
     *
     * @param listener the listener to deregister
     */
    @Override
    public void deregisterStateListener(StateChangeListener listener) {
        ((StateChangeEvent) businessEditorState).deregisterStateListener(listener);
    }
}
