package org.supsi.controller.image;

import org.supsi.controller.confirmation.ConfirmationController;
import org.supsi.controller.confirmation.IConfirmationController;
import org.supsi.controller.errors.ErrorController;
import org.supsi.controller.errors.IErrorController;
import org.supsi.view.image.ExportEvent;
import org.supsi.model.event.EventManager;
import org.supsi.model.event.EventSubscriber;
import org.supsi.model.state.IStateModel;
import org.supsi.model.state.StateModel;
import org.supsi.model.image.IImageModel;
import org.supsi.model.image.ImageModel;
import org.supsi.model.info.ILoggerModel;
import org.supsi.model.info.LoggerModel;
import org.supsi.view.fileSystem.FileSystemView;
import org.supsi.view.fileSystem.IFileSystemView;
import org.supsi.view.image.IImageView;
import javafx.stage.Stage;

import java.io.File;

/**
 * Controls image-related operations and manages interactions between the image view and model.
 * Handles file operations such as opening, saving, and exporting images.
 */
public class ImageController implements IImageController {

    private static ImageController myself;
    private final IImageModel model;
    private final IErrorController errorController;
    private final ILoggerModel loggerModel;
    private final IConfirmationController confirmationController;
    private IImageView mainImageView;
    private Stage root;

    /**
     * Private constructor for the ImageController.
     * Initializes the required components and sets up listeners for state changes and events:
     * - Instantiates the image model, error controller, logger, and confirmation controller.
     * - Registers a listener to refresh the image view when the state model indicates a refresh is required.
     * - Subscribes to export events to handle image export requests dynamically.
     */
    protected ImageController() {
        model = ImageModel.getInstance();
        errorController = ErrorController.getInstance();
        loggerModel = LoggerModel.getInstance();
        confirmationController = ConfirmationController.getInstance();

        IStateModel stateModel = StateModel.getInstance();
        stateModel.refreshRequiredProperty().addListener((obs, old, newValue) -> {
            if (newValue) {
                mainImageView.update();
            }
        });
        EventSubscriber subscriber = EventManager.getSubscriber();
        subscriber.subscribe(ExportEvent.ExportRequested.class, this::onExportRequested);
    }

    /**
     * Retrieves the singleton instance of the controller.
     *
     * @return the singleton instance
     */
    public static ImageController getInstance() {
        if (myself == null) {
            myself = new ImageController();
        }
        return myself;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void open() {
        confirmationController.requestConfirm((event) -> openImage());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save() {
        try {
            model.writeImage(null);
        } catch (Exception e) {
            errorController.showError(e.getMessage());
        }
        loggerModel.addInfo("ui_image_saved");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveAs() {
        IFileSystemView fsPopUp = getFileSystemView(root);
        File chosen = fsPopUp.askForDirectory();

        if (chosen == null) { //popup closed
            return;
        }
        try {
            model.writeImage(chosen.getPath());
        } catch (Exception e) {
            errorController.showError(e.getMessage());
        }
        loggerModel.addInfo("ui_image_saved");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setImage(IImageView image) {
        this.mainImageView = image;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStage(Stage stage) {
        this.root = stage;
    }

    protected FileSystemView getFileSystemView(Stage root){
        return new FileSystemView(root);
    }

    /**
     * Handles export events, allowing the user to save the image in a different format.
     *
     * @param event the export request event containing the desired file extension
     */
    protected void onExportRequested(ExportEvent.ExportRequested event) {
        IFileSystemView fsPopUp = getFileSystemView(root);
        fsPopUp.setFileExtension(event.extension());
        File chosen = fsPopUp.askForDirectory();


        if (chosen == null) { //popup closed
            return;
        }
        try {
            model.export(event.extension(), chosen.getPath());
        } catch (Exception e) {
            errorController.showError(e.getMessage());
        }
        loggerModel.addInfo("ui_image_exported");
    }

    /**
     * Opens an image file selected by the user and loads it into the model.
     */
    private void openImage() {

        IFileSystemView fsPopUp = getFileSystemView(root);
        File chosen = fsPopUp.askForFile();

        if (chosen == null) { //popup closed
            return;
        }

        try {
            model.readImage(chosen.getPath());
            loggerModel.addInfo("ui_image_loaded");
        } catch (Exception e) {
            errorController.showError(e.getMessage());
        }
    }
}
