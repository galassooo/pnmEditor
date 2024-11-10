package org.supsi.view.fileSystem;

import org.supsi.model.image.IImageModel;
import org.supsi.model.image.ImageModel;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class FileSystemView implements IFileSystemView {

    private final Stage root;
    private final IImageModel model = ImageModel.getInstance();
    private String extension;

    public FileSystemView(Stage root) {
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
