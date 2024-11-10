package org.supsi.model;

import javafx.beans.property.ReadOnlyBooleanProperty;

public interface IStateModel {
    ReadOnlyBooleanProperty canSaveProperty();
    ReadOnlyBooleanProperty canSaveAsProperty();
    ReadOnlyBooleanProperty canExportProperty();
    ReadOnlyBooleanProperty canApplyFiltersProperty();
    ReadOnlyBooleanProperty canAddFilterProperty();
    ReadOnlyBooleanProperty refreshRequiredProperty();
}
