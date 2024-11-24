package org.supsi.model.image;

import ch.supsi.application.image.ImageApplication;
import ch.supsi.application.image.WritableImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageModelTest {

    @Mock
    private ImageApplication mockedImageApplication;

    private IImageModel model;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        Field instance = ImageModel.class.getDeclaredField("myself");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    void testSingleton(){
        assertEquals(ImageModel.getInstance(), ImageModel.getInstance());
    }

    @Test
    void testReadNoExceptions() throws IOException, IllegalAccessException {
        try (MockedStatic<ImageApplication> mockedStatic = mockStatic(ImageApplication.class)) {

            mockedStatic.when(ImageApplication::getInstance).thenReturn(mockedImageApplication);
            when(mockedImageApplication.read("test-image-path")).thenReturn(null);

            model = ImageModel.getInstance();
            model.readImage("test-image-path");

            verify(mockedImageApplication).read("test-image-path");
        }
    }

    @Test
    void testReadIllegalAccessEx() throws IOException, IllegalAccessException {
        try (MockedStatic<ImageApplication> mockedStatic = mockStatic(ImageApplication.class)) {

            mockedStatic.when(ImageApplication::getInstance).thenReturn(mockedImageApplication);
            when(mockedImageApplication.read("test-image-path")).thenThrow(new IllegalAccessException());

            model = ImageModel.getInstance();

            //should re throw ex
            assertThrows(IllegalAccessException.class, ()->model.readImage("test-image-path"));
        }
    }

    @Test
    void testReadIoEx() throws IOException, IllegalAccessException {
        try (MockedStatic<ImageApplication> mockedStatic = mockStatic(ImageApplication.class)) {

            mockedStatic.when(ImageApplication::getInstance).thenReturn(mockedImageApplication);
            when(mockedImageApplication.read("test-image-path")).thenThrow(new IOException());

            model = ImageModel.getInstance();

            //should re throw ex
            assertThrows(IOException.class, ()->model.readImage("test-image-path"));
        }
    }

    @Test
    void testGetImageName() throws IOException, IllegalAccessException {
        try (MockedStatic<ImageApplication> mockedStatic = mockStatic(ImageApplication.class)) {

            mockedStatic.when(ImageApplication::getInstance).thenReturn(mockedImageApplication);
            when(mockedImageApplication.getImageName()).thenReturn(Optional.of("cuteImage"));

            model = ImageModel.getInstance();

            assertEquals("cuteImage", model.getImageName());
        }
    }

    @Test
    void testGetImageNameEmpty() throws IOException, IllegalAccessException {
        try (MockedStatic<ImageApplication> mockedStatic = mockStatic(ImageApplication.class)) {

            mockedStatic.when(ImageApplication::getInstance).thenReturn(mockedImageApplication);
            when(mockedImageApplication.getImageName()).thenReturn(Optional.empty());

            model = ImageModel.getInstance();

            assertEquals("N/A", model.getImageName());
        }
    }

    @Test
    void testWriteNoExceptions() throws IOException, IllegalAccessException {
        try (MockedStatic<ImageApplication> mockedStatic = mockStatic(ImageApplication.class)) {

            mockedStatic.when(ImageApplication::getInstance).thenReturn(mockedImageApplication);
            doNothing().when(mockedImageApplication).persist(eq("test-image-path"));

            model = ImageModel.getInstance();
            model.writeImage("test-image-path");

            verify(mockedImageApplication).persist("test-image-path");
        }
    }

    @Test
    void testWriteIllegalAccessEx() throws IOException, IllegalAccessException {
        try (MockedStatic<ImageApplication> mockedStatic = mockStatic(ImageApplication.class)) {

            mockedStatic.when(ImageApplication::getInstance).thenReturn(mockedImageApplication);
            doThrow(new IllegalAccessException()).when(mockedImageApplication).persist("test-image-path");

            model = ImageModel.getInstance();

            //should re throw ex
            assertThrows(IllegalAccessException.class, ()->model.writeImage("test-image-path"));
        }
    }

    @Test
    void testWriteIoEx() throws IOException, IllegalAccessException {
        try (MockedStatic<ImageApplication> mockedStatic = mockStatic(ImageApplication.class)) {

            mockedStatic.when(ImageApplication::getInstance).thenReturn(mockedImageApplication);
            doThrow(new IOException()).when(mockedImageApplication).persist("test-image-path");

            model = ImageModel.getInstance();

            //should re throw ex
            assertThrows(IOException.class, ()->model.writeImage("test-image-path"));
        }
    }

    @Test
    void testGetSupportedExtensions() {
        try (MockedStatic<ImageApplication> mockedStatic = mockStatic(ImageApplication.class)) {
            mockedStatic.when(ImageApplication::getInstance).thenReturn(mockedImageApplication);

            when(mockedImageApplication.getAllSupportedExtension()).thenReturn(List.of("uno", "due", "ventisette"));

            model = ImageModel.getInstance();

            assertEquals(List.of("uno", "due", "ventisette"), model.getSupportedExtensions());
        }
    }
    @Test
    void testExportNoExceptions() throws IOException, IllegalAccessException {
        try (MockedStatic<ImageApplication> mockedStatic = mockStatic(ImageApplication.class)) {

            mockedStatic.when(ImageApplication::getInstance).thenReturn(mockedImageApplication);
            doNothing().when(mockedImageApplication).export("extension", "test-image-path");

            model = ImageModel.getInstance();
            model.export("extension", "test-image-path");

            verify(mockedImageApplication).export("extension","test-image-path");
        }
    }

    @Test
    void testExportIllegalAccessEx() throws IOException, IllegalAccessException {
        try (MockedStatic<ImageApplication> mockedStatic = mockStatic(ImageApplication.class)) {

            mockedStatic.when(ImageApplication::getInstance).thenReturn(mockedImageApplication);
            doThrow(new IllegalAccessException()).when(mockedImageApplication).export("ciao","test-image-path");

            model = ImageModel.getInstance();

            //should re throw ex
            assertThrows(IllegalAccessException.class, ()->model.export("ciao","test-image-path"));
        }
    }

    @Test
    void testExportIoEx() throws IOException, IllegalAccessException {
        try (MockedStatic<ImageApplication> mockedStatic = mockStatic(ImageApplication.class)) {

            mockedStatic.when(ImageApplication::getInstance).thenReturn(mockedImageApplication);
            doThrow(new IOException()).when(mockedImageApplication).export("odio i test","test-image-path");

            model = ImageModel.getInstance();

            //should re throw ex
            assertThrows(IOException.class, ()->model.export("odio i test","test-image-path"));
        }
    }

    @Test
    void testGetPixels(){
        try (MockedStatic<ImageApplication> mockedStatic = mockStatic(ImageApplication.class)) {

            mockedStatic.when(ImageApplication::getInstance).thenReturn(mockedImageApplication);
            long[][] data = {{1, 2}, {3,4}};
            when(mockedImageApplication.getImagePixels()).thenReturn((Optional.of(data)));

            model = ImageModel.getInstance();
            assertEquals(data, model.getImagePixels());
        }
    }

    @Test
    void testGetEmptyPixels(){
        try (MockedStatic<ImageApplication> mockedStatic = mockStatic(ImageApplication.class)) {

            mockedStatic.when(ImageApplication::getInstance).thenReturn(mockedImageApplication);
            when(mockedImageApplication.getImagePixels()).thenReturn((Optional.empty()));

            model = ImageModel.getInstance();
            assertArrayEquals(new long[0][], model.getImagePixels());
        }
    }
}
