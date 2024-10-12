package business.strategy;

import ch.supsi.business.strategy.ArgbThreeChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArgbThreeChannelTest {

    @Test
    public void testToArgbWith8BitImage() {
        ArgbThreeChannel converter = new ArgbThreeChannel();
        int maxValue = 255; // 8-bit image

        long pixel = (120L << 16) | (200L << 8) | 150L; // R=120, G=200, B=150
        long expectedArgb = (0xFFL << 24) | (120L << 16) | (200L << 8) | 150L;

        assertEquals(expectedArgb, converter.toArgb(pixel, maxValue));
    }

    @Test
    public void testToArgbWith16BitImage() {
        ArgbThreeChannel converter = new ArgbThreeChannel();
        int maxValue = 65535; // 16-bit image

        // Original 16-bit color values (not normalized)
        long red16 = 30000L;
        long green16 = 45000L;
        long blue16 = 25000L;

        // Construct the pixel with the original 16-bit values
        long pixel = (red16 << 32) | (green16 << 16) | blue16;

        // Normalize expected colors to 8-bit and calculate ARGB
        long expectedRed = (long) ((red16 / (double) maxValue) * 255);
        long expectedGreen = (long) ((green16 / (double) maxValue) * 255);
        long expectedBlue = (long) ((blue16 / (double) maxValue) * 255);

        // Expected ARGB value with 0xFF alpha
        long expectedArgb = (0xFFL << 24) | (expectedRed << 16) | (expectedGreen << 8) | expectedBlue;

        // Assert that the converted ARGB matches the expected value
        assertEquals(expectedArgb, converter.toArgb(pixel, maxValue));
    }


    @Test
    public void testToArgbWithMaxColorValues() {
        ArgbThreeChannel converter = new ArgbThreeChannel();
        int maxValue = 255; // Test for 8-bit max color values

        long pixel = (255L << 16) | (255L << 8) | 255L; // R=255, G=255, B=255 (white)
        long expectedArgb = (0xFFL << 24) | (255L << 16) | (255L << 8) | 255L;

        assertEquals(expectedArgb, converter.toArgb(pixel, maxValue));
    }
}