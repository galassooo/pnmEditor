package dataaccess.image;

import ch.supsi.business.strategy.ArgbConvertStrategy;
import ch.supsi.business.strategy.ArgbSingleChannel;
import ch.supsi.business.strategy.ArgbThreeChannel;
import ch.supsi.dataaccess.PGMDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;

import static org.junit.jupiter.api.Assertions.*;

class PGMDataAccessTest {

    /* instance field */
    private PGMDataAccess pgmDataAccess;

    /**
     * Initializes an instance with preset width and height
     */
    @BeforeEach
    void setUp() {
        pgmDataAccess = PGMDataAccess.getInstance();
        pgmDataAccess.setWidth(4);
        pgmDataAccess.setHeight(4);
    }

    /**
     * Tests singleton pattern
     */
    @Test
    void testSingleton() {
        PGMDataAccess pgmDataAccess2 = PGMDataAccess.getInstance();
        assertEquals(pgmDataAccess2, pgmDataAccess);
    }
    /* --------- invalid maxValue --------- */

    /**
     * Test negative value in maxvalue
     */
    @Test
    void testNegativeMaxValue(){
        String maxGrayValue = "-2";
        ByteArrayInputStream headerStream = new ByteArrayInputStream(maxGrayValue.getBytes());

        IOException e = assertThrows(IOException.class, () -> pgmDataAccess.readMaxValue(headerStream));
        assertEquals("Invalid max gray value in header", e.getMessage());
    }

    /**
     * Test bigger value then possible in maxvalue
     */
    @Test
    void testOverMaximumValueMaxValue(){
        String maxGrayValue = "65536";
        ByteArrayInputStream headerStream = new ByteArrayInputStream(maxGrayValue.getBytes());

        IOException e = assertThrows(IOException.class, () -> pgmDataAccess.readMaxValue(headerStream));
        assertEquals("Invalid max gray value in header", e.getMessage());
    }

    /**
     * Test missing value in maxvalue
     */
    @Test
    void testMissingMaxValue(){
        String maxGrayValue = "";
        ByteArrayInputStream headerStream = new ByteArrayInputStream(maxGrayValue.getBytes());

        IOException e = assertThrows(IOException.class, () -> pgmDataAccess.readMaxValue(headerStream));
        assertEquals("Max gray value is missing or invalid in header", e.getMessage());
    }

    /* --------- invalid pixel value ---------- */

    /**
     * test the case in which a pixel value is higher then maxValue (byte file)
     */
    @Test
    void testPixelOverflowByte() {
        String maxGrayValueHeader = "150\n";
        byte[] binaryData = new byte[]{
                (byte) 10, (byte) 20, (byte) 30, (byte) 40,
                (byte) 50, (byte) 60, (byte) 70, (byte) 80,
                (byte) 90, (byte) 100, (byte) 110, (byte) 120,
                (byte) 140, (byte) 140, (byte) 150, (byte) 160
        };

        //is for header
        ByteArrayInputStream headerStream = new ByteArrayInputStream(maxGrayValueHeader.getBytes());
        //is for matrix
        ByteArrayInputStream dataStream = new ByteArrayInputStream(binaryData);
        //combined is
        InputStream is = new SequenceInputStream(headerStream, dataStream);
        IOException e = assertThrows(IOException.class, () -> pgmDataAccess.processBinary(is));
        assertEquals("gray pixel value out of range in binary pmg file", e.getMessage());
    }

    /**
     * test the case in which a pixel value is higher then maxValue (ascii file)
     */
    @Test
    void testPixelOverflowAscii() {
        String asciiData = "150\n10 20 30 40\n50 60 70 80\n90 100 110 120\n130 140 150 160\n";
        InputStream is = new ByteArrayInputStream(asciiData.getBytes());
        IOException e = assertThrows(IOException.class, () -> pgmDataAccess.processAscii(is));
        assertEquals("gray pixel value out of range in binary pmg file", e.getMessage());

    }

    /**
     * test the case in which a pixel value is negative
     */
    @Test
    void testNegativePixelAscii() {
        String asciiData = "255\n10 -20 30 40\n50 60 70 80\n90 100 110 120\n130 140 150 160\n";
        InputStream is = new ByteArrayInputStream(asciiData.getBytes());
        IOException e = assertThrows(IOException.class, () -> pgmDataAccess.processAscii(is));
        assertEquals("gray pixel value out of range in binary pmg file", e.getMessage());
    }

    /* --------- valid input --------- */

    /**
     * Tests processBinary with valid 8 bit binary input
     */
    @Test
    void testProcessBinary8BitValidInput() throws IOException {
        String maxGrayValueHeader = "255\n";
        byte[] binaryData = new byte[]{
                (byte) 10, (byte) 20, (byte) 30, (byte) 40,
                (byte) 50, (byte) 60, (byte) 70, (byte) 80,
                (byte) 90, (byte) 100, (byte) 110, (byte) 120,
                (byte) 130, (byte) 140, (byte) 150, (byte) 160
        };

        //is for header
        ByteArrayInputStream headerStream = new ByteArrayInputStream(maxGrayValueHeader.getBytes());
        //is for matrix
        ByteArrayInputStream dataStream = new ByteArrayInputStream(binaryData);
        //combined is
        InputStream is = new SequenceInputStream(headerStream, dataStream);

        long[][] expectedMatrix = {
                {10, 20, 30, 40},
                {50, 60, 70, 80},
                {90, 100, 110, 120},
                {130, 140, 150, 160}
        };

        assertArrayEquals(expectedMatrix, pgmDataAccess.processBinary(is));
    }

