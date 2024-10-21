package dataaccess;

import ch.supsi.business.image.ImageBusiness;
import ch.supsi.business.strategy.ArgbSingleChannel;
import ch.supsi.dataaccess.image.PGMDataAccess;
import ch.supsi.application.image.ImageBusinessInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class PGMDataAccessTest {

    /* instance field */
    private PGMDataAccess pgmDataAccess;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        pgmDataAccess = PGMDataAccess.getInstance();
    }

    /* --------- singleton --------- */
    @Test
    void testSingleton() {
        PGMDataAccess pgmDataAccess2 = PGMDataAccess.getInstance();
        assertEquals(pgmDataAccess2, pgmDataAccess);
    }

    /* --------- valid input --------- */

    @Test
    void testProcessBinary8BitValidInput() throws IOException {
        Path tempFile = tempDir.resolve("image.pgm");
        String header = "P5\n4 4\n255\n";
        byte[] binaryData = new byte[]{
                10, 20, 30, 40,
                50, 60, 70, 80,
                90, 100, 110, 120,
                (byte) 130, (byte) 140, (byte) 150, (byte) 160
        };

        byte[] fileContent = new byte[header.getBytes().length + binaryData.length];
        System.arraycopy(header.getBytes(), 0, fileContent, 0, header.getBytes().length);
        System.arraycopy(binaryData, 0, fileContent, header.getBytes().length, binaryData.length);
        Files.write(tempFile, fileContent);

        long[][] expectedMatrix = {
                {0xFF0A0A0AL, 0xFF141414L, 0xFF1E1E1EL, 0xFF282828L},
                {0xFF323232L, 0xFF3C3C3CL, 0xFF464646L, 0xFF505050L},
                {0xFF5A5A5AL, 0xFF646464L, 0xFF6E6E6EL, 0xFF787878L},
                {0xFF828282L, 0xFF8C8C8CL, 0xFF969696L, 0xFFA0A0A0L}
        };

        ImageBusinessInterface img = pgmDataAccess.read(tempFile.toAbsolutePath().toString());
        assertArrayEquals(expectedMatrix, img.getPixels());
    }

    @Test
    void testProcessBinary16BitRedBlueRows() throws IOException {
        Path tempFile = tempDir.resolve("image.pgm");
        String header = "P5\n4 2\n65535\n";

        byte[] binaryData = new byte[]{
                (byte) 255, (byte) 255, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, (byte) 255, (byte) 255, 0, 0
        };

        byte[] fileContent = new byte[header.getBytes().length + binaryData.length];
        System.arraycopy(header.getBytes(), 0, fileContent, 0, header.getBytes().length);
        System.arraycopy(binaryData, 0, fileContent, header.getBytes().length, binaryData.length);
        Files.write(tempFile, fileContent);

        long[][] expectedMatrix = {
                {0xFFFFFFFFL, 0xFF000000L, 0xFF000000L, 0xFF000000L},
                {0xFF000000L, 0xFF000000L, 0xFFFFFFFFL, 0xFF000000L}
        };

        ImageBusinessInterface img = pgmDataAccess.read(tempFile.toAbsolutePath().toString());
        assertArrayEquals(expectedMatrix, img.getPixels());
    }

    @Test
    void testProcessBinaryWithPadding() throws IOException {
        Path tempFile = tempDir.resolve("image.pgm");
        String header = "P5\n3 2\n255\n";
        byte[] binaryData = new byte[]{
                10, 20, 30,
                40, 50, 60,
                0, 0, 0
        };

        Files.write(tempFile, (header + new String(binaryData)).getBytes());

        long[][] expectedMatrix = {
                {0xFF0A0A0AL, 0xFF141414L, 0xFF1E1E1EL},
                {0xFF282828L, 0xFF323232L, 0xFF3C3C3CL}
        };


        ImageBusinessInterface img = pgmDataAccess.read(tempFile.toAbsolutePath().toString());
        assertArrayEquals(expectedMatrix, img.getPixels());
    }

    @Test
    void testProcessAscii8bitValidInput() throws IOException {
        Path tempFile = tempDir.resolve("image.pgm");
        String asciiData = "P2\n4 4\n255\n10 20 30 40\n50 60 70 80\n90 100 110 120\n130 140 150 160\n";

        Files.write(tempFile, asciiData.getBytes());

        long[][] expectedMatrix = {
                {0xFF0A0A0AL, 0xFF141414L, 0xFF1E1E1EL, 0xFF282828L},
                {0xFF323232L, 0xFF3C3C3CL, 0xFF464646L, 0xFF505050L},
                {0xFF5A5A5AL, 0xFF646464L, 0xFF6E6E6EL, 0xFF787878L},
                {0xFF828282L, 0xFF8C8C8CL, 0xFF969696L, 0xFFA0A0A0L}
        };


        ImageBusinessInterface img = pgmDataAccess.read(tempFile.toAbsolutePath().toString());
        assertArrayEquals(expectedMatrix, img.getPixels());
    }
    @Test
    void testProcessAscii16bitValidInput() throws IOException {
        Path tempFile = tempDir.resolve("image.pgm");

        String asciiData = "P2\n4 2\n65535\n65535 65535 65535 65535\n0 0 0 0\n";
        Files.write(tempFile, asciiData.getBytes());

        long[][] expectedMatrix = {
                {0xFFFFFFFFL, 0xFFFFFFFFL, 0xFFFFFFFFL, 0xFFFFFFFFL},
                {0xFF000000L, 0xFF000000L, 0xFF000000L, 0xFF000000L}
        };

        ImageBusinessInterface img = pgmDataAccess.read(tempFile.toAbsolutePath().toString());
        assertArrayEquals(expectedMatrix, img.getPixels());
    }

    /* --------- invalid input --------- */

    @Test
    void testProcessBinary8BitInvalidInput() throws IOException {
        Path tempFile = tempDir.resolve("image.pgm");
        String header = "P5\n4 4\n255\n";
        byte[] incompleteData = {10, 20};

        Files.write(tempFile, (header + new String(incompleteData)).getBytes());

        IOException e = assertThrows(IOException.class, () -> pgmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Insufficient data in binary pmg file", e.getMessage());
    }

    @Test
    void testProcessBinary16BitInValidInput() throws IOException {
        Path tempFile = tempDir.resolve("image.pgm");
        String header = "P5\n4 4\n65535\n";
        byte[] binaryData = new byte[]{
                10, 20, 30, 40,
                50, 60, 70, 80,
                90, 100, 110, 120,
                2, 34, 21, 12
        };

        //combine header and  data
        byte[] fileContent = new byte[header.getBytes().length + binaryData.length];
        System.arraycopy(header.getBytes(), 0, fileContent, 0, header.getBytes().length);
        System.arraycopy(binaryData, 0, fileContent, header.getBytes().length, binaryData.length);
        Files.write(tempFile, fileContent);

        IOException e = assertThrows(IOException.class, () -> pgmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Insufficient data in binary pmg file for a 16 bit image", e.getMessage());
    }

    @Test
    void testProcessBinary8BitInvalidColorInput() throws IOException {
        Path tempFile = tempDir.resolve("image.pgm");
        String header = "P5\n4 4\n100\n";
        byte[] binaryData = new byte[]{
                (byte) 200, 100, 30, 40,
                50, 60, 70, 80,
                90, 100, 110, 120,
                (byte) 130, (byte) 140, (byte) 150, (byte) 160
        };

        byte[] fileContent = new byte[header.getBytes().length + binaryData.length];
        System.arraycopy(header.getBytes(), 0, fileContent, 0, header.getBytes().length);
        System.arraycopy(binaryData, 0, fileContent, header.getBytes().length, binaryData.length);
        Files.write(tempFile, fileContent);

        IOException e = assertThrows(IOException.class, () -> pgmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("gray pixel value out of range in binary pmg file", e.getMessage());
    }

    @Test
    void testProcessBinary16BitEOFOnLowByte() throws IOException {
        Path tempFile = tempDir.resolve("image.pgm");
        String header = "P5\n4 4\n65535\n";

        byte[] binaryData = new byte[]{
                0, 10, 0, 20, 0, 30, 0, 40,
                0, 50, 0, 60, 0, 70, 0, 80,
                0, 90, 0, 100, 0, 110, 0, 120,
                0, (byte) 130, 0, (byte) 140, 0,
        };

        byte[] fileContent = new byte[header.getBytes().length + binaryData.length];
        System.arraycopy(header.getBytes(), 0, fileContent, 0, header.getBytes().length);
        System.arraycopy(binaryData, 0, fileContent, header.getBytes().length, binaryData.length);
        Files.write(tempFile, fileContent);

        IOException e = assertThrows(IOException.class, () -> pgmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Insufficient data in binary pmg file for a 16 bit image", e.getMessage());
    }

    @Test
    void testProcessBinary16BitEOFOnHighByte() throws IOException {
        Path tempFile = tempDir.resolve("image.pgm");
        String header = "P5\n4 4\n65535\n";

        // Construct binary data where the last 16-bit pixel is missing the high byte
        byte[] incompleteBinaryData = new byte[]{
                0, 10, 0, 20, 0, 30, 0, 40,
                0, 50, 0, 60, 0, 70, 0, 80,
                0, 90, 0, 100,
        };

        // Combine header and incomplete binary data
        byte[] fileContent = new byte[header.getBytes().length + incompleteBinaryData.length];
        System.arraycopy(header.getBytes(), 0, fileContent, 0, header.getBytes().length);
        System.arraycopy(incompleteBinaryData, 0, fileContent, header.getBytes().length, incompleteBinaryData.length);
        Files.write(tempFile, fileContent);

        // Assert that the read method throws an IOException with the expected message
        IOException e = assertThrows(IOException.class, () -> pgmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Insufficient data in binary pmg file for a 16 bit image", e.getMessage());
    }


    @Test
    void testProcessAsciiInvalidInput() throws IOException {
        Path tempFile = tempDir.resolve("image.pgm");
        String asciiData = "P2\n4 4\n255\n10 20\n30";

        Files.write(tempFile, asciiData.getBytes());

        IOException e = assertThrows(IOException.class, () -> pgmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Invalid or missing data in ascii PGM file",e.getMessage());
    }

    @Test
    void testProcessAsciiInvalidColorInput() throws IOException {
        Path tempFile = tempDir.resolve("image.pgm");
        String asciiData = "P2\n4 4\n255\n2556 20\n30";

        Files.write(tempFile, asciiData.getBytes());

        IOException e = assertThrows(IOException.class, () -> pgmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("gray pixel value out of range in binary pmg file", e.getMessage());
    }

    @Test
    void testProcessAsciiNegativeColorInput() throws IOException {
        Path tempFile = tempDir.resolve("image.pgm");
        String asciiData = "P2\n4 4\n255\n-3 20\n30";

        Files.write(tempFile, asciiData.getBytes());

        IOException e = assertThrows(IOException.class, () -> pgmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("gray pixel value out of range in binary pmg file", e.getMessage());
    }

    /* --------- invalid maxValue --------- */

    @Test
    void testNegativeMaxValue() throws IOException {
        Path tempFile = tempDir.resolve("image.pgm");
        String header = "P5\n4 4\n-1\n";

        Files.write(tempFile, header.getBytes());

        IOException e = assertThrows(IOException.class, () -> pgmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Max value out of range in header", e.getMessage());
    }

    @Test
    void testOverMaximumMaxValue() throws IOException {
        Path tempFile = tempDir.resolve("image.pgm");
        String header = "P5\n4 4\n65536\n";

        Files.write(tempFile, header.getBytes());

        IOException e = assertThrows(IOException.class, () -> pgmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Max value out of range in header", e.getMessage());
    }

    @Test
    void testMissingMaxValue() throws IOException {
        Path tempFile = tempDir.resolve("image.pgm");
        String header = "P5\n2 4\n2 3 4 5 3 4 5 6 ";

        Files.write(tempFile, header.getBytes());

        IOException e = assertThrows(IOException.class, () -> pgmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Max value is invalid in header", e.getMessage());
    }

    /* --------- header invalid -------- */

    @Test
    void testNoDimensionHeader() throws IOException {
        Path tempFile = tempDir.resolve("image.pbm");
        String header = "P1\n4\n";

        Files.write(tempFile, (header).getBytes());

        IOException e = assertThrows(IOException.class, () -> pgmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Width or height is missing", e.getMessage());
    }

    @Test
    void testNegativeWidthDimensionHeader() throws IOException {
        Path tempFile = tempDir.resolve("image.pbm");
        String header = "P1\n-4 8\n";

        Files.write(tempFile, (header).getBytes());

        IOException e =assertThrows(IOException.class, () -> pgmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Invalid dimension in header", e.getMessage());
    }

    @Test
    void testNegativeHeightDimensionHeader() throws IOException {
        Path tempFile = tempDir.resolve("image.pbm");
        String header = "P1\n4 -8\n";

        Files.write(tempFile, (header).getBytes());

        IOException e =assertThrows(IOException.class, () -> pgmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Invalid dimension in header", e.getMessage());
    }

    @Test
    void testInvalidFormat() throws IOException {
        Path tempFile = tempDir.resolve("image.pbm");
        String header = "P9\n \n1 1\n";

        Files.write(tempFile, (header).getBytes());

        IOException e = assertThrows(IOException.class, ()->pgmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Invalid format", e.getMessage());
    }

    /* --------- valid header -------- */
    @Test
    void testBlankLineHeader() throws IOException {
        Path tempFile = tempDir.resolve("image.pbm");
        String header = "P1\n \n1 1\n255\n";
        String data = "10";

        Files.write(tempFile, (header + data).getBytes());

        long[][] expected = {{0xFF0A0A0AL}};

        ImageBusinessInterface img = pgmDataAccess.read(tempFile.toAbsolutePath().toString());
        assertArrayEquals(expected, img.getPixels());
    }


    @Test
    void testHeaderComment() throws IOException {
        Path tempFile = tempDir.resolve("image.pbm");
        String header = "P1\n#comment \n1 1\n255\n";
        String data = "10";

        Files.write(tempFile, (header + data).getBytes());

        long[][] expected = {{0xFF0A0A0AL}};

        ImageBusinessInterface img = pgmDataAccess.read(tempFile.toAbsolutePath().toString());
        assertArrayEquals(expected, img.getPixels());
    }

    @Test
    void testWrite8bitBinary() {
        Path tmpFile = tempDir.resolve("image.pgm");
        long[][] data = new long[][]{
                {1, 2, 3},
                {4, 5, 6},
        };
        ImageBusinessInterface img = new ImageBusiness(
                data, tmpFile.toAbsolutePath().toString(), "P5",
                new ArgbSingleChannel(255));
        assertDoesNotThrow(() -> pgmDataAccess.write(img));
    }

    @Test
    void testWrite16bitBinary() throws IOException {
        Path tempFile = tempDir.resolve("image.pgm");
        String header = "P5\n2 2\n65535\n";
        byte[] binaryData = new byte[]{
                (byte) 255, (byte) 255, 0, 0,
                0, 0, (byte) 255, (byte) 255
        };

        byte[] fileContent = new byte[header.getBytes().length + binaryData.length];
        System.arraycopy(header.getBytes(), 0, fileContent, 0, header.getBytes().length);
        System.arraycopy(binaryData, 0, fileContent, header.getBytes().length, binaryData.length);
        Files.write(tempFile, fileContent);

        ImageBusinessInterface img = pgmDataAccess.read(tempFile.toAbsolutePath().toString());
        assertDoesNotThrow(() -> pgmDataAccess.write(img));

        byte[] actualFileContent = Files.readAllBytes(tempFile.toAbsolutePath());
        assertArrayEquals(fileContent, actualFileContent);
    }

    @Test
    void testWrite8bitAscii() {
        Path tmpFile = tempDir.resolve("image.pgm");
        long[][] data = new long[][]{
                {1, 2, 3},
                {4, 5, 6},
        };
        ImageBusinessInterface img = new ImageBusiness(
                data, tmpFile.toAbsolutePath().toString(), "P2",
                new ArgbSingleChannel(255));
        assertDoesNotThrow(() -> pgmDataAccess.write(img));
    }

    @Test
    void testWrite16bitAscii() throws IOException {
        Path tempFile = tempDir.resolve("image.pgm");
        String asciiData = "P2\n2 2\n65535\n65535 0\n0 65535\n";
        Files.write(tempFile, asciiData.getBytes());

        ImageBusinessInterface img = pgmDataAccess.read(tempFile.toAbsolutePath().toString());
        assertDoesNotThrow(() -> pgmDataAccess.write(img));

        String actualContent = new String(Files.readAllBytes(tempFile));

        String normalizedExpected = normalizeAsciiContent(asciiData);
        String normalizedActual = normalizeAsciiContent(actualContent);

        assertEquals(normalizedExpected, normalizedActual);
    }

    // Helper method to normalize ASCII content by removing excess whitespace and standardizing line endings
    private String normalizeAsciiContent(String content) {
        return content
                .replaceAll("\\s+", " ")
                .replaceAll(" \n", "\n")
                .trim();
    }

    /* --------- invalid write -------- */

    @Test
    void testWriteToNonWritableFile() throws IOException {
        Path nonWritablePath = tempDir.resolve("image.pgm");
        Files.createFile(nonWritablePath);
        nonWritablePath.toFile().setWritable(false);

        long[][] data = new long[][]{
                {1, 2, 3},
                {4, 5, 6},
        };
        ImageBusinessInterface img = new ImageBusiness(
                data, nonWritablePath.toAbsolutePath().toString(), "P2",
                new ArgbSingleChannel(255));

        IOException e = assertThrows(IOException.class, () -> pgmDataAccess.write(img));
        assertTrue(e.getMessage().contains("Unable to write to file: "));
        nonWritablePath.toFile().setWritable(true);
    }
}
