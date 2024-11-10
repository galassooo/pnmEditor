package ch.supsi.business.filters.strategy;


import ch.supsi.application.image.ImageBusinessInterface;
import ch.supsi.business.image.ImageBusiness;
import ch.supsi.business.filter.command.NegativeCommand;
import ch.supsi.business.strategy.SingleChannel;
import ch.supsi.business.strategy.SingleBit;
import ch.supsi.business.strategy.ThreeChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NegativeFilterTest {

    private NegativeCommand filter;

    @BeforeEach
    void setUp() {
        filter = new NegativeCommand();
    }

    @Test
    void testApplyFilterOnPPM() {
        long[][] argbPixels = {
                {0x000000L, 0x7F7F7FL, 0xFFFFFFL},
                {0xCCCCCCL, 0x333333L, 0x999999L}
        };

        long[][] expectedPixels = {
                {0xFFFFFFFFL, 0xFF808080L, 0xFF000000L},
                {0xFF333333L, 0xFFCCCCCCL, 0xFF666666L}
        };
//
//        ImageBusinessInterface img = new ImageBusiness(argbPixels, "test-path", "P3", new ThreeChannel(255));
//        filter.execute(img);
//
//        assertArrayEquals(expectedPixels, img.getPixels());
    }

    @Test
    void testApplyFilterOnPBM() {
        long[][] binaryPixels = {
                {0, 1, 0, 1},
                {1, 0, 1, 0}
        };

        //uso argbSingleBIt quindi viene convertita in:
        /**
         *         long[][] binaryPixels = {
         *                 {1, 0, 1, 0},
         *                 {0, 1, 0, 1}
         *         };
         */

        long[][] expectedPixels = {
                {0xFF000000L, 0xFFFFFFFFL, 0xFF000000L, 0xFFFFFFFFL},
                {0xFFFFFFFFL, 0xFF000000L, 0xFFFFFFFFL, 0xFF000000L}
        };

//        ImageBusinessInterface img = new ImageBusiness(binaryPixels, "test-path", "P4", new SingleBit());
//        filter.execute(img);
//
//        assertArrayEquals(expectedPixels, img.getPixels());
    }

    @Test
    void testApplyFilterOnPGM() {
        long[][] edgeCasePixels = {
                {0x0000, 0xFFFF}
        };

        long[][] expectedPixels = {
                {0xFFFFFFFFL, 0xFF000000L}
        };

//        ImageBusinessInterface img = new ImageBusiness(edgeCasePixels, "test-path", "P2", new SingleChannel(255));
//        filter.execute(img);
//
//        assertArrayEquals(expectedPixels, img.getPixels());
    }

    @Test
    void testMirrorOnEmptySecondDimensionMatrix() {
        // Matrice vuota
        long[][] original = new long[1][0];

//        ImageBusiness img = new ImageBusiness(original, "test.path", "P1", new ThreeChannel(255));
//        filter.execute(img);
//
//        assertEquals(0, img.getPixels()[0].length);
    }

    @Test
    void testMirrorOnEmptyMatrix() {
        // Matrice vuota
        long[][] original = new long[0][0];

//        ImageBusiness img = new ImageBusiness(original, "test.path", "P1", new ThreeChannel(255));
//        filter.execute(img);
//
//        assertEquals(0, img.getPixels().length);
    }


    @Test
    void testMirrorOnNullMatrix() {
        // Matrice vuota
        long[][] original = new long[0][0];

//        ImageBusiness img = new ImageBusiness(original, "test.path", "P1", new ThreeChannel(255));
//        img.setPixels(null);
//        filter.execute(img);
//
//        assertNull(img.getPixels());
    }

    @Test
    void testGetCode(){
        assertEquals("Negative", filter.getName());
    }
}

