package org.supsi.view.fileSystem;

import java.io.File;

/**
 * defines a contract for Filesystem views
 */
public interface IFileSystemView {

    /**
     * Opens a file chooser dialog to allow the user to select a file to open.
     *
     * @return the selected {@link File}, or {@code null} if no file was selected
     */
    File askForFile();

    /**
     * Opens a file chooser dialog to allow the user to select a directory or file for saving.
     * Sets default file name and location based on the user's home directory and current image name.
     *
     * @return the selected {@link File}, or {@code null} if no directory was selected
     */
    File askForDirectory();

    /**
     * Sets the file extension to be used for saving files.
     *
     * @param fileExtension the file extension to use (e.g., "png", "jpg")
     */
    void setFileExtension(String fileExtension);
}
