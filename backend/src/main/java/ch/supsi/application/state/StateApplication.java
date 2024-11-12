package ch.supsi.application.state;

import ch.supsi.business.state.BusinessEditorState;
import ch.supsi.business.state.EditorState;
import ch.supsi.business.state.StateChangeEvent;

public class StateApplication implements EditorState, StateChangeEvent {

    private static StateApplication myself;

    private final EditorStateManager businessEditorState = BusinessEditorState.getInstance();

    private StateApplication(){}

    public static StateApplication getInstance(){
        if(myself==null){
            myself = new StateApplication();
        }
        return myself;
    }


    @Override
    public boolean canApplyFilters() {
        return businessEditorState.canApplyFilters();
    }

    @Override
    public boolean canSave() {
        return businessEditorState.canSave();
    }

    @Override
    public boolean canSaveAs() {
        return businessEditorState.canSaveAs();
    }

    @Override
    public boolean canAddFilter() {
        return businessEditorState.canAddFilter();
    }

    @Override
    public boolean canExport() {
        return businessEditorState.canExport();
    }


    @Override
    public boolean isRefreshRequired() {
        return businessEditorState.isRefreshRequired();
    }

    @Override
    public boolean areChangesPending() {
        return businessEditorState.areChangesPending();
    }

    @Override
    public void registerStateListener(StateChangeListener listener) {
        ((StateChangeEvent) businessEditorState).registerStateListener(listener);
    }

    @Override
    public void deregisterStateListener(StateChangeListener listener) {
        ((StateChangeEvent) businessEditorState).deregisterStateListener(listener);
    }
}
