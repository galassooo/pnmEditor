package dataaccess;

import ch.supsi.business.image.ImageBusiness;
import ch.supsi.business.strategy.ArgbThreeChannel;
import ch.supsi.dataaccess.image.PPMDataAccess;
import ch.supsi.application.image.ImageBusinessInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PPMDataAccessTest {

    /* instance fields*/
    private PPMDataAccess ppmDataAccess;

    @TempDir
    Path tempDir;

    /**
     * set up variables
     */
    @BeforeEach
    void setUp() {
        ppmDataAccess = PPMDataAccess.getInstance();
    }

    /* --------- singleton --------- */
    @Test
    void testSingleton() {
        PPMDataAccess ppmDataAccess2 = PPMDataAccess.getInstance();
        assertEquals(ppmDataAccess2, ppmDataAccess);
    }

    /* --------- valid input --------- */

    @Test
    void testReadBinary8BitValidInput() throws IOException {
        Path tempFile = tempDir.resolve("image.ppm");
        String header = "P6\n2 2\n255\n";
        byte[] binaryData = new byte[]{
                10, 20, 30,
                40, 50, 60,
                70, 80, 90,
                100, 110, 120
        };

        //create a stream containing header and data and write it to file
        byte[] fileContent = new byte[header.getBytes().length + binaryData.length];
        System.arraycopy(header.getBytes(), 0, fileContent, 0, header.getBytes().length);
        System.arraycopy(binaryData, 0, fileContent, header.getBytes().length, binaryData.length);
        Files.write(tempFile, fileContent);

        long[][] expectedMatrix = {
                {0xFF0A141EL, 0xFF28323CL},
                {0xFF46505AL, 0xFF646E78L}
        };

        ImageBusinessInterface img = ppmDataAccess.read(tempFile.toAbsolutePath().toString());
        assertArrayEquals(expectedMatrix, img.getPixels());
    }

    @Test
    void testReadBinary16BitValidInput() throws IOException {
        Path tempFile = tempDir.resolve("image.ppm");
        String header = "P6\n2 2\n65535\n";
        byte[] binaryData = new byte[]{
                0, 10, 0, 20, 0, 30,
                0, 40, 0, 50, 0, 60,
                0, 70, 0, 80, 0, 90,
                0, 100, 0, 110, 0, 120
        };

        byte[] fileContent = new byte[header.getBytes().length + binaryData.length];
        System.arraycopy(header.getBytes(), 0, fileContent, 0, header.getBytes().length);
        System.arraycopy(binaryData, 0, fileContent, header.getBytes().length, binaryData.length);
        Files.write(tempFile, fileContent);

        long[][] expectedMatrix = {
                {0xFF000000L, 0xFF000000L},
                {0xFF000000L, 0xFF000000L}
        };

        ImageBusinessInterface img = ppmDataAccess.read(tempFile.toAbsolutePath().toString());
        assertArrayEquals(expectedMatrix, img.getPixels());
    }

    @Test
    void testReadBinaryWithPadding() throws IOException {
        Path tempFile = tempDir.resolve("image.ppm");
        String header = "P6\n3 1\n255\n";
        byte[] binaryData = new byte[]{
                10, 20, 30,
                40, 50, 60,
                70, 80, 90,
                0, 0, 0//padding
        };

        byte[] fileContent = new byte[header.getBytes().length + binaryData.length];
        System.arraycopy(header.getBytes(), 0, fileContent, 0, header.getBytes().length);
        System.arraycopy(binaryData, 0, fileContent, header.getBytes().length, binaryData.length);
        Files.write(tempFile, fileContent);

        long[][] expectedMatrix = {
                {0xFF0A141EL, 0xFF28323CL, 0xFF46505AL}
        };

        ImageBusinessInterface img = ppmDataAccess.read(tempFile.toAbsolutePath().toString());
        assertArrayEquals(expectedMatrix, img.getPixels());
    }

    @Test
    void testReadAscii8bitValidInput() throws IOException {
        Path tempFile = tempDir.resolve("image.ppm");
        String asciiData = "P3\n2 2\n255\n10 20 30\n40 50 60\n70 80 90\n100 110 120\n";
        Files.write(tempFile, asciiData.getBytes());

        long[][] expectedMatrix = {
                {0xFF0A141EL, 0xFF28323CL},
                {0xFF46505AL, 0xFF646E78L}
        };
        ImageBusinessInterface img = ppmDataAccess.read(tempFile.toAbsolutePath().toString());
        assertArrayEquals(expectedMatrix, img.getPixels());
    }

    @Test
    void testReadAscii16bitValidInput() throws IOException {
        Path tempFile = tempDir.resolve("image.ppm");
        String asciiData = "P3\n2 2\n65355\n10 20 30\n40 50 60\n70 80 90\n100 110 120\n";
        Files.write(tempFile, asciiData.getBytes());

        long[][] expectedMatrix = {
                {0xFF000000L, 0xFF000000L},
                {0xFF000000L, 0xFF000000L}
        };
        ImageBusinessInterface img = ppmDataAccess.read(tempFile.toAbsolutePath().toString());
        assertArrayEquals(expectedMatrix, img.getPixels());
    }

    /* --------- invalid input --------- */

    @Test
    void testReadAsciiOutOfRangeColor() throws IOException {
        Path tempFile = tempDir.resolve("image.ppm");
        String asciiData = "P3\n2 2\n255\n256 20 30\n40 50 60\n70 80 90\n100 110 120\n";
        Files.write(tempFile, asciiData.getBytes());

        IOException e = assertThrows(IOException.class, () -> ppmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Color value out of range in ASCII PPM file", e.getMessage());
    }

    @Test
    void testReadAsciiNegativeColorInput() throws IOException {
        Path tempFile = tempDir.resolve("image.ppm");
        String asciiData = "P3\n2 2\n255\n-10 20 30\n40 50 60\n70 80 90\n100 110 120\n";
        Files.write(tempFile, asciiData.getBytes());

        IOException e = assertThrows(IOException.class, () -> ppmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Color value out of range in ASCII PPM file", e.getMessage());
    }
    @Test
    void testReadAsciiLiteralColorInput() throws IOException {
        Path tempFile = tempDir.resolve("image.ppm");
        String asciiData = "P3\n2 2\n255\n34 ac 30\n40 50 60\n70 80 90\n100 110 120\n";
        Files.write(tempFile, asciiData.getBytes());

        IOException e = assertThrows(IOException.class, () -> ppmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Invalid color data in ASCII PPM file", e.getMessage());
    }

    @Test
    void testIncompleteBinaryAtRed() throws IOException {
        Path tempFile = tempDir.resolve("image.ppm");
        String header = "P6\n3 1\n255\n";
        byte[] binaryData = new byte[]{
                10, 20, 30,
                40, 50, 60,

        };

        byte[] fileContent = new byte[header.getBytes().length + binaryData.length];
        System.arraycopy(header.getBytes(), 0, fileContent, 0, header.getBytes().length);
        System.arraycopy(binaryData, 0, fileContent, header.getBytes().length, binaryData.length);
        Files.write(tempFile, fileContent);

        IOException e = assertThrows(IOException.class, () -> ppmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Insufficient data in binary ppm file", e.getMessage());
    }

    @Test
    void testIncompleteBinaryAtGreen() throws IOException {
        Path tempFile = tempDir.resolve("image.ppm");
        String header = "P6\n2 2\n255\n";
        byte[] incompleteData = {10, 20, 30, 40};

        byte[] fileContent = new byte[header.getBytes().length + incompleteData.length];
        System.arraycopy(header.getBytes(), 0, fileContent, 0, header.getBytes().length);
        System.arraycopy(incompleteData, 0, fileContent, header.getBytes().length, incompleteData.length);
        Files.write(tempFile, fileContent);

        IOException e = assertThrows(IOException.class, () -> ppmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Insufficient data in binary ppm file", e.getMessage());
    }

    @Test
    void testIncompleteBinaryAtBlue() throws IOException {
        Path tempFile = tempDir.resolve("image.ppm");
        String header = "P6\n3 1\n255\n";
        byte[] binaryData = new byte[]{
                10, 20, 30,
                40, 50
        };

        byte[] fileContent = new byte[header.getBytes().length + binaryData.length];
        System.arraycopy(header.getBytes(), 0, fileContent, 0, header.getBytes().length);
        System.arraycopy(binaryData, 0, fileContent, header.getBytes().length, binaryData.length);
        Files.write(tempFile, fileContent);

        IOException e = assertThrows(IOException.class, () -> ppmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Insufficient data in binary ppm file", e.getMessage());
    }

    /* --------- maxValue invalid -------- */

    @Test
    void testOverFlowMaximumMaxValue() throws IOException {
        Path tempFile = tempDir.resolve("image.ppm");
        String header = "P6\n2 2\n65536\n";
        Files.write(tempFile, header.getBytes());

        IOException e = assertThrows(IOException.class, () -> ppmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Max value out of range in header", e.getMessage());
    }

    @Test
    void testMissingMaxValue() throws IOException {
        Path tempFile = tempDir.resolve("image.ppm");
        String header = "P6\n2 2\n";
        Files.write(tempFile, header.getBytes());

        IOException e = assertThrows(IOException.class, () -> ppmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Max value is invalid in header", e.getMessage());
    }
    @Test
    void testNegativeMaxValue() throws IOException {
        Path tempFile = tempDir.resolve("image.ppm");
        String header = "P6\n2 2\n-2";
        Files.write(tempFile, header.getBytes());

       IOException e =  assertThrows(IOException.class, () -> ppmDataAccess.read(tempFile.toAbsolutePath().toString()));
       assertEquals("Max value out of range in header", e.getMessage());
    }

    /* --------- invalid header -------- */

    @Test
    void testNoDimensionHeader() throws IOException {
        Path tempFile = tempDir.resolve("image.ppm");
        String header = "P6\n3\n";
        Files.write(tempFile, header.getBytes());

        IOException e = assertThrows(IOException.class, () -> ppmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Width or height is missing", e.getMessage());
    }

    @Test
    void testInvalidFormat() throws IOException {
        Path tempFile = tempDir.resolve("image.ppm");
        String header = "P9\n3 5\n";
        Files.write(tempFile, header.getBytes());

        IOException e = assertThrows(IOException.class, () -> ppmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Invalid format", e.getMessage());
    }

    @Test
    void testNegativeDimensionWidth() throws IOException {
        Path tempFile = tempDir.resolve("image.ppm");
        String header = "P6\n-2 -3\n255\n";
        Files.write(tempFile, header.getBytes());

        IOException e = assertThrows(IOException.class, () -> ppmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Invalid dimension in header", e.getMessage());
    }

    @Test
    void testNegativeDimensionHeight() throws IOException {
        Path tempFile = tempDir.resolve("image.ppm");
        String header = "P6\n2 -3\n255\n";
        Files.write(tempFile, header.getBytes());

        IOException e = assertThrows(IOException.class, () -> ppmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Invalid dimension in header", e.getMessage());
    }

    /* --------- valid header -------- */

    @Test
    void testHeaderCommentRead() throws IOException {
        Path tempFile = tempDir.resolve("image.ppm");
        String asciiData = "P3\n#comment\n2 2\n255\n34 23 30\n40 50 60\n70 80 90\n100 110 120\n";
        Files.write(tempFile, asciiData.getBytes());

        assertDoesNotThrow(() -> ppmDataAccess.read(tempFile.toAbsolutePath().toString()));
    }

    @Test
    void testBlankLineHeader() throws IOException {
        Path tempFile = tempDir.resolve("image.ppm");
        String asciiData = "P3\n \n2 2\n255\n34 23 30\n40 50 60\n70 80 90\n100 110 120\n";
        Files.write(tempFile, asciiData.getBytes());

        assertDoesNotThrow(() -> ppmDataAccess.read(tempFile.toAbsolutePath().toString()));
    }


    //+++++++++++++++++ WRITE TEST +++++++++++++++++

    /* --------- valid write -------- */
    @Test
    void testWrite8bitBinary() {
        Path tmpFile = tempDir.resolve("image.ppm");
        long[][] data = new long[][]{
                {1, 2, 3},
                {4, 5, 6},
        };
        ImageBusiness img = new ImageBusiness(
                data, tmpFile.toAbsolutePath().toString(),"P6",
                new ArgbThreeChannel(255));
        assertDoesNotThrow(()->ppmDataAccess.write(img));
    }
    @Test
    void testWrite16bitBinary() throws IOException {
        Path tempFile = tempDir.resolve("image.ppm");
        String header = "P6\n2 2\n65535\n";
        byte[] binaryData = new byte[]{
                127, 127, 0, 0, 0, 0,
                0, 0, 127, 127, 0, 0,
                0, 0, 0, 0, 127, 127,
                127, 127, 0, 0, 0, 0
        };

        byte[] fileContent = new byte[header.getBytes().length + binaryData.length];
        System.arraycopy(header.getBytes(), 0, fileContent, 0, header.getBytes().length);
        System.arraycopy(binaryData, 0, fileContent, header.getBytes().length, binaryData.length);
        Files.write(tempFile, fileContent);

        //devo leggere e scrivere nel caso del test write 16 bit perche la variabile maxvalue rimane scritta
        //nella classe dopo la lettura e viene usata nella scrittura
        ImageBusinessInterface img = ppmDataAccess.read(tempFile.toAbsolutePath().toString());
        assertDoesNotThrow(()->ppmDataAccess.write(img));

        byte[] actualFileContent = Files.readAllBytes(tempFile.toAbsolutePath());

        assertArrayEquals(fileContent, actualFileContent);

    }

    @Test
    void testWrite8bitAscii() {
        Path tmpFile = tempDir.resolve("image.ppm");
        long[][] data = new long[][]{
                {1, 2, 3},
                {4, 5, 6},
        };
        ImageBusiness img = new ImageBusiness(
                data, tmpFile.toAbsolutePath().toString(),"P3",
                new ArgbThreeChannel(255));
        assertDoesNotThrow(()->ppmDataAccess.write(img));
    }

    @Test
    void testWrite16bitAscii() throws IOException {

        Path tempFile = tempDir.resolve("image.ppm");
        String asciiData = "P3\n2 2\n65535\n65535 0 0\n0 0 65535\n0 0 0\n65535 65535 65535\n";
        Files.write(tempFile, asciiData.getBytes());

        ImageBusinessInterface img = ppmDataAccess.read(tempFile.toAbsolutePath().toString());
        assertDoesNotThrow(() -> ppmDataAccess.write(img));

        String actualContent = new String(Files.readAllBytes(tempFile));

        String normalizedExpected = normalizeAsciiContent(asciiData);
        String normalizedActual = normalizeAsciiContent(actualContent);

        assertEquals(normalizedExpected, normalizedActual);
    }

    //helper method to normalize ascii content by removing excess whitespace and standardizing line endings
    private String normalizeAsciiContent(String content) {
        return content
                .replaceAll("\\s+", " ")
                .replaceAll(" \n", "\n")
                .trim();
    }

    /* --------- invalid write -------- */

    @Test
    void testWriteToNonWritableFile() throws IOException {
        Path nonWritablePath = tempDir.resolve("image.ppm");
        Files.createFile(nonWritablePath);
        nonWritablePath.toFile().setWritable(false);

        long[][] data = new long[][]{
                {1, 2, 3},
                {4, 5, 6},
        };
        ImageBusiness img = new ImageBusiness(
                data, nonWritablePath.toAbsolutePath().toString(), "P3",
                new ArgbThreeChannel(255));

        IOException e = assertThrows(IOException.class, () -> ppmDataAccess.write(img));
        assertTrue(e.getMessage().contains("Unable to write to file: "));
        nonWritablePath.toFile().setWritable(true);
    }
}