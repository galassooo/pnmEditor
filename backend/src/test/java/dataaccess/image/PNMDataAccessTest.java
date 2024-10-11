package dataaccess.image;

import ch.supsi.business.Image.ImageBusiness;
import ch.supsi.dataaccess.PBMDataAccess;
import ch.supsi.dataaccess.PNMDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class PNMDataAccessTest {

    /* instance field */
    private PNMDataAccess pbmDataAccess;

    /**
     * Initialize a concrete instance for testing
     */
    @BeforeEach
    void setUp() {
        pbmDataAccess = PBMDataAccess.getInstance();
    }

    /* --------- Read Header - Valid Input --------- */

    /**
     * test reading a valid binary header
     */
    @Test
    void testReadHeaderValidBinaryFormat() throws IOException {
        String header = "P4\n# Commento\n4 4\n";
        InputStream is = new ByteArrayInputStream(header.getBytes());

        pbmDataAccess.readHeader(is);

        assertEquals(4, pbmDataAccess.width);
        assertEquals(4, pbmDataAccess.height);
        assertTrue(pbmDataAccess.isBinaryFormat());
    }

    /**
     * test reading a valid non-binary header (ASCII)
     */
    @Test
    void testReadHeaderValidNonBinaryFormat() throws IOException {
        String header = "P1\n# Commento\n4 4\n";
        InputStream is = new ByteArrayInputStream(header.getBytes());

        pbmDataAccess.readHeader(is);

        assertEquals(4, pbmDataAccess.width);
        assertEquals(4, pbmDataAccess.height);
        assertFalse(pbmDataAccess.isBinaryFormat());
    }

    /* --------- Read Header - Invalid Input --------- */

    /**
     * test invalid dimensions in the header
     */
    @Test
    void testReadHeaderInvalidDimensions() {
        String invalidHeader = "P4\n4\n";
        InputStream is = new ByteArrayInputStream(invalidHeader.getBytes());

        IOException exception = assertThrows(IOException.class, () -> pbmDataAccess.readHeader(is));
        assertEquals("L'header non contiene larghezza e altezza valide.", exception.getMessage());
    }

    /**
     * test negative width in header
     */
    @Test
    void testReadHeaderNegativeWidth() {
        String invalidHeader = "P4\n-1 0\n";
        InputStream is = new ByteArrayInputStream(invalidHeader.getBytes());

        assertThrows(IOException.class, () -> pbmDataAccess.readHeader(is));
    }

    /**
     * test negative height in header
     */
    @Test
    void testReadHeaderNegativeHeight() {
        String invalidHeader = "P4\n7 -1\n";
        InputStream is = new ByteArrayInputStream(invalidHeader.getBytes());

        assertThrows(IOException.class, () -> pbmDataAccess.readHeader(is));
    }

    /**
     * test invalid format in header
     */
    @Test
    void testInvalidFormat() {
        String header = "P9";
        InputStream is = new ByteArrayInputStream(header.getBytes());
        assertThrows(IOException.class, () -> pbmDataAccess.readHeader(is));
    }

    /**
     * test invalid comment format in header
     */
    @Test
    void testInvalidComment() {
        String header = "P4\n# Commento\n abc abc \n4 4\n";
        InputStream is = new ByteArrayInputStream(header.getBytes());

        assertThrows(NumberFormatException.class, () -> pbmDataAccess.readHeader(is));
    }

    /* --------- read - valid input --------- */

    /**
     * test reading a valid binary file
     */
    @Test
    void testReadBinaryFormat() throws IOException {
        String binaryFilePath = "/images/pnmBinary";
        ImageBusiness result = pbmDataAccess.read(binaryFilePath);
        assertNotNull(result);
    }

    /**
     * Test reading a valid ascii file
     */
    @Test
    void testReadAsciiFormat() throws IOException {
        String asciiFilePath = "/images/pnmAscii";
        ImageBusiness result = pbmDataAccess.read(asciiFilePath);
        assertNotNull(result);
    }

    /**
     * Test reading a real binary file and comparing dimensions
     */
    @Test
    void testReadRealFile() {
        String filePath = "/images/pnmBinary";

        try (InputStream is = getClass().getResourceAsStream(filePath)) {
            assertNotNull(is, "file not found: " + filePath);

            pbmDataAccess.readHeader(is);

            InputStream testIs = getClass().getResourceAsStream(filePath);
            assertNotNull(testIs, "file not found: " + filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(testIs));

            String expectedFormat = reader.readLine().trim();

            String dimensionLine;
            do {
                dimensionLine = reader.readLine().trim();
            } while (dimensionLine.startsWith("#") || dimensionLine.isEmpty());

            String[] dimensions = dimensionLine.split("\\s+");
            int expectedWidth = Integer.parseInt(dimensions[0]);
            int expectedHeight = Integer.parseInt(dimensions[1]);

            assertEquals(expectedFormat, pbmDataAccess.format);
            assertEquals(expectedWidth, pbmDataAccess.width);
            assertEquals(expectedHeight, pbmDataAccess.height);

        } catch (IOException e) {
            fail("Error reading file: " + e.getMessage());
        }
    }

    /* --------- read - invalid input --------- */

    /**
     * test reading a non-existent file
     */
    @Test
    public void testReadFileNotFound() {
        assertThrows(IOException.class, () -> pbmDataAccess.read("nonexistent_file_path"));
    }

    /**
     * test reading an invalid binary file
     */
    @Test
    void testReadBinaryInvalidInput() {
        assertThrows(IOException.class, () -> pbmDataAccess.read("/images/incompleteBin"));
    }

    /**
     * test reading an invalid acii file
     */
    @Test
    void testReadAsciiInvalidInput() {
        assertThrows(IOException.class, () -> pbmDataAccess.read("/images/incompleteAscii"));
    }

    /* --------- getters and setters --------- */

    /**
     * test setter methods for width, height, and format
     */
    @Test
    void testSetters() {
        pbmDataAccess.setWidth(5);
        pbmDataAccess.setHeight(5);
        pbmDataAccess.setFormat("P4");

        assertEquals(5, pbmDataAccess.width);
        assertEquals(5, pbmDataAccess.height);
        assertEquals("P4", pbmDataAccess.format);
    }
}
