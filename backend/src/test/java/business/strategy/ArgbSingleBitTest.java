package business.strategy;

import ch.supsi.business.strategy.ArgbSingleBit;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ArgbSingleBitTest {


    private final ArgbSingleBit argbSingleBit = new ArgbSingleBit();

    @Test
    void testToArgb() {
        assertEquals(0xFF000000L, argbSingleBit.toArgb(1), "Expected black for pixel value 1");

        assertEquals(0xFFFFFFFFL, argbSingleBit.toArgb(0), "Expected white for pixel value 0");
    }

    @Test
    void testToOriginal() {
        assertEquals(1, argbSingleBit.toOriginal(0xFF000000L), "Expected 1 for black pixel");

        assertEquals(0, argbSingleBit.toOriginal(0xFFFFFFFFL), "Expected 0 for white pixel");
    }
}

