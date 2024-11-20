package ch.supsi.dataaccess.image;

import ch.supsi.application.image.WritableImage;
import ch.supsi.business.image.ImageDataAccess;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

class DataAccessFactoryTest {

    @TempDir
    Path tempDir;

    @Test
    void testGetDefaultMagicNumberFromExtension() {
        Optional<String> magicNumber = DataAccessFactory.getDefaultMagicNumberFromExtension("pbm");
        assertTrue(magicNumber.isPresent());
        assertEquals("P1", magicNumber.get());
    }

    @Test
    void testGetDefaultMagicNumberFromInvalidExtension() {
        Optional<String> magicNumber = DataAccessFactory.getDefaultMagicNumberFromExtension("invalid");
        assertFalse(magicNumber.isPresent());
    }

    @Test
    void testGetInstanceFromMagicNumber() throws IOException, IllegalAccessException {
        ImageDataAccess instance = DataAccessFactory.getInstanceFromMagicNumber("P1");
        assertNotNull(instance);
        assertInstanceOf(ImageDataAccess.class, instance);
    }

    @Test
    void testGetInstanceFromInvalidMagicNumber() {
        assertThrows(IOException.class, () ->
                DataAccessFactory.getInstanceFromMagicNumber("invalid")
        );
    }

    @Test
    void testConstructor(){
        assertDoesNotThrow(DataAccessFactory::new);
    }
    @Test
    void testGetInstanceFromExtension() throws IOException, IllegalAccessException {
        ImageDataAccess instance = DataAccessFactory.getInstanceFromExtension("ppm");
        assertNotNull(instance);
    }

    @Test
    void testGetInstanceFromInvalidExtension() {
        assertThrows(IOException.class, () ->
                DataAccessFactory.getInstanceFromExtension("invalid")
        );
    }

    @Test
    void testSupportedExtensions() {
        var list = DataAccessFactory.getSupportedExtensions();
        assertNotNull(list);
        assertTrue(list.contains("pbm"));
        assertTrue(list.contains("ppm"));
        assertTrue(list.contains("pgm"));
    }

    @Test
    void testGetInstance() throws IOException, IllegalAccessException {
        Path tempFile = tempDir.resolve("asciiImage.pbm");
        String header = "P1\n#comment \n1 1\n";
        String data = "1";

        Files.write(tempFile, (header + data).getBytes());

        ImageDataAccess instance = DataAccessFactory.getInstance(tempFile.toAbsolutePath().toString());
        assertNotNull(instance);
    }

    @Test
    void testGetInstanceWithEmptyLines() throws IOException, IllegalAccessException {
        Path tempFile = tempDir.resolve("asciiImage.pbm");
        String header = "\n\n\nP1\n#comment \n1 1\n";
        String data = "1";

        Files.write(tempFile, (header + data).getBytes());

        ImageDataAccess instance = DataAccessFactory.getInstance(tempFile.toAbsolutePath().toString());
        assertNotNull(instance);
    }

    private static class FakeClass implements ImageDataAccess {

        private static final FakeClass instance = new FakeClass();

        private static FakeClass getInstance() {
            return instance;
        }

        @Override
        public WritableImage read(String path) throws IOException {
            return null;
        }

        @Override
        public WritableImage write(WritableImage image) throws IOException {
            return null;
        }
        // Implementazione minima di ImageDataAccess
    }

    @Test
    void getInstanceWithPrivateModifier() {
        String extension = "ppm";

        try (MockedStatic<DataAccessFactory> mockedFactory = mockStatic(DataAccessFactory.class)) {

            mockedFactory.when(() -> DataAccessFactory.getFromExtension(extension))
                    .thenReturn(FakeClass.class);

            //implementazione reale
            mockedFactory.when(() -> DataAccessFactory.getInstanceFromExtension(extension))
                    .thenCallRealMethod();
            mockedFactory.when(() -> DataAccessFactory.loadClazz(any()))
                    .thenCallRealMethod();
            mockedFactory.when(() -> DataAccessFactory.getSingletonInstance(any()))
                    .thenCallRealMethod();

            //non dovrebbe fallire e mettere il mod a public
            assertDoesNotThrow(() ->
                    DataAccessFactory.getInstanceFromExtension(extension)
            );
        }
    }

    private static class FakeClass1 implements ImageDataAccess {

        private static final FakeClass1 instance = new FakeClass1();

        public static FakeClass1 getInstance() throws IllegalAccessException {
            throw new IllegalAccessException();
        }

        @Override
        public WritableImage read(String path) throws IOException {
            return null;
        }

        @Override
        public WritableImage write(WritableImage image) throws IOException {
            return null;
        }
    }

    @Test
    void getInstanceWithSingletonException() {
        String extension = "ppm";

        try (MockedStatic<DataAccessFactory> mockedFactory = mockStatic(DataAccessFactory.class)) {
            mockedFactory.when(() -> DataAccessFactory.getFromExtension(extension))
                    .thenReturn(FakeClass1.class);

            //implementazione reale
            mockedFactory.when(() -> DataAccessFactory.getInstanceFromExtension(extension))
                    .thenCallRealMethod();
            mockedFactory.when(() -> DataAccessFactory.loadClazz(any()))
                    .thenCallRealMethod();
            mockedFactory.when(() -> DataAccessFactory.getSingletonInstance(any()))
                    .thenCallRealMethod();

            //non dovrebbe fallire e mettere il mod a public
            IllegalAccessException ie = assertThrows(IllegalAccessException.class, () ->
                    DataAccessFactory.getInstanceFromExtension(extension)
            );
            assertEquals("Singleton method thrown an exception.", ie.getMessage());
        }
    }

    private static class FakeClass2 implements ImageDataAccess {
        @Override
        public WritableImage read(String path) throws IOException {
            return null;
        }

        @Override
        public WritableImage write(WritableImage image) throws IOException {
            return null;
        }
    }

    @Test
    void getInstanceNoSingleton() throws IOException, IllegalAccessException {
        String extension = "ppm";

        try (MockedStatic<DataAccessFactory> mockedFactory = mockStatic(DataAccessFactory.class)) {
            //inietto fakeclass
            mockedFactory.when(() -> DataAccessFactory.getFromExtension(extension))
                    .thenReturn(FakeClass2.class);

            //implementazione reale
            mockedFactory.when(() -> DataAccessFactory.getInstanceFromExtension(extension))
                    .thenCallRealMethod();
            mockedFactory.when(() -> DataAccessFactory.loadClazz(any()))
                    .thenCallRealMethod();
            mockedFactory.when(() -> DataAccessFactory.getSingletonInstance(any()))
                    .thenCallRealMethod();

            //non dovrebbe fallire e ritornare null
            assertNull(
                    DataAccessFactory.getInstanceFromExtension(extension)
            );
        }
    }

}
