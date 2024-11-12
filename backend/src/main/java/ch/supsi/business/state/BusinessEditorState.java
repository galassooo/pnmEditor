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
    public boolean isRefreshRequired() {
        return currentState.isRefreshRequired();
    }

    @Override
    public boolean areChangesPending() {
        return currentState.areChangesPending();
    }

    @Override
    public void onLoading() {
        currentState = new LoadingState();
        listeners.forEach(StateChangeListener::onStateChange);
    }

    @Override
    public void onLoadingError() {
        currentState = new NoImageState();
        listeners.forEach(StateChangeListener::onStateChange);
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
        currentState = new EditedImage();
        listeners.forEach(StateChangeListener::onStateChange);
    }

    @Override
    public void onFiltersRemoved() {
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

    static abstract class DefaultState implements EditorState {
        @Override public boolean canApplyFilters() { return false; }
        @Override public boolean canSave() { return false; }
        @Override public boolean canSaveAs() { return false; }
        @Override public boolean canAddFilter() { return false; }
        @Override public boolean canExport() {return false;}
        @Override public boolean isRefreshRequired() { return false; }
        @Override public boolean areChangesPending() { return false; }
    }

    static class NoImageState extends DefaultState {

    }

    static class ImageLoadedState extends DefaultState {
        @Override public boolean canApplyFilters() { return true; }
        @Override public boolean canSave() { return true; }
        @Override public boolean canSaveAs() { return true; }
        @Override public boolean canAddFilter() { return true; }
        @Override public boolean canExport() { return true; }
        @Override public boolean isRefreshRequired() { return true; }
    }

    static class FilterPending extends DefaultState {
        @Override public boolean canApplyFilters() { return true; }
        @Override public boolean canSave() { return true; }
        @Override public boolean canSaveAs() { return true; }
        @Override public boolean canAddFilter() { return true; }
        @Override public boolean canExport() { return true; }
        @Override public boolean areChangesPending() { return true; }
    }

    static class LoadingState extends DefaultState {
        @Override public boolean areChangesPending() { return true; }
    }

    static class EditedImage extends DefaultState {
        @Override public boolean canApplyFilters() { return true; }
        @Override public boolean canSave() { return true; }
        @Override public boolean canSaveAs() { return true; }
        @Override public boolean canAddFilter() { return true; }
        @Override public boolean canExport() {return true;}
        @Override public boolean isRefreshRequired() { return true; }
        @Override public boolean areChangesPending() { return true; }
    }
}