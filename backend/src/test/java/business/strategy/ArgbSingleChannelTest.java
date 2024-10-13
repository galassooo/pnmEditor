package business.strategy;

import ch.supsi.business.strategy.ArgbSingleChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class ArgbSingleChannelTest {
    @Test
    void testToArgb() {
        ArgbSingleChannel converter = new ArgbSingleChannel(65535);
        //lowest
        assertEquals(0xFF000000L, converter.toArgb(0));

        //max
        assertEquals(0xFFFFFFFFL, converter.toArgb(65535));

        //mid
        assertEquals(0xFF7F7F7FL, converter.toArgb(32767));
    }

    @Test
    void testToOriginal() {
        ArgbSingleChannel converter = new ArgbSingleChannel(65535);

        assertEquals(0, converter.toOriginal(0xFF000000L));

        assertEquals(65535, converter.toOriginal(0xFFFFFFFFL));
        //la conversione inversa arrotonda i valori intermedi -> not equal to original
    }

    @Test
    void testWithDifferentMaxValue() {
        ArgbSingleChannel converter = new ArgbSingleChannel(255);

        assertEquals(0xFFFFFFFFL, converter.toArgb(255), "Expected white for max grayscale value 255");

        assertEquals(128, converter.toOriginal(0xFF808080L), "Expected 128 for mid-gray ARGB with maxValue 255");
    }
}

