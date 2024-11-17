package ch.supsi.application.state;

import ch.supsi.business.state.EditorState;

/**
 * Manages the state of the editor, providing methods to handle transitions between different states.
 * This interface extends {@link EditorState}, allowing for additional state management behaviors
 * to be implemented by the application.
 */
public interface EditorStateManager extends EditorState {

    /**
     * Updates the state of the editor to reflect that an image has been successfully loaded.
     */
    void onImageLoaded();

    /**
     * Updates the state of the editor to reflect that a filter has been added.
     */
    void onFilterAdded();

    /**
     * Updates the state of the editor to reflect that a filter has been processed.
     */
    void onFilterProcessed();

    /**
     * Updates the state of the editor to reflect that all filters have been removed.
     */
    void onFiltersRemoved();

    /**
     * Updates the state of the editor to reflect that a loading operation is in progress.
     */
    void onLoading();

    /**
     * Updates the state of the editor to reflect that an error occurred during a loading operation.
     */
    void onLoadingError();
}
