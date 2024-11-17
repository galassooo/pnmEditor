package ch.supsi.application.state;

/**
 * Interface for listening to state changes in the editor.
 * Implements the Observer pattern to notify components when the editor's state changes.
 */
public interface StateChangeListener {
    /**
     * Called when the editor's state changes.
     * Implementing classes should handle the state change appropriately.
     */
    void onStateChange();
}