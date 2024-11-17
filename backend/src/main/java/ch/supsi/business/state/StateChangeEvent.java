package ch.supsi.business.state;

import ch.supsi.application.state.StateChangeListener;

/**
 * Represents an application event that allows the registration and removal of listeners
 * for state changes. Classes implementing this interface manage state change listeners
 * and notify them when a state change occurs.
 */
public interface StateChangeEvent {

    /**
     * Registers a listener to be notified when a state change occurs.
     *
     * @param listener the listener to register
     */
    void registerStateListener(StateChangeListener listener);

    /**
     * Deregister a previously registered listener, removing it from receiving state change notifications.
     *
     * @param listener the listener to deregister
     */
    void deregisterStateListener(StateChangeListener listener);
}
