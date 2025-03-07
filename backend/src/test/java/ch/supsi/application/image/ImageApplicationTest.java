package ch.supsi.application.image;

import ch.supsi.business.translations.TranslationBusiness;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.MappedByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ImageApplicationTest {

    private ImageApplication application;

    @TempDir(cleanup =  CleanupMode.ALWAYS)
    private Path tempDir;


    @BeforeEach
    void setUp() {
        application = ImageApplication.getInstance();
    }

    @BeforeAll
    public static void firstTests() {
        assertTrue(ImageApplication.getInstance().getImagePixels().isEmpty());
        assertTrue(ImageApplication.getInstance().getImageName().isEmpty());
    }

    @Test
    void testSingleton(){
        ImageApplication application = ImageApplication.getInstance();
        ImageApplication other = ImageApplication.getInstance();

        assertSame(application, other);
    }
    @Test
    void testGetImage() throws IOException, IllegalAccessException {
        Path tempFile = tempDir.resolve("image.pbm");
        String header = "P1\n2 2\n";

        String asciiData =
                """
                        0 1
                        0 1
                        """;
        Files.write(tempFile, (header + asciiData).getBytes());

        long[][] expected = {
                {0XFFFFFFFFL, 0XFF000000L},
                {0XFFFFFFFFL, 0XFF000000L},
        };

        assertDoesNotThrow(() -> application.read(tempFile.toAbsolutePath().toString()));
        //assertArrayEquals(application.getImagePixels(), expected);
        assertNotNull(application.getCurrentImage());
    }
    @Test
    void testRead() throws IOException {
        Path tempFile = tempDir.resolve("ciao.pbm");
        String header = "P1\n4 4\n";

        String asciiData =
                """
                        0 1 1 0
                        1 0 0 1
                        0 1 1 0
                        1 0 0 1
                        """;
        Files.write(tempFile, (header + asciiData).getBytes());
        assertDoesNotThrow(() -> application.read(tempFile.toAbsolutePath().toString()));
        assertEquals(application.getImageName().get(),"ciao.pbm");
    }

    @Test
    void testWriteNullPath() throws IOException {
        Path tempFile = tempDir.resolve("ciao.pbm");
        String header = "P1\n4 4\n";

        String asciiData =
                """
                        0 1 1 0
                        1 0 0 1
                        0 1 1 0
                        1 0 0 1
                        """;
        Files.write(tempFile, (header + asciiData).getBytes());
        assertDoesNotThrow(() -> application.read(tempFile.toAbsolutePath().toString()));

        assertDoesNotThrow(() -> application.persist(null));
    }

    @Test
    void testWriteNonNullPath() throws IOException {
        Path tempFile = tempDir.resolve("ciao.pbm");
        String header = "P1\n4 4\n";

        Path output = tempDir.resolve("test.pbm");
        String asciiData =
                """
                        0 1 1 0
                        1 0 0 1
                        0 1 1 0
                        1 0 0 1
                        """;
        Files.write(tempFile, (header + asciiData).getBytes());
        assertDoesNotThrow(() -> application.read(tempFile.toAbsolutePath().toString()));

        assertDoesNotThrow(() -> application.persist(output.toAbsolutePath().toString()));
        assertEquals("test.pbm",application.getImageName().get());
    }

    @Test
    void testSupportedExtensions() throws IOException {
        assertFalse(application.getAllSupportedExtension().isEmpty());
    }

    @Test
    void testGetPixels() throws IOException {
        Path tempFile = tempDir.resolve("ciao.pbm");
        String header = "P1\n4 4\n";

        Path output = tempDir.resolve("test.pbm");
        String asciiData =
                """
                        0 1 1 0
                        1 0 0 1
                        0 1 1 0
                        1 0 0 1
                        """;
        Files.write(tempFile, (header + asciiData).getBytes());
        assertDoesNotThrow(() -> application.read(tempFile.toAbsolutePath().toString()));
        assertFalse(application.getImagePixels().isEmpty());
        assertDoesNotThrow(() -> application.persist(output.toAbsolutePath().toString()));
        assertFalse(application.getImagePixels().isEmpty());
    }

    @Test
    void testExport() throws IOException {
        Path tempFile = tempDir.resolve("ciao.pbm");
        Path outputPath = tempDir.resolve("ciao.pgm");
        String header = "P1\n4 4\n";

        String asciiData =
                """
                        0 1 1 0
                        1 0 0 1
                        0 1 1 0
                        1 0 0 1
                        """;
        Files.write(tempFile, (header + asciiData).getBytes());
        assertDoesNotThrow(() -> application.read(tempFile.toAbsolutePath().toString()));
        assertDoesNotThrow(() -> application.export("pgm",outputPath.toAbsolutePath().toString()));
    }

    @Test
    void errorOnReading() throws IOException {
        IOException e = assertThrows(IOException.class, () -> application.read("invalid path"));
    }

    @Test
    void errorOnFirstReading() throws IOException, NoSuchFieldException, IllegalAccessException {

        //force reset
        Field instanceField = ImageApplication.class.getDeclaredField("myself");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        //init
        application = ImageApplication.getInstance();


        IOException e = assertThrows(IOException.class, () -> application.read("invalid path"));
    }
}
