package ch.supsi.view;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class FileSystemView implements IFileSystemView{

    private Stage root;

    public FileSystemView(Stage root) {
        if(root == null){
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
}
