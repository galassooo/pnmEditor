package org.supsi.model.state;

import javafx.beans.property.ReadOnlyBooleanProperty;

/**
 * Defines the behavior that a state model should expose to manage application states.
 * Provides properties to observe and interact with different application states such as saving, exporting, and applying filters.
 */
public interface IStateModel {
    /**
     * Retrieves the property indicating whether saving is currently allowed.
     *
     * @return a {@link ReadOnlyBooleanProperty} representing the save state
     */
    ReadOnlyBooleanProperty canSaveProperty();

    /**
     * Retrieves the property indicating whether "Save As" is currently allowed.
     *
     * @return a {@link ReadOnlyBooleanProperty} representing the "Save As" state
     */
    ReadOnlyBooleanProperty canSaveAsProperty();

    /**
     * Retrieves the property indicating whether "Export" is currently allowed.
     *
     * @return a {@link ReadOnlyBooleanProperty} representing the "Export" state
     */
    ReadOnlyBooleanProperty canExportProperty();

    /**
     * Retrieves the property indicating whether applying filters is currently allowed.
     *
     * @return a {@link ReadOnlyBooleanProperty} representing the filter application state
     */
    ReadOnlyBooleanProperty canApplyFiltersProperty();

    /**
     * Retrieves the property indicating whether a refresh is required.
     *
     * @return a {@link ReadOnlyBooleanProperty} representing the refresh required state
     */
    ReadOnlyBooleanProperty refreshRequiredProperty();

    /**
     * Retrieves the property indicating whether there are unsaved changes pending.
     *
     * @return a {@link ReadOnlyBooleanProperty} representing the changes pending state
     */
    ReadOnlyBooleanProperty areChangesPending();
}
