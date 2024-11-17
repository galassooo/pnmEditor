package org.supsi.controller.image;

import org.supsi.view.image.IImageView;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Interface defining the core operations for image manipulation in the application.
 * Provides methods for loading, saving, and managing image files.
 */
public interface IImageController {

    /**
     * Saves the current image to its associated file path.
     */
    void save();

    /**
     * Prompts the user to select a file path and saves the current image to the chosen location.
     */
    void saveAs();

    /**
     * Opens a file dialog to allow the user to select an image file and loads the selected image.
     */
    void open();

    /**
     * Associates a view with the controller for managing image display and updates.
     *
     * @param image The {@link IImageView} instance to be associated with this controller.
     */
    void setImage(IImageView image);

    /**
     * Sets the stage for this controller, allowing it to manage its UI components.
     *
     * @param stage The {@link Stage} instance to be managed by this controller.
     */
    void setStage(Stage stage);
}
