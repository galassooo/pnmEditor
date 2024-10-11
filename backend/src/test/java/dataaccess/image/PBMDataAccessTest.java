package dataaccess.image;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ch.supsi.dataaccess.PBMDataAccess;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class PBMDataAccessTest {

    /* instance field */
    private PBMDataAccess pbmDataAccess;

    /**
     * Initializes an instance with preset width and height
     */
    @BeforeEach
    void setUp() {
        pbmDataAccess = PBMDataAccess.getInstance();
        pbmDataAccess.setWidth(4);
        pbmDataAccess.setHeight(4);
    }

    /**
     * Tests singleton pattern
     */
    @Test
    void testSingleton() {
        PBMDataAccess pbmDataAccess2 = PBMDataAccess.getInstance();
        assertEquals(pbmDataAccess2, pbmDataAccess);
    }

    /* --------- valid input --------- */

    /**
     * Tests processBinary with valid binary input
     */
    @Test
    void testProcessBinaryValidInput() throws IOException {
        byte[] binaryData = new byte[]{
                (byte) 0b10100000,
                (byte) 0b01010000,
                (byte) 0b11000000,
                (byte) 0b00110000
        };

        InputStream is = new ByteArrayInputStream(binaryData);
        pbmDataAccess.processBinary(is);

        int[][] expectedMatrix = {
                {1, 0, 1, 0},
                {0, 1, 0, 1},
                {1, 1, 0, 0},
                {0, 0, 1, 1}
        };

        assertArrayEquals(expectedMatrix, pbmDataAccess.originalMatrix);
    }

    /**
     * Tests processAscii with valid ASCII input
     */
    @Test
    void testProcessAsciiValidInput() throws IOException {
        String asciiData = "1 0 1 0\n0 1 0 1\n1 1 0 0\n0 0 1 1\n";
        InputStream is = new ByteArrayInputStream(asciiData.getBytes());

        pbmDataAccess.processAscii(is);

        int[][] expectedMatrix = {
                {1, 0, 1, 0},
                {0, 1, 0, 1},
                {1, 1, 0, 0},
                {0, 0, 1, 1}
        };

        assertArrayEquals(expectedMatrix, pbmDataAccess.originalMatrix);
    }

    /* --------- invalid input --------- */

    /**
     * Tests processBinary with incomplete binary data
     */
    @Test
    void testProcessBinaryInvalidInput() {
        byte[] incompleteData = new byte[]{(byte) 0b10100000};
        InputStream is = new ByteArrayInputStream(incompleteData);

        assertThrows(IOException.class, () -> pbmDataAccess.processBinary(is));
    }

    /**
     * Tests processAscii with incomplete ASCII data
     */
    @Test
    void testProcessAsciiInvalidInput() {
        String incompleteAsciiData = "1 0 1\n0 1 0\n";
        InputStream is = new ByteArrayInputStream(incompleteAsciiData.getBytes());

        assertThrows(IOException.class, () -> pbmDataAccess.processAscii(is));
    }

    /**
     * Tests processBinary with an incomplete byte and padding
     */
    @Test
    void testProcessBinaryWithIncompleteByte() throws IOException {
        pbmDataAccess.setWidth(9);
        pbmDataAccess.setHeight(2);
        pbmDataAccess.setFormat("P4");

        byte[] binaryData = new byte[]{
                (byte) 0b10101010,
                (byte) 0b10000000,
                (byte) 0b01010101,
                (byte) 0b00000000
        };

        InputStream is = new ByteArrayInputStream(binaryData);
        pbmDataAccess.processBinary(is);

        int[][] expectedMatrix = {
                {1, 0, 1, 0, 1, 0, 1, 0, 1},
                {0, 1, 0, 1, 0, 1, 0, 1, 0}
        };

        assertArrayEquals(expectedMatrix, pbmDataAccess.originalMatrix);
    }
}
