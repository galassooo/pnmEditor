package business.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ArgbSingleBitTest {

    /* instance field */
    private ch.supsi.business.strategy.ArgbSingleBit strategy;

    /**
     * create an instance
     */
    @BeforeEach
    void setUp() {
        strategy = new ch.supsi.business.strategy.ArgbSingleBit();
    }

    /**
     * Test conversion of a pixel with minimum value (expected ARGB_WHITE)
     */
    @Test
    void testToArgbWithZero() {
        long argbValue = strategy.toArgb(0, 1);  // 0 represents white
        assertEquals(0xFFFFFFFFL, argbValue, "expected white for a pixel value of 0");
    }

    /**
     * Test conversion of a pixel with maximum value (expected ARGB_BLACK)
     */
    @Test
    void testToArgbWithMaxValue() {
        long argbValue = strategy.toArgb(1, 1);  // Max value represents black
        assertEquals(0xFF000000L, argbValue, "expected black for a pixel value of 1");
    }

    /**
     * Test conversion of a pixel with half the max gray value (expected ARGB_WHITE)
     */
    @Test
    void testToArgbWithHalfMaxValue() {
        long argbValue = strategy.toArgb(0, 2);  // Less than maxValue should be white
        assertEquals(0xFFFFFFFFL, argbValue, "expected white for a pixel with max value bigger than 1");
    }
}

