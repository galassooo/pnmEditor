package ch.supsi.business.state;

import ch.supsi.application.state.EditorStateManager;
import ch.supsi.application.state.StateChangeListener;

import java.util.ArrayList;
import java.util.List;

public class BusinessEditorState implements EditorStateManager, StateChangeEvent {
    private static BusinessEditorState instance;

    private static EditorState currentState = new NoImageState();

    private final List<StateChangeListener> listeners = new ArrayList<>();

    public static BusinessEditorState getInstance() {
        if (instance == null) {
            instance = new BusinessEditorState();
        }
        return instance;
    }

    // implementazione dei metodi dell'interfaccia che delegano allo stato corrente
    @Override
    public boolean canApplyFilters() {
        return currentState.canApplyFilters();
    }

    @Override
    public boolean canSave() {
        return currentState.canSave();
    }

    @Override
    public boolean canSaveAs() {
        return currentState.canSaveAs();
    }

    @Override
    public boolean canAddFilter() {
        return currentState.canAddFilter();
    }

    @Override
    public boolean canExport() {
        return currentState.canExport();
    }

    @Override
    public boolean hasUnsavedChanges() {
        return currentState.hasUnsavedChanges();
    }

    @Override
    public boolean isRefreshRequired() {
        return currentState.isRefreshRequired();
    }

    @Override
    public void onImageLoaded() {
        currentState = new ImageLoadedState();
        listeners.forEach(StateChangeListener::onStateChange);
    }

    @Override
    public void onFilterAdded() {
        currentState = new FilterPending();
        listeners.forEach(StateChangeListener::onStateChange);
    }

    @Override
    public void onFilterProcessed() {
        currentState = new ImageLoadedState();
        listeners.forEach(StateChangeListener::onStateChange);
    }

    @Override
    public void onFilterRemoved() {
        currentState = new ImageLoadedState();
        listeners.forEach(StateChangeListener::onStateChange);
    }

    @Override
    public void registerStateListener(StateChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void deregisterStateListener(StateChangeListener listener) {
        listeners.remove(listener);
    }


    static class NoImageState implements EditorState {
        @Override public boolean canApplyFilters() { return false; }
        @Override public boolean canSave() { return false; }
        @Override public boolean canSaveAs() { return false; }
        @Override public boolean canAddFilter() { return false; }
        @Override public boolean canExport() {return false;}
        @Override public boolean hasUnsavedChanges() { return false; }
        @Override public boolean isRefreshRequired() { return false; }
    }

    static class ImageLoadedState implements EditorState {
        @Override public boolean canApplyFilters() { return true; }
        @Override public boolean canSave() { return true; }
        @Override public boolean canSaveAs() { return true; }
        @Override public boolean canAddFilter() { return true; }
        @Override public boolean canExport() { return true; }
        @Override public boolean hasUnsavedChanges() { return false; }
        @Override public boolean isRefreshRequired() { return true; }
    }

    static class FilterPending implements EditorState {
        @Override public boolean canApplyFilters() { return true; }
        @Override public boolean canSave() { return true; }
        @Override public boolean canSaveAs() { return true; }
        @Override public boolean canAddFilter() { return true; }
        @Override public boolean canExport() { return true; }
        @Override public boolean hasUnsavedChanges() { return true; }
        @Override public boolean isRefreshRequired() { return false; }
    }
}