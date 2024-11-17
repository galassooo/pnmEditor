package org.supsi.view.fileSystem;

import org.supsi.model.image.IImageModel;
import org.supsi.model.image.ImageModel;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * Represents a file system view for interacting with file and directory dialogs in the application.
 * Provides functionality to open and save files using a {@link FileChooser}.
 */
public class FileSystemView implements IFileSystemView {

    private final Stage root;
    private final IImageModel model;
    private String extension;

    /**
     * Constructs a new {@code FileSystemView} with the given root stage.
     *
     * @param root the {@link Stage} used as the owner for file dialogs
     * @throws IllegalArgumentException if the provided {@code root} is {@code null}
     */
    public FileSystemView(Stage root) {
        model = ImageModel.getInstance();

        if (root == null) {
            throw new IllegalArgumentException("root must not be null");
        }
        this.root = root;
    }

    @Override
    public File askForFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("prova");
        return fileChooser.showOpenDialog(root);
    }

    @Override
    public File askForDirectory() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Save File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setInitialFileName(extension == null ? model.getImageName() : model.getImageName().replaceAll("\\.[^.]+$", "." + extension));

        return fileChooser.showSaveDialog(root);

    }

    @Override
    public void setFileExtension(String fileExtension) {
        extension = fileExtension;
    }

}
