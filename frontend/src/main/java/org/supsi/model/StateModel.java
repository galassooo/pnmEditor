package org.supsi.model;

import ch.supsi.application.state.StateApplication;
import ch.supsi.application.state.StateChangeListener;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class StateModel implements StateChangeListener, IStateModel {
    private static StateModel myself;
    private final StateApplication stateApp = StateApplication.getInstance();
    private final SimpleBooleanProperty canSave = new SimpleBooleanProperty();
    private final SimpleBooleanProperty canSaveAs = new SimpleBooleanProperty();
    private final SimpleBooleanProperty canExport = new SimpleBooleanProperty();
    private final SimpleBooleanProperty canApplyFilters = new SimpleBooleanProperty();
    private final SimpleBooleanProperty canAddFilter = new SimpleBooleanProperty();
    private final SimpleBooleanProperty refreshRequired = new SimpleBooleanProperty();

    public static StateModel getInstance() {
        if (myself == null) {
            myself = new StateModel();
        }
        return myself;
    }

    private StateModel() {
        stateApp.registerStateListener(this);
    }

    @Override
    public void onStateChange() {
        canSave.set(stateApp.canSave());
        canSaveAs.set(stateApp.canSaveAs());
        canExport.set(stateApp.canExport());
        canApplyFilters.set(stateApp.canApplyFilters());
        canAddFilter.set(stateApp.canAddFilter());
        refreshRequired.set(stateApp.isRefreshRequired());
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
    public ReadOnlyBooleanProperty canAddFilterProperty() { return canAddFilter; }

    @Override
    public ReadOnlyBooleanProperty refreshRequiredProperty() { return refreshRequired; }
}