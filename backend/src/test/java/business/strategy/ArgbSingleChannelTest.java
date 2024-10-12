package business.strategy;

import ch.supsi.business.strategy.ArgbSingleChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class ArgbSingleChannelTest {

    /* instance fields */
    private ArgbSingleChannel strategy;
    private Random random;

    @BeforeEach
    void setUp() {
        strategy = new ArgbSingleChannel();
        random = new Random();
    }

    /**
     * Generates and test 20 random gray values
     */
    @Test
    void testRandomGrayValues() {
        for (int i = 0; i < 20; i++) {
            //genero valori random 20 volte e li testo
            int grayValue = random.nextInt(65536);
            int maxValue = random.nextInt(65535) + 1;

            //calcolo valore atteso
            long normalizedGray = (int) ((grayValue / (double) maxValue) * 255);
            long expectedArgb = (0xFFL << 24) | (normalizedGray << 16) | (normalizedGray << 8) | normalizedGray;

            long resultArgb = strategy.toArgb(grayValue, maxValue);

            assertEquals(expectedArgb, resultArgb, "Failed for grayValue: " + grayValue + " and maxValue: " + maxValue);
        }
    }
}

