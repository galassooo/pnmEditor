package org.supsi.model.state;

import ch.supsi.application.state.StateApplication;
import ch.supsi.application.state.StateChangeListener;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Represents the state model for managing application states and exposing them to the view.
 * Observes changes in the application state and updates internal properties for bindings.
 */
public class StateModel implements StateChangeListener, IStateModel {

    private static StateModel myself;
    private final StateApplication stateApp;
    private final SimpleBooleanProperty canSave;
    private final SimpleBooleanProperty canSaveAs;
    private final SimpleBooleanProperty canExport;
    private final SimpleBooleanProperty canApplyFilters;
    private final SimpleBooleanProperty canAddFilter;
    private final SimpleBooleanProperty refreshRequired;
    private final SimpleBooleanProperty areChangesPending;

    /**
     * Initializes the {@code StateModel} and registers it as a state listener.
     * Creates bindings for state properties.
     */
    protected StateModel() {
        stateApp = StateApplication.getInstance();
        canSave = new SimpleBooleanProperty();
        canSaveAs = new SimpleBooleanProperty();
        canExport = new SimpleBooleanProperty();
        canApplyFilters = new SimpleBooleanProperty();
        canAddFilter  = new SimpleBooleanProperty();
        refreshRequired = new SimpleBooleanProperty();
        areChangesPending = new SimpleBooleanProperty();

        stateApp.registerStateListener(this);
    }

    /**
     * Retrieves the singleton instance of the {@code StateModel}.
     *
     * @return the singleton instance of {@code StateModel}
     */
    public static StateModel getInstance() {
        if (myself == null) {
            myself = new StateModel();
        }
        return myself;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStateChange() {
        canSave.set(stateApp.canSave());
        canSaveAs.set(stateApp.canSaveAs());
        canExport.set(stateApp.canExport());
        canApplyFilters.set(stateApp.canApplyFilters());
        canAddFilter.set(stateApp.canAddFilter());
        refreshRequired.set(stateApp.isRefreshRequired());
        areChangesPending.set(stateApp.areChangesPending());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReadOnlyBooleanProperty canSaveProperty() { return canSave; }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReadOnlyBooleanProperty canSaveAsProperty() { return canSaveAs; }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReadOnlyBooleanProperty canExportProperty() { return canExport; }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReadOnlyBooleanProperty canApplyFiltersProperty() { return canApplyFilters; }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReadOnlyBooleanProperty refreshRequiredProperty() { return refreshRequired; }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReadOnlyBooleanProperty areChangesPending() {
        return areChangesPending;
    }
}