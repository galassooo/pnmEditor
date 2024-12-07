package ch.supsi.business.strategy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArgbThreeChannelTest {

    @Test
    void testToArgbWith8BitMaxValue() {
        ThreeChannel converter = new ThreeChannel(255);
        assertEquals(0xFF000000L, converter.toArgb(0x000000));
        assertEquals(0xFFFFFFFFL, converter.toArgb(0xFFFFFF));
        assertEquals(0xFFFF0000L, converter.toArgb(0xFF0000));
        assertEquals(0xFF00FF00L, converter.toArgb(0x00FF00));
        assertEquals(0xFF0000FFL, converter.toArgb(0x0000FF));
    }

    @Test
    void testArgbToOriginalWith8BitMaxValue() {
        ThreeChannel converter = new ThreeChannel(255);
        assertEquals(0x000000, converter.ArgbToOriginal(0xFF000000L));
        assertEquals(0xFFFFFF, converter.ArgbToOriginal(0xFFFFFFFFL));
        assertEquals(0xFF0000, converter.ArgbToOriginal(0xFFFF0000L));
        assertEquals(0x00FF00, converter.ArgbToOriginal(0xFF00FF00L));
        assertEquals(0x0000FF, converter.ArgbToOriginal(0xFF0000FFL));
    }

    @Test
    void testToArgbWith16BitMaxValue() {
        ThreeChannel converter = new ThreeChannel(65535);
        long pixel = (65535L << 32) | (65535L << 16) | 65535L;
        assertEquals(0xFFFFFFFFL, converter.toArgb(pixel));

        pixel = (32767L << 32) | (32767L << 16) | 32767L;
        assertEquals(0xFF7F7F7FL, converter.toArgb(pixel));

        pixel = (65535L << 32);
        assertEquals(0xFFFF0000L, converter.toArgb(pixel));

        pixel = (65535L << 16);
        assertEquals(0xFF00FF00L, converter.toArgb(pixel));

        pixel = 65535L;
        assertEquals(0xFF0000FFL, converter.toArgb(pixel));
    }

    @Test
    void testArgbToOriginalWith16BitMaxValue() {
        ThreeChannel converter = new ThreeChannel(65535);
        assertEquals((65535L << 32) | (65535L << 16) | 65535L, converter.ArgbToOriginal(0xFFFFFFFFL));
        assertEquals(0, converter.ArgbToOriginal(0xFF000000L));
        assertEquals((65535L << 32), converter.ArgbToOriginal(0xFFFF0000L));
        assertEquals((65535L << 16), converter.ArgbToOriginal(0xFF00FF00L));
        assertEquals(65535L, converter.ArgbToOriginal(0xFF0000FFL));
    }
}