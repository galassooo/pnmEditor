package ch.supsi.application.state;

import ch.supsi.business.state.EditorState;

public interface EditorStateManager extends EditorState {
    void onImageLoaded();
    void onFilterAdded();
    void onFilterProcessed();
    void onFilterRemoved();
}