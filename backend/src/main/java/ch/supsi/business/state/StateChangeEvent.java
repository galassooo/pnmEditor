package ch.supsi.business.state;

import ch.supsi.application.state.StateChangeListener;

public interface StateChangeEvent {
    void registerStateListener(StateChangeListener listener);
    void deregisterStateListener(StateChangeListener listener);
}