    /**
     * Tests processBinary with valid 16 bit binary input
     */
    @Test
    void testProcessBinary16BitValidInput() throws IOException {
        String maxGrayValueHeader = "65535\n";
        byte[] binaryData = new byte[]{ //non estraggo metodo per leggibilità
                0, 10, 0, 20, 0, 30, 0, 40,
                0, 50, 0, 60, 0, 70, 0, 80,
                0, 90, 0, 100, 0, 110, 0, 120,
                0, 110, 0, 110, 0, 110, 0, 110
        };

        //is for header
        ByteArrayInputStream headerStream = new ByteArrayInputStream(maxGrayValueHeader.getBytes());
        //is for matrix
        ByteArrayInputStream dataStream = new ByteArrayInputStream(binaryData);
        //combined is
        InputStream is = new SequenceInputStream(headerStream, dataStream);

        long[][] expectedMatrix = {
                {10, 20, 30, 40},
                {50, 60, 70, 80},
                {90, 100, 110, 120},
                {110, 110, 110, 110}
        };

        assertArrayEquals(expectedMatrix, pgmDataAccess.processBinary(is));
    }

    /**
     * Tests processAscii with valid ASCII input
     */
    @Test
    void testProcessAsciiValidInput() throws IOException {
        String asciiData = "255\n10 20 30 40\n50 60 70 80\n90 100 110 120\n130 140 150 160\n";
        InputStream is = new ByteArrayInputStream(asciiData.getBytes());

        long[][] expectedMatrix = {
                {10, 20, 30, 40},
                {50, 60, 70, 80},
                {90, 100, 110, 120},
                {130, 140, 150, 160}
        };

        assertArrayEquals(expectedMatrix, pgmDataAccess.processAscii(is));
    }

    /* --------- invalid input --------- */

    /**
     * Tests processBinary with incomplete binary data for 8 bit
     */
    @Test
    void testProcessBinary8BitInvalidInput() {
        String maxGrayValueHeader = "255\n";
        byte[] incompleteData = new byte[]{10, 20}; //incomplete data
        InputStream is = new ByteArrayInputStream((maxGrayValueHeader + new String(incompleteData)).getBytes());

        IOException e = assertThrows(IOException.class, () -> pgmDataAccess.processBinary(is));
        assertEquals("Insufficient data in binary pmg file",e.getMessage());
    }

    /**
     * Tests processBinary with incomplete binary data for 16 bit
     */
    @Test
    void testProcessBinary16BitInvalidInput() {
        String maxGrayValueHeader = "65535\n";
        byte[] incompleteData = new byte[]{0, 10, 0}; //incomplete data for 16 bit
        InputStream is = new ByteArrayInputStream((maxGrayValueHeader + new String(incompleteData)).getBytes());

        IOException e = assertThrows(IOException.class, () -> pgmDataAccess.processBinary(is));
        assertEquals("Insufficient data in binary pmg file for a 16 bit image",e.getMessage());
    }

    /**
     * Tests processAscii with incomplete ascii data
     */
    @Test
    void testProcessAsciiInvalidInput() {
        String incompleteAsciiData = "255\n10 20\n30"; // incomplete data (expected 4x4)
        InputStream is = new ByteArrayInputStream(incompleteAsciiData.getBytes());

        IOException e = assertThrows(IOException.class, () -> pgmDataAccess.processAscii(is));
        assertEquals("Invalid or missing data in ascii PGM file",e.getMessage());
    }


    /* --------- padding -------- */

    /**
     * Tests processBinary with an incomplete byte and padding for a non-standard width
     */
    @Test
    void testProcessBinary16BitWithPadding() throws IOException {
        pgmDataAccess.setWidth(3);
        pgmDataAccess.setHeight(2);
        pgmDataAccess.setFormat("P5");

        String maxGrayValueHeader = "65535\n";
        byte[] binaryData = new byte[]{
                0, 10, 0, 20, 0, 30, // Row 1
                0, 40, 0, 50, 0, 60  // Row 2, with padding for width 3
        };

        InputStream is = new ByteArrayInputStream((maxGrayValueHeader + new String(binaryData)).getBytes());

        long[][] expectedMatrix = {
                {10, 20, 30},
                {40, 50, 60}
        };

        assertArrayEquals(expectedMatrix, pgmDataAccess.processBinary(is));
    }

    /**
     * test if eof is reached in binary files
     */
    @Test
    void testEOF() {
        String maxGrayValueHeader = "65535\n";
        //genero solo un byte per l'ultimo pixel in modo da avere -1 in highbyte e 0 in low
        byte[] binaryData = new byte[]{
                0, 10,
                0, 30,
                //missing lowbyte
        };

        InputStream headerStream = new ByteArrayInputStream(maxGrayValueHeader.getBytes());
        InputStream dataStream = new ByteArrayInputStream(binaryData);
        InputStream is = new SequenceInputStream(headerStream, dataStream);

        IOException e = assertThrows(IOException.class, () -> pgmDataAccess.processBinary(is));
        assertEquals("Insufficient data in binary pmg file for a 16 bit image", e.getMessage());
    }

    /* ---------- getters --------- */

    /**
     * test the return type of argb strategy
     */
    @Test
    void testGetArgbStrategy(){
        ArgbConvertStrategy expected = new ArgbSingleChannel();
        //non è possibile fare equals -> classe senza stato
        assertEquals(expected.getClass(), pgmDataAccess.getArgbConvertStrategy().getClass());
    }
    /**
     * test the return type of maxValue strategy
     */
    @Test
    void TestGetMaxValue() throws IOException {
        InputStream is = new ByteArrayInputStream("24\n".getBytes());
        pgmDataAccess.readMaxValue(is);
        assertEquals(24, pgmDataAccess.getMaxPixelValue());
    }
}

