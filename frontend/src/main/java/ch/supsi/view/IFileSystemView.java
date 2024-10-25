package ch.supsi.view;

import java.io.File;

public interface IFileSystemView {

    File askForFile();

    File askForDirectory();
}
