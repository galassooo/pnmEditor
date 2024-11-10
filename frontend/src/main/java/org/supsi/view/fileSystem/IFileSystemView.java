package org.supsi.view.fileSystem;

import java.io.File;

public interface IFileSystemView {

    File askForFile();

    File askForDirectory();

    void setFileExtension(String fileExtension);
}
