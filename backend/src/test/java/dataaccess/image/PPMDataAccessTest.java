package dataaccess.image;

import ch.supsi.business.Image.ImageBusiness;
import ch.supsi.dataaccess.PPMDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PPMDataAccessTest {

    private PPMDataAccess ppmDataAccess;

    /**
     * Initializes the PPMDataAccess singleton instance for testing.
     */
    @BeforeEach
    void setUp() {
        ppmDataAccess = PPMDataAccess.getInstance();
        ppmDataAccess.setWidth(2);
        ppmDataAccess.setHeight(2);
    }

    /* --------- Valid Input Tests --------- */

    /**
     * Tests 8-bit binary PPM processing with valid input.
     */
    @Test
    void testProcessBinary8BitValidInput() throws IOException {
        InputStream header = new ByteArrayInputStream("255\n".getBytes());
        byte[] binaryData = new byte[]{
                (byte) 255, (byte) 128, (byte) 64,
                (byte) 32, (byte) 16, (byte) 8,
                (byte) 200, (byte) 100, (byte) 50,
                (byte) 25, (byte) 12, (byte) 6
        };
        InputStream data = new ByteArrayInputStream(binaryData);
        InputStream is = new SequenceInputStream(header, data);
        ImageBusiness result = ppmDataAccess.processBinary(is);

        long[][] expectedMatrix = {
                {(255 << 16) | (128 << 8) | 64, (32 << 16) | (16 << 8) | 8},
                {(200 << 16) | (100 << 8) | 50, (25 << 16) | (12 << 8) | 6}
        };
        assertArrayEquals(expectedMatrix, ppmDataAccess.originalMatrix);
        assertNotNull(result);
    }

    /**
     * Tests 16-bit binary PPM processing with valid input.
     */
    @Test
    void testProcessBinary16BitValidInput() throws IOException {

        InputStream header = new ByteArrayInputStream("65535\n".getBytes());
        byte[] binaryData = new byte[]{
                0, (byte) 255, 0, (byte) 128, 0, 64,
                0, 32, 0, 16, 0, 8,
                0, (byte) 200, 0, 100, 0, 50,
                0, 25, 0, 12, 0, 6
        };
        InputStream data = new ByteArrayInputStream(binaryData);
        InputStream is = new SequenceInputStream(header, data);

        ImageBusiness result = ppmDataAccess.processBinary(is);

        long[][] expectedMatrix = {
                {((long) 255 << 32) | ((long) 128 << 16) | 64, ((long) 32 << 32) | ((long) 16 << 16) | 8},
                {((long) 200 << 32) | ((long) 100 << 16) | 50, ((long) 25 << 32) | ((long) 12 << 16) | 6}
        };

        System.out.println(Arrays.deepToString(ppmDataAccess.originalMatrix));

        assertArrayEquals(expectedMatrix, ppmDataAccess.originalMatrix);
        assertNotNull(result);
    }


    /**
     * Tests valid ASCII PPM input with max color value 255.
     */
    @Test
    void testProcessAscii8BitValidInput() throws IOException {
        InputStream header = new ByteArrayInputStream("255\n".getBytes());
        String asciiData = "255 128 64 32 16 8 200 100 50 25 12 6\n";
        InputStream data = new ByteArrayInputStream(asciiData.getBytes());

        InputStream is = new SequenceInputStream(header, data);
        ImageBusiness result = ppmDataAccess.processAscii(is);

        long[][] expectedMatrix = {
                {(255 << 16) | (128 << 8) | 64, (32 << 16) | (16 << 8) | 8},
                {(200 << 16) | (100 << 8) | 50, (25 << 16) | (12 << 8) | 6}
        };

        assertArrayEquals(expectedMatrix, ppmDataAccess.originalMatrix);
        assertNotNull(result);
    }

    @Test
    void testProcessAscii16BitValidInput() throws IOException {
        InputStream header = new ByteArrayInputStream("65535\n".getBytes());
        String asciiData = "340 500 80 32 16 340" + " 200 430 50 279 12 6\n";
        InputStream data = new ByteArrayInputStream(asciiData.getBytes());

        InputStream is = new SequenceInputStream(header, data);
        ImageBusiness result = ppmDataAccess.processAscii(is);

        long[][] expectedMatrix = {
                {((long) 340 << 32) | ((long) 500 << 16) | 80, ((long) 32 << 32) | ((long) 16 << 16) | 340},
                {((long) 200 << 32) | ((long) 430 << 16) | 50, ((long) 279 << 32) | ((long) 12 << 16) | 6}
        };

        assertArrayEquals(expectedMatrix, ppmDataAccess.originalMatrix);
        assertNotNull(result);
    }



    /* --------- Invalid Input Tests --------- */

    /* binary */

    /**
     * Tests 16-bit binary PPM with insufficient data  (read interrupted on green channel)
     */
    @Test
    void testProcessBinary16BitInsufficientDataOnGreen() {
        InputStream header = new ByteArrayInputStream("65535\n".getBytes());
        InputStream data = new ByteArrayInputStream(new byte[]{0, (byte) 255, 0});
        InputStream is = new SequenceInputStream(header, data);

        IOException e = assertThrows(IOException.class, () -> ppmDataAccess.processBinary(is));
        assertEquals("Insufficient data in binary ppm file", e.getMessage());
    }

    /**
     * Tests 16-bit binary PPM with insufficient data  (read interrupted on blue channel)
     */
    @Test
    void testProcessBinary16BitInsufficientDataOnBlue() {
        InputStream header = new ByteArrayInputStream("65535\n".getBytes());
        InputStream data = new ByteArrayInputStream(new byte[]{0, (byte) 255, 0, (byte) 128});
        InputStream is = new SequenceInputStream(header, data);

        IOException e = assertThrows(IOException.class, () -> ppmDataAccess.processBinary(is));
        assertEquals("Insufficient data in binary ppm file", e.getMessage());
    }

    /**
     * Tests 16-bit binary PPM with insufficient data. (read interrupted on red channel)
     */
    @Test
    void testProcessBinary16BitInsufficientDataOnRed() {
        InputStream header = new ByteArrayInputStream("65535\n".getBytes());
        InputStream data = new ByteArrayInputStream(new byte[]{});
        InputStream is = new SequenceInputStream(header, data);

        IOException e = assertThrows(IOException.class, () -> ppmDataAccess.processBinary(is));
        assertEquals("Insufficient data in binary ppm file", e.getMessage());
    }

    /* ascii */
    @Test
    void testProcessAsciiNonNumericData() {
        InputStream header = new ByteArrayInputStream("255\n".getBytes());
        InputStream data = new ByteArrayInputStream("a 128 64 128 64 35 ".getBytes());
        InputStream is = new SequenceInputStream(header, data);

        IOException e = assertThrows(IOException.class, () -> ppmDataAccess.processAscii(is));
        assertEquals("Missing or invalid color data in ascii ppm file", e.getMessage());
    }

    /**
     * Tests ASCII PPM with an invalid color value exceeding max range.
     */
    @Test
    void testProcessAsciiColorValueOutOfRange() {
        InputStream header = new ByteArrayInputStream("255\n".getBytes());
        InputStream data = new ByteArrayInputStream("260 128 64 128 64 35 ".getBytes());
        InputStream is = new SequenceInputStream(header, data);
        IOException e = assertThrows(IOException.class, () -> ppmDataAccess.processAscii(is));
        assertEquals("color value out of range in ascii ppm file", e.getMessage());
    }

    /**
     * Tests ASCII PPM with incomplete data.
     */
    @Test
    void testProcessAsciiNegativeData() {
        InputStream header = new ByteArrayInputStream("255\n".getBytes());
        InputStream data = new ByteArrayInputStream("-3 128 64 128 64 35 ".getBytes());
        InputStream is = new SequenceInputStream(header, data);

        IOException e = assertThrows(IOException.class, () -> ppmDataAccess.processAscii(is));
        assertEquals("color value out of range in ascii ppm file", e.getMessage());
    }

    /* ---------- max color --------- */

    /**
     * Tests max color value outside valid range.
     */
    @Test
    void testInvalidMaxColorValue() {
        InputStream is = new ByteArrayInputStream("70000\n".getBytes()); // maxColorValue exceeds 65535
        IOException e = assertThrows(IOException.class, () -> ppmDataAccess.readMaxValue(is));
        assertEquals("Invalid max color value in header", e.getMessage());
    }

    /**
     * tests maxcolor value negative
     */
    @Test
    void testNegativeMaxColorValue() {
        InputStream is = new ByteArrayInputStream("-3\n".getBytes());
        IOException e = assertThrows(IOException.class, () -> ppmDataAccess.readMaxValue(is));
        assertEquals("Invalid max color value in header", e.getMessage());
    }

    /**
     * tests maxcolor value negative
     */
    @Test
    void testNonNumericMaxColorValue() {
        InputStream is = new ByteArrayInputStream("ciao\n".getBytes());
        IOException e = assertThrows(IOException.class, () -> ppmDataAccess.readMaxValue(is));
        assertEquals("Max color value is missing or invalid in header", e.getMessage());
    }
}
