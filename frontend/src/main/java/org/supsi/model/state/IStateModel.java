package org.supsi.model.state;

import javafx.beans.property.ReadOnlyBooleanProperty;

public interface IStateModel {
    ReadOnlyBooleanProperty canSaveProperty();
    ReadOnlyBooleanProperty canSaveAsProperty();
    ReadOnlyBooleanProperty canExportProperty();
    ReadOnlyBooleanProperty canApplyFiltersProperty();
    ReadOnlyBooleanProperty refreshRequiredProperty();
    ReadOnlyBooleanProperty areChangesPending();
}
