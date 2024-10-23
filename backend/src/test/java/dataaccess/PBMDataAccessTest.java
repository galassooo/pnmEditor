package dataaccess;

import ch.supsi.application.image.ImageBusinessInterface;
import ch.supsi.business.image.ImageBusiness;
import ch.supsi.business.strategy.SingleBit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ch.supsi.dataaccess.image.PBMDataAccess;
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

    //++++++++++++Write ++++++++++++
    @Test
    void testWriteBinary() {
        Path tmpFile = tempDir.resolve("image.pbm");
        long[][] data = new long[][]{
                {1, 0, 1},
                {0, 1, 0},
        };
        ImageBusinessInterface img = new ImageBusiness(
                data, tmpFile.toAbsolutePath().toString(), "P4",
                new SingleBit());
        assertDoesNotThrow(() -> pbmDataAccess.write(img));
    }

    @Test
    void testWriteAscii() {
        Path tmpFile = tempDir.resolve("image.pbm");
        long[][] data = new long[][]{
                {1, 0, 1},
                {0, 1, 0},
        };
        ImageBusinessInterface img = new ImageBusiness(
                data, tmpFile.toAbsolutePath().toString(), "P1",
                new SingleBit());
        assertDoesNotThrow(() -> pbmDataAccess.write(img));
    }
    @Test
    void testWriteBinaryContentNoPadding() throws IOException {
        Path tempFile = tempDir.resolve("image.pbm");
        String header = "P4\n8 2\n";

        byte[] expectedBinaryData = new byte[]{
                (byte) 0b10101010,
                (byte) 0b01010101
        };

        byte[] fileContent = new byte[header.getBytes().length + expectedBinaryData.length];
        System.arraycopy(header.getBytes(), 0, fileContent, 0, header.getBytes().length);
        System.arraycopy(expectedBinaryData, 0, fileContent, header.getBytes().length, expectedBinaryData.length);

        Files.write(tempFile, fileContent);

        ImageBusinessInterface img = pbmDataAccess.read(tempFile.toAbsolutePath().toString());
        assertDoesNotThrow(() -> pbmDataAccess.write(img));

        byte[] actualFileContent = Files.readAllBytes(tempFile.toAbsolutePath());
        assertArrayEquals(fileContent, actualFileContent);
    }


    @Test
    void testWriteAsciiContent() throws IOException {
        Path tempFile = tempDir.resolve("image.pbm");
        String asciiData = "P1\n2 2\n1 0\n0 1\n";
        Files.write(tempFile, asciiData.getBytes());

        ImageBusinessInterface img = pbmDataAccess.read(tempFile.toAbsolutePath().toString());
        assertDoesNotThrow(() -> pbmDataAccess.write(img));

        String actualContent = new String(Files.readAllBytes(tempFile));

        String normalizedExpected = normalizeAsciiContent(asciiData);
        String normalizedActual = normalizeAsciiContent(actualContent);

        assertEquals(normalizedExpected, normalizedActual);
    }

    private String normalizeAsciiContent(String content) {
        return content
                .replaceAll("\\s+", " ")
                .replaceAll(" \n", "\n")
                .trim();
    }

    /* --------- invalid write -------- */

    @Test
    void testWriteToNonWritableFile() throws IOException {
        Path nonWritablePath = tempDir.resolve("image.pbm");
        Files.createFile(nonWritablePath);
        nonWritablePath.toFile().setWritable(false);

        long[][] data = new long[][]{
                {1, 0, 1},
                {0, 1, 0},
        };
        ImageBusinessInterface img = new ImageBusiness(
                data, nonWritablePath.toAbsolutePath().toString(), "P1",
                new SingleBit());

        IOException e = assertThrows(IOException.class, () -> pbmDataAccess.write(img));
        assertTrue(e.getMessage().contains("Unable to write to file: "));
        nonWritablePath.toFile().setWritable(true);
    }
}


