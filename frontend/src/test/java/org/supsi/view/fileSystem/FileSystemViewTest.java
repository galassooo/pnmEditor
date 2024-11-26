package org.supsi.view.fileSystem;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.supsi.view.fileSystem.FileSystemView;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FileSystemViewTest {


    @BeforeAll
    public static void setUpClass() {
        mockConstruction(FileChooser.class,
                (mock, context) -> {
                    File testFile = new File(FileSystemViewTest.class.getResource("/image.ppm").getFile());
                    File testDir= new File(FileSystemViewTest.class.getResource("/").getFile());
                    when(mock.showOpenDialog(any())).thenReturn(testFile);
                    when(mock.showSaveDialog(any())).thenReturn(testDir);
                });
    }
    @Test
    void testConstruction(){
        FileSystemView view = new FileSystemView(mock(Stage.class));
        assertEquals(view, view);
    }

    @Test
    void testConstructionEx(){
        IllegalArgumentException e = assertThrows( IllegalArgumentException.class, ()-> new FileSystemView(null));
        assertEquals("root must not be null", e.getMessage());
    }

    @Test
    void testSetExtension(){

        FileSystemView view = new FileSystemView(mock(Stage.class));
        assertDoesNotThrow(()-> view.setFileExtension("EXTENSION"));
        view.askForDirectory();

    }

    @Test
    void testNoSetExtension(){
        FileSystemView view = new FileSystemView(mock(Stage.class));
        view.askForDirectory();

    }
}