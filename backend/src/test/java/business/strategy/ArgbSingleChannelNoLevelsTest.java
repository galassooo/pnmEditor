package business.strategy;

import ch.supsi.business.strategy.ArgbSingleChannelNoLevels;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ArgbSingleChannelNoLevelsTest {

    /* instance field */
    private ArgbSingleChannelNoLevels strategy;

    /**
     * create an instance
     */
    @BeforeEach
    void setUp() {
        strategy = new ArgbSingleChannelNoLevels();
    }

    /* -------- conversion --------- */

    /**
     * test conversion with all ones (black expected)
     */
    @Test
    void testToArgbWithAllOnes() {
        int[][] inputMatrix = {
                {1, 1},
                {1, 1}
        };

        int[][] expectedOutput = {
                {0xFF000000, 0xFF000000},
                {0xFF000000, 0xFF000000}
        };

        assertArrayEquals(expectedOutput, strategy.toArgb(inputMatrix));
    }

    /**
     * test conversion with all zeros (white expected)
     */
    @Test
    void testToArgbWithAllZeros() {
        int[][] inputMatrix = {
                {0, 0},
                {0, 0}
        };

        int[][] expectedOutput = {
                {0xFFFFFFFF, 0xFFFFFFFF},
                {0xFFFFFFFF, 0xFFFFFFFF}
        };

        assertArrayEquals(expectedOutput, strategy.toArgb(inputMatrix));
    }

    /**
     * test conversion with a matrix containing zeros and ones
     */
    @Test
    void testToArgbWithMixedValues() {
        int[][] inputMatrix = {
                {1, 0},
                {0, 1}
        };

        int[][] expectedOutput = {
                {0xFF000000, 0xFFFFFFFF},
                {0xFFFFFFFF, 0xFF000000}
        };

        assertArrayEquals(expectedOutput, strategy.toArgb(inputMatrix));
    }

    /* --------- size --------- */

    /**
     * test conversion for an empty matrix
     */
    @Test
    void testToArgbWithEmptyMatrix() {
        int[][] inputMatrix = new int[0][0];
        int[][] output = strategy.toArgb(inputMatrix);
        assertEquals(0, output.length, "Expected an empty output matrix");
    }

    /**
     * test conversion for a non-square matrix
     */
    @Test
    void testToArgbWithNonSquareMatrix() {
        int[][] inputMatrix = {
                {1, 0, 1},
                {0, 1, 0}
        };

        int[][] expectedOutput = {
                {0xFF000000, 0xFFFFFFFF, 0xFF000000},
                {0xFFFFFFFF, 0xFF000000, 0xFFFFFFFF}
        };

        assertArrayEquals(expectedOutput, strategy.toArgb(inputMatrix));
    }

    /* -------- single pixel -------- */

    /**
     * Test conversion of a single pixel with value one.
     * Expects the output to be ARGB_BLACK (opaque black) when input is 1.
     */
    @Test
    void testConvertToArgbWithOne() {
        int argbValue = strategy.toArgb(new int[][]{{1}})[0][0];
        assertEquals(0xFF000000, argbValue, "Expected ARGB_BLACK for input 1");
    }

    /**
     * Test conversion of a single pixel with value zero.
     * Expects the output to be ARGB_WHITE (opaque white) when input is 0.
     */
    @Test
    void testConvertToArgbWithZero() {
        int argbValue = strategy.toArgb(new int[][]{{0}})[0][0];
        assertEquals(0xFFFFFFFF, argbValue, "Expected ARGB_WHITE for input 0");
    }

}
