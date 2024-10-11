package business.strategy;

import ch.supsi.business.strategy.ArgbSingleBit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ArgbSingleChannelNoLevelsTest {

    /* instance field */
    private ArgbSingleBit strategy;

    /**
     * create an instance
     */
    @BeforeEach
    void setUp() {
        strategy = new ArgbSingleBit();
    }

    /* -------- single pixel conversion -------- */

    /**
     * Test conversion of a pixel with minimum value (expected ARGB_WHITE)
     */
    @Test
    void testToArgbWithZero() {
        int argbValue = strategy.toArgb(0, 1);  // 0 represents white
        assertEquals(0xFFFFFFFF, argbValue, "expected white for a pixel value of 0");
    }

    /**
     * Test conversion of a pixel with maximum value (expected ARGB_BLACK)
     */
    @Test
    void testToArgbWithMaxValue() {
        int argbValue = strategy.toArgb(1, 1);  // Max value represents black
        assertEquals(0xFF000000, argbValue, "expected black for a pixel value of 1");
    }

    /**
     * Test conversion of a pixel with half the max gray value (expected ARGB_WHITE)
     */
    @Test
    void testToArgbWithHalfMaxValue() {
        int argbValue = strategy.toArgb(0, 2);  // Less than maxValue should be white
        assertEquals(0xFFFFFFFF, argbValue, "expected white for a pixel with max value bigger than 1");
    }
}

