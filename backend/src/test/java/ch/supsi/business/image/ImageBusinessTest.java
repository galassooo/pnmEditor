package ch.supsi.business.image;

import ch.supsi.application.image.WritableImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class ImageBusinessTest {
    @TempDir
    Path tempDir;

    private ImageBusiness imageBusiness;
    private Path testPath;

    @BeforeEach
    void setUp() throws IOException {
        testPath = tempDir.resolve("test.pbm");
        // Create a valid PBM file
        Files.writeString(testPath, "P1\n2 2\n1 0\n0 1\n");

        long[][] pixels = {{1, 2, 3}, {4, 5, 6}};
        ImageBuilder builder = new ImageBuilder()
                .withFilePath(testPath.toAbsolutePath().toString())
                .withMagicNumber("P1")
                .withPixels(pixels)
                .build();

        imageBusiness = new ImageBusiness(builder);
    }

    @Test
    void testRead() throws IOException, IllegalAccessException {
        WritableImage image = ImageBusiness.read(testPath.toAbsolutePath().toString());
        assertNotNull(image);
        assertEquals("P1", image.getMagicNumber());
        assertEquals(testPath.toAbsolutePath().toString(), image.getFilePath());
    }

    @Test
    void testPersistWithNewPath() throws IOException, IllegalAccessException {
        // First read the test file
        imageBusiness = (ImageBusiness) ImageBusiness.read(testPath.toAbsolutePath().toString());

        // Then try to persist to a new path
        Path newPath = tempDir.resolve("newImage.pbm");
        imageBusiness.persist(newPath.toAbsolutePath().toString());
        assertEquals(newPath.toAbsolutePath().toString(), imageBusiness.getFilePath());
        assertTrue(Files.exists(newPath));
    }

    @Test
    void testPersistWithSamePath() throws IOException, IllegalAccessException {
        imageBusiness = (ImageBusiness) ImageBusiness.read(testPath.toAbsolutePath().toString());
        String originalPath = imageBusiness.getFilePath();
        imageBusiness.persist(originalPath);
        assertEquals(originalPath, imageBusiness.getFilePath());
    }

    @Test
    void testPersistWithNullPath() throws IOException, IllegalAccessException {
        imageBusiness = (ImageBusiness) ImageBusiness.read(testPath.toAbsolutePath().toString());
        String originalPath = imageBusiness.getFilePath();
        imageBusiness.persist(null);
        assertEquals(originalPath, imageBusiness.getFilePath());
    }

    @Test
    void testExport() throws IOException, IllegalAccessException {
        imageBusiness = (ImageBusiness) ImageBusiness.read(testPath.toAbsolutePath().toString());
        Path exportPath = tempDir.resolve("exported.pgm");
        imageBusiness.export("pgm", exportPath.toAbsolutePath().toString());
        assertTrue(Files.exists(exportPath));
    }

    @Test
    void testDimensions() throws IOException, IllegalAccessException {
        imageBusiness = (ImageBusiness) ImageBusiness.read(testPath.toAbsolutePath().toString());
        assertEquals(2, imageBusiness.getWidth());
        assertEquals(2, imageBusiness.getHeight());
    }

    @Test
    void testGetName() throws IOException, IllegalAccessException {
        imageBusiness = (ImageBusiness) ImageBusiness.read(testPath.toAbsolutePath().toString());
        assertEquals("test.pbm", imageBusiness.getName());
    }

    @Test
    void testExportWithSamePathUsesNewPath() throws IOException, IllegalAccessException {
        imageBusiness = (ImageBusiness) ImageBusiness.read(testPath.toAbsolutePath().toString());
        Path exportPath = tempDir.resolve("exported.pgm");
        String originalPath = imageBusiness.getFilePath();

        imageBusiness.export("pgm", originalPath);
        ImageBuilder exportedImage = new ImageBuilder()
                .withFilePath(originalPath)  // Should use original path
                .withMagicNumber("P2")
                .withPixels(imageBusiness.getPixels())
                .build();

        assertEquals(originalPath, exportedImage.getFilePath());
    }

    @Test
    void testGetWidthWithNullPixels() {
        ImageBuilder builder = new ImageBuilder()
                .withPixels(null)
                .withFilePath(testPath.toAbsolutePath().toString())
                .withMagicNumber("P1")
                .build();

        ImageBusiness image = new ImageBusiness(builder);
        assertEquals(0, image.getWidth());
    }

    @Test
    void testGetWidthWithEmptyPixels() {
        ImageBuilder builder = new ImageBuilder()
                .withPixels(new long[0][])
                .withFilePath(testPath.toAbsolutePath().toString())
                .withMagicNumber("P1")
                .build();

        ImageBusiness image = new ImageBusiness(builder);
        assertEquals(0, image.getWidth());
    }

    @Test
    void testGetWidthWithValidPixels() {
        long[][] pixels = {{1, 2, 3}, {4, 5, 6}};
        ImageBuilder builder = new ImageBuilder()
                .withPixels(pixels)
                .withFilePath(testPath.toAbsolutePath().toString())
                .withMagicNumber("P1")
                .build();

        ImageBusiness image = new ImageBusiness(builder);
        assertEquals(3, image.getWidth());
    }

    @Test
    void testSetPixels(){
        long[][] pixels = {{1, 2, 3}, {4, 5, 6}};
        ImageBuilder builder = new ImageBuilder()
                .withFilePath(testPath.toAbsolutePath().toString())
                .withMagicNumber("P1")
                .withPixels(pixels)
                .build();

        imageBusiness = new ImageBusiness(builder);
        long[][] pixels1 = {{4, 6, 1}, {7, 35, 56}};

        imageBusiness.setPixels(pixels1);
        assertArrayEquals(pixels1, imageBusiness.getPixels());
    }

    @Test
    void testEqualsSameObject(){
        assertEquals(imageBusiness, imageBusiness);
    }

    @Test
    void testEqualsDifferentTypes(){
        assertNotEquals(imageBusiness, "imageBusiness");
    }

    @Test
    void testEqualsSameValues(){
        long[][] pixels = {{1, 2, 3}, {4, 5, 6}};
        ImageBuilder builder = new ImageBuilder()
                .withFilePath(testPath.toAbsolutePath().toString())
                .withMagicNumber("P1")
                .withPixels(pixels)
                .build();

        ImageBusiness imageBusiness1 = new ImageBusiness(builder);

        assertEquals(imageBusiness, imageBusiness1);
        assertEquals(imageBusiness.hashCode(), imageBusiness1.hashCode());
    }

    @Test
    void testEqualsDifferentPixels(){
        long[][] pixels = {{1, 2, 0}, {4, 5, 6}};
        ImageBuilder builder = new ImageBuilder()
                .withFilePath(testPath.toAbsolutePath().toString())
                .withMagicNumber("P1")
                .withPixels(pixels)
                .build();

        ImageBusiness imageBusiness1 = new ImageBusiness(builder);

        assertNotEquals(imageBusiness, imageBusiness1);
        assertNotEquals(imageBusiness.hashCode(), imageBusiness1.hashCode());
    }

    @Test
    void testEqualsDifferentPath(){
        long[][] pixels = {{1, 2, 3}, {4, 5, 6}};
        ImageBuilder builder = new ImageBuilder()
                .withFilePath(testPath.toAbsolutePath()+"ciao")
                .withMagicNumber("P1")
                .withPixels(pixels)
                .build();

        ImageBusiness imageBusiness1 = new ImageBusiness(builder);

        assertNotEquals(imageBusiness, imageBusiness1);
    }

    @Test
    void testEqualsDifferentMagicNumber(){
        long[][] pixels = {{1, 2, 3}, {4, 5, 6}};
        ImageBuilder builder = new ImageBuilder()
                .withFilePath(testPath.toAbsolutePath().toString())
                .withMagicNumber("P9")
                .withPixels(pixels)
                .build();

        ImageBusiness imageBusiness1 = new ImageBusiness(builder);

        assertNotEquals(imageBusiness, imageBusiness1);
    }
}