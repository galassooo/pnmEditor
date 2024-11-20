package ch.supsi.business.strategy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class SingleBitTest {


    private final SingleBit converter = new SingleBit();

    @Test
    void testToArgb() {
        assertEquals(0xFF000000L, converter.toArgb(1), "Expected black for pixel value 1");

        assertEquals(0xFFFFFFFFL, converter.toArgb(0), "Expected white for pixel value 0");
    }

    @Test
    void testArgbToOriginal() {
        assertEquals(1, converter.ArgbToOriginal(0xFF000000L), "Expected 1 for black pixel");

        assertEquals(0, converter.ArgbToOriginal(0xFFFFFFFFL), "Expected 0 for white pixel");
    }

    @ParameterizedTest //testa tutti i valori sotto con un unico test
    @MethodSource("colorToGrayscaleTestCases")
    void testColorToGrayscaleConversion(long inputPixel, long expected, String message) {
        assertEquals(expected, converter.ArgbToOriginal(inputPixel), message);
    }

    private static Stream<Arguments> colorToGrayscaleTestCases() {
        return Stream.of(
                // Dark colors (should convert to 1/black)
                Arguments.of(0xFF000080L, 1, "Dark blue should convert to 1"),
                Arguments.of(0xFF800000L, 1, "Dark red should convert to 1"),
                Arguments.of(0xFF008000L, 1, "Dark green should convert to 1"),
                Arguments.of(0xFF404040L, 1, "Dark gray should convert to 1"),

                // Light colors (should convert to 0/white)
                Arguments.of(0xFFFF8080L, 0, "Light red should convert to 0"),
                Arguments.of(0xFF80FF80L, 0, "Light green should convert to 0"),
                Arguments.of(0xFF8080FFL, 0, "Light blue should convert to 0"),
                Arguments.of(0xFFA0A0A0L, 0, "Light gray should convert to 0"),

                // Mixed intensity colors
                Arguments.of(0xFFFF0000L, 1, "Pure red converts based on weighted formula"),
                Arguments.of(0xFF00FF00L, 0, "Pure green converts based on weighted formula"),
                Arguments.of(0xFF0000FFL, 1, "Pure blue converts based on weighted formula")
        );
    }

    @Test
    void testEdgeCases() {
        // Test values right around the threshold
        long justAboveThreshold = 0xFF808080L; // Gray value 128
        long justBelowThreshold = 0xFF7F7F7FL; // Gray value 127

        assertEquals(0, converter.ArgbToOriginal(justAboveThreshold),
                "Gray value just above threshold should be white/0");
        assertEquals(1, converter.ArgbToOriginal(justBelowThreshold),
                "Gray value just below threshold should be black/1");
    }

    @Test
    void testEquals(){
        assertNotEquals(converter, new SingleBit());
        assertEquals(converter, converter);
    }
}

