package dataaccess.image;

import ch.supsi.application.Image.ImageBusinessInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ch.supsi.dataaccess.PBMDataAccess;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PBMDataAccessTest {

    /* instance field */
    private PBMDataAccess pbmDataAccess;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        pbmDataAccess = PBMDataAccess.getInstance();
    }

    /* --------- singleton --------- */
    @Test
    void testSingleton() {
        PBMDataAccess pbmDataAccess2 = PBMDataAccess.getInstance();
        assertEquals(pbmDataAccess2, pbmDataAccess);
    }

    /* --------- valid input --------- */

    @Test
    void testProcessBinaryValidInput() throws IOException {
        Path tempFile = tempDir.resolve("image.pbm");

        byte[] header = "P4\n4 4\n".getBytes();
        byte[] binaryData = new byte[]{
                (byte) 0b10100000,
                (byte) 0b01010000,
                (byte) 0b11000000,
                (byte) 0b00110000
        };

        long[][] expectedMatrix = {
                {0xFF000000L, 0xFFFFFFFFL, 0xFF000000L, 0xFFFFFFFFL},
                {0xFFFFFFFFL, 0xFF000000L, 0xFFFFFFFFL, 0xFF000000L},
                {0xFF000000L, 0xFF000000L, 0xFFFFFFFFL, 0xFFFFFFFFL},
                {0xFFFFFFFFL, 0xFFFFFFFFL, 0xFF000000L, 0xFF000000L}
        };

        byte[] fileContent = new byte[header.length + binaryData.length];
        System.arraycopy(header, 0, fileContent, 0, header.length);
        System.arraycopy(binaryData, 0, fileContent, header.length, binaryData.length);

        Files.write(tempFile, fileContent);

        ImageBusinessInterface img = pbmDataAccess.read(tempFile.toAbsolutePath().toString());
        assertArrayEquals(expectedMatrix, img.getPixels());
    }

    @Test
    void testProcessBinaryWithPadding() throws IOException {
        Path tempFile = tempDir.resolve("image.pbm");

        byte[] header = "P4\n10 4\n".getBytes();
        byte[] binaryData = new byte[]{
                (byte) 0b10101010,
                (byte) 0b10000000,
                (byte) 0b01010101,
                (byte) 0b11000000,
                (byte) 0b11111111,
                (byte) 0b00000000,
                (byte) 0b00000000,
                (byte) 0b11111111
        };

        byte[] fileContent = new byte[header.length + binaryData.length];
        System.arraycopy(header, 0, fileContent, 0, header.length);
        System.arraycopy(binaryData, 0, fileContent, header.length, binaryData.length);

        Files.write(tempFile, fileContent);

        long[][] expectedMatrix = {
                {0xFF000000L, 0xFFFFFFFFL, 0xFF000000L, 0xFFFFFFFFL, 0xFF000000L, 0xFFFFFFFFL, 0xFF000000L, 0xFFFFFFFFL, 0xFF000000L, 0xFFFFFFFFL},
                {0xFFFFFFFFL, 0xFF000000L, 0xFFFFFFFFL, 0xFF000000L, 0xFFFFFFFFL, 0xFF000000L, 0xFFFFFFFFL, 0xFF000000L, 0xFF000000L, 0xFF000000L},
                {0xFF000000L, 0xFF000000L, 0xFF000000L, 0xFF000000L, 0xFF000000L, 0xFF000000L, 0xFF000000L, 0xFF000000L, 0xFFFFFFFFL, 0xFFFFFFFFL},
                {0xFFFFFFFFL, 0xFFFFFFFFL, 0xFFFFFFFFL, 0xFFFFFFFFL, 0xFFFFFFFFL, 0xFFFFFFFFL, 0xFFFFFFFFL, 0xFFFFFFFFL, 0xFF000000L, 0xFF000000L}
        };

        ImageBusinessInterface img = pbmDataAccess.read(tempFile.toAbsolutePath().toString());

        long[][] actualMatrix = img.getPixels();
        assertArrayEquals(expectedMatrix, actualMatrix);
    }

    @Test
    void testProcessAsciiValidInput() throws IOException {
        Path tempFile = tempDir.resolve("image.pbm");
        String header = "P1\n4 4\n";

        String asciiData =
                """
                        0 1 1 0
                        1 0 0 1
                        0 1 1 0
                        1 0 0 1
                        """;
        Files.write(tempFile, (header + asciiData).getBytes());

        long[][] expectedMatrix = {
                {0xFFFFFFFFL, 0xFF000000L, 0xFF000000L, 0xFFFFFFFFL},
                {0xFF000000L, 0xFFFFFFFFL, 0xFFFFFFFFL, 0xFF000000L},
                {0xFFFFFFFFL, 0xFF000000L, 0xFF000000L, 0xFFFFFFFFL},
                {0xFF000000L, 0xFFFFFFFFL, 0xFFFFFFFFL, 0xFF000000L}
        };

        ImageBusinessInterface img = pbmDataAccess.read(tempFile.toAbsolutePath().toString());

        long[][] actualMatrix = img.getPixels();

        assertArrayEquals(expectedMatrix, actualMatrix);

    }

    /* --------- invalid input --------- */

    @Test
    void testProcessBinaryInvalidInput() throws IOException {
        Path tempFile = tempDir.resolve("image.pbm");

        byte[] header = "P4\n4 4\n".getBytes();
        byte[] binaryData = new byte[]{
                (byte) 0b10100000,
                (byte) 0b01010000,
                (byte) 0b11000000,
        };

        byte[] fileContent = new byte[header.length + binaryData.length];
        System.arraycopy(header, 0, fileContent, 0, header.length);
        System.arraycopy(binaryData, 0, fileContent, header.length, binaryData.length);

        Files.write(tempFile, fileContent);

        IOException e = assertThrows(IOException.class, () -> pbmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Insufficient data in pbm binary file", e.getMessage());
    }

    @Test
    void testProcessAsciiInvalidInput() throws IOException {
        Path tempFile = tempDir.resolve("image.pbm");
        String header = "P1\n4 4\n";

        String asciiData =
                """
                        0 1 1 0
                        1 0 0 1
                        0 1 1 0
                        1 0 0
                        """;

        Files.write(tempFile, (header + asciiData).getBytes());

        IOException e = assertThrows(IOException.class, () -> pbmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Insufficient data in pbm ascii file", e.getMessage());
    }

    /* ---------- invalid header ---------- */

    @Test
    void testNoDimensionHeader() throws IOException {
        Path tempFile = tempDir.resolve("image.pbm");
        String header = "P1\n4\n";

        Files.write(tempFile, (header).getBytes());

        IOException e = assertThrows(IOException.class, () -> pbmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Width or height is missing", e.getMessage());
    }

    @Test
    void testNegativeWidthDimensionHeader() throws IOException {
        Path tempFile = tempDir.resolve("image.pbm");
        String header = "P1\n-4 8\n";

        Files.write(tempFile, (header).getBytes());

        IOException e =assertThrows(IOException.class, () -> pbmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Invalid dimension in header", e.getMessage());
    }

    @Test
    void testNegativeHeightDimensionHeader() throws IOException {
        Path tempFile = tempDir.resolve("image.pbm");
        String header = "P1\n4 -8\n";

        Files.write(tempFile, (header).getBytes());

        IOException e =assertThrows(IOException.class, () -> pbmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Invalid dimension in header", e.getMessage());
    }

    @Test
    void testInvalidFormat() throws IOException {
        Path tempFile = tempDir.resolve("image.pbm");
        String header = "P9\n \n1 1\n";

        Files.write(tempFile, (header).getBytes());

        IOException e = assertThrows(IOException.class, ()->pbmDataAccess.read(tempFile.toAbsolutePath().toString()));
        assertEquals("Invalid format", e.getMessage());
    }

    /* ---------- valid header ---------- */
    @Test
    void testBlankLineHeader() throws IOException {
        Path tempFile = tempDir.resolve("asciiImage.pbm");
        String header = "P1\n \n1 1\n";
        String data = "1";

        Files.write(tempFile, (header + data).getBytes());

        assertDoesNotThrow(()->pbmDataAccess.read(tempFile.toAbsolutePath().toString()));
    }

    @Test
    void testHeaderComment() throws IOException {
        Path tempFile = tempDir.resolve("asciiImage.pbm");
        String header = "P1\n#comment \n1 1\n";
        String data = "1";

        Files.write(tempFile, (header + data).getBytes());

        assertDoesNotThrow(()->pbmDataAccess.read(tempFile.toAbsolutePath().toString()));
    }
}

