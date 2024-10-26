package ch.supsi.view.fileSystem;

import ch.supsi.model.image.IImageModel;
import ch.supsi.model.image.ImageModel;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class FileSystemView implements IFileSystemView {

    private final Stage root;
    private final IImageModel model = ImageModel.getInstance();

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
        fileChooser.setInitialFileName(model.getImageName());

        return fileChooser.showSaveDialog(root);

    }

}
