package ch.supsi.business.strategy;

import ch.supsi.business.strategy.SingleChannel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SingleChannelTest {

    @Test
    void constructor_SetsMaxValue() {
        SingleChannel converter = new SingleChannel(255);
    }

    @Test
    void equals_SameMaxValue_ReturnsTrue() {
        SingleChannel converter1 = new SingleChannel(255);
        SingleChannel converter2 = new SingleChannel(255);
        assertEquals(converter1, converter2);
        assertEquals(converter1.hashCode(), converter2.hashCode());
    }

    @Test
    void equals_DifferentMaxValue_ReturnsFalse() {
        SingleChannel converter1 = new SingleChannel(255);
        SingleChannel converter2 = new SingleChannel(65535);
        assertNotEquals(converter1, converter2);
        assertNotEquals(converter1.hashCode(), converter2.hashCode());
    }

    @ParameterizedTest
    @MethodSource("toArgbTestCases")
    void toArgb_ConvertsProperly(int maxValue, long input, long expected) {
        SingleChannel converter = new SingleChannel(maxValue);
        assertEquals(expected, converter.toArgb(input));
    }

    private static Stream<Arguments> toArgbTestCases() {
        return Stream.of(
                Arguments.of(255, 0, 0xFF000000L),
                Arguments.of(255, 255, 0xFFFFFFFFL),
                Arguments.of(255, 128, 0xFF808080L),
                Arguments.of(65535, 0, 0xFF000000L),
                Arguments.of(65535, 65535, 0xFFFFFFFFL),
                Arguments.of(65535, 32767, 0xFF7F7F7FL)
        );
    }

    @ParameterizedTest
    @MethodSource("argbToOriginalColorTestCases")
    void argbToOriginal_WithColorInput_AppliesWeightedConversion(int maxValue, long colorPixel, long expected) {
        SingleChannel converter = new SingleChannel(maxValue);
        long result = converter.ArgbToOriginal(colorPixel);
        assertTrue(Math.abs(expected - result) <= 1,
                String.format("Expected ~%d but was %d", expected, result));
    }

    //testa tutti i valori sotto con un unico test stra comodo
    private static Stream<Arguments> argbToOriginalColorTestCases() {
        return Stream.of(
                Arguments.of(255, 0xFFFF0000L, 76),
                Arguments.of(255, 0xFF00FF00L, 149),
                Arguments.of(255, 0xFF0000FFL, 29),
                Arguments.of(65535, 0xFFFF0000L, 19595),
                Arguments.of(65535, 0xFF00FF00L, 38470),
                Arguments.of(65535, 0xFF0000FFL, 7470)
        );
    }

    @Test
    void conversionRoundTrip_PreservesGrayValues() {
        SingleChannel converter = new SingleChannel(255);
        long originalGray = 128;
        long argb = converter.toArgb(originalGray);
        long result = converter.ArgbToOriginal(argb);
        assertEquals(originalGray, result);
    }

    @Test
    void testEqualsSameObject(){
        SingleChannel converter = new SingleChannel(255);
        assertEquals(converter, converter);
    }

    @Test
    void testEqualsDifferentClass(){
        SingleChannel converter = new SingleChannel(255);

        assertNotEquals(converter, "converter2");
    }
}