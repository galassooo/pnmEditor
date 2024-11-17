package org.supsi.view;

/**
 * Defines the contract for a generic view in the application.
 * A view is responsible for presenting data from a model to the user.
 * This interface supports building or updating the view, displaying it, and associating it with a specific model.
 *
 * @param <T> the type of the model associated with the view
 */
public interface IView<T> {
    /**
     * Builds or updates the view with current model data
     */
    void build();

    /**
     * Shows/displays the view
     */
    void show();

    /**
     * Sets the model associated with this view
     * @param model The model to be associated with the view
     */
    void setModel(T model);
}