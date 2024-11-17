package org.supsi.view.image;

/**
 * Represents a contract for image view in the application.
 * Provides a method to update the view when changes occur in the underlying image model.
 */
public interface IImageView {

    /**
     * Updates the image view to reflect changes in the image model.
     */
    void update();
}
