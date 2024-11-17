package org.supsi.model.state;

import ch.supsi.application.state.StateApplication;
import ch.supsi.application.state.StateChangeListener;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

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

    public static StateModel getInstance() {
        if (myself == null) {
            myself = new StateModel();
        }
        return myself;
    }

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

    // property getters per il binding
    @Override
    public ReadOnlyBooleanProperty canSaveProperty() { return canSave; }

    @Override
    public ReadOnlyBooleanProperty canSaveAsProperty() { return canSaveAs; }

    @Override
    public ReadOnlyBooleanProperty canExportProperty() { return canExport; }

    @Override
    public ReadOnlyBooleanProperty canApplyFiltersProperty() { return canApplyFilters; }

    @Override
    public ReadOnlyBooleanProperty refreshRequiredProperty() { return refreshRequired; }

    @Override
    public ReadOnlyBooleanProperty areChangesPending() {
        return areChangesPending;
    }
}