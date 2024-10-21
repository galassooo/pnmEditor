package business.image;

import ch.supsi.business.image.ImageBusiness;
import ch.supsi.business.strategy.ArgbSingleBit;
import ch.supsi.business.strategy.ArgbConvertStrategy;
import ch.supsi.business.strategy.ArgbSingleChannel;
import ch.supsi.business.strategy.ArgbThreeChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ImageBusinessTest {

    /* instance fields */
    private long[][] sampleMatrix;
    private int width;
    private int height;
    private ArgbConvertStrategy strategy;

    /**
     * initialize a 2x2 matrix
     */
    @BeforeEach
    void setUp() {
        sampleMatrix = new long[][]{
                {1, 0},
                {0, 1}
        };
        width = 2;
        height = 2;
        strategy = new ArgbSingleBit();
    }

    /* -------- constructor tests -------- */

    /**
     * test the image constructor
     */
    @Test
    void testConstructorAndPixelConversion() {
        ImageBusiness image = new ImageBusiness(sampleMatrix, "null", "P4",  strategy);

        long[][] expectedArgbPixels = {
                {0xFF000000L, 0xFFFFFFFFL},
                {0xFFFFFFFFL, 0xFF000000L}
        };

        assertArrayEquals(expectedArgbPixels, image.getPixels(), "wrong pixel conversion");
        assertEquals(width, image.getWidth(), "wrong width");
        assertEquals(height, image.getHeight(), "wrong height");
        assertEquals("null", image.getFilePath());
        assertEquals("P4", image.getMagicNumber());
    }


    /**
     * test the constructor with a non-squared image
     */
    @Test
    void testNonSquareImage() {
        long[][] nonSquareMatrix = {
                {1, 0, 1},
                {0, 1, 0}
        };
        ImageBusiness image = new ImageBusiness(nonSquareMatrix, null,"P3", strategy);

        long[][] expectedArgbPixels = {
                {0xFF000000L, 0xFFFFFFFFL, 0xFF000000L},
                {0xFFFFFFFFL, 0xFF000000L, 0xFFFFFFFFL}
        };

        assertArrayEquals(expectedArgbPixels, image.getPixels(), "wrong pixel conversion for non-squared image");
        assertEquals(3, image.getWidth(), "wrong width for non-squared image");
        assertEquals(2, image.getHeight(), "wrong height for non-squared image");
    }

    /**
     * construct a matrix with no dimension
     */
    @Test
    void testEmptyMatrix() {
        long[][] emptyMatrix = new long[0][0];
        ImageBusiness image = new ImageBusiness(emptyMatrix, null, "P3", strategy);

        assertEquals(0, image.getPixels().length, "empty matrix expected");
        assertEquals(0, image.getWidth(), "wrong width for empty matrix");
        assertEquals(0, image.getHeight(), "wrong height for empty matrix");
    }

    /* -------- conversion strategy -------- */

    @Test
    void testDifferentStrategy() {
        ArgbConvertStrategy customStrategy = new ArgbConvertStrategy() {
            @Override
            public long toArgb(long pixel) {
                return pixel*2;
            }

            @Override
            public long toOriginal(long pixel) {
                return pixel/2;
            }
        };

        //construct a new matrix with the given strategy
        ImageBusiness image = new ImageBusiness(sampleMatrix, null, "P8", customStrategy);

        long[][] expectedCustomPixels = {
                {2, 0},
                {0, 2}
        };

        assertArrayEquals(expectedCustomPixels, image.getPixels(), "Pixel conversione errata con strategia personalizzata");
    }

    @Test
    void testReturnOriginalMatrixWithSingleChannelStrategy() {
        long[][] originalMatrix = {
                {0, 128, 255},
                {255, 128, 0}
        };
        ArgbSingleChannel strategy = new ArgbSingleChannel(255);

        ImageBusiness img = new ImageBusiness(originalMatrix, "testImage.pgm", "P5", strategy);

        long[][] result = img.returnOriginalMatrix(strategy);
        assertArrayEquals(originalMatrix, result);
    }

    @Test
    void testReturnOriginalMatrixWithThreeChannelStrategy() {
        long[][] originalMatrix = {
                {(65535L << 32), (65535L << 16), 65535L},
                {(65535L << 32) | (65535L << 16) | 65535L, 0, 0}
        };
        ArgbThreeChannel strategy = new ArgbThreeChannel(65535);

        ImageBusiness img = new ImageBusiness(originalMatrix, "testImage.ppm", "P6", strategy);

        long[][] result = img.returnOriginalMatrix(strategy);
        assertArrayEquals(originalMatrix, result);
    }

    @Test
    void testEmptyOriginalMatrix() {
        long[][] emptyMatrix = new long[0][0];
        ArgbSingleChannel strategy = new ArgbSingleChannel(255);

        ImageBusiness img = new ImageBusiness(emptyMatrix, "emptyImage.pgm", "P5", strategy);

        assertEquals(0, img.getHeight());
        assertEquals(0, img.getWidth());
        assertArrayEquals(new long[0][0], img.returnOriginalMatrix(strategy));
    }

    @Test
    void testSetPixels(){
        long[][] expected = {{1,2}, {3,4}};

        ImageBusiness img = new ImageBusiness(null, "testImage.pgm", "P5", strategy);
        img.setPixels(expected);

        assertArrayEquals(expected, img.getPixels());

    }


    @Test
    void testGetWidthNullMatrix(){

        ImageBusiness img = new ImageBusiness(null, "testImage.pgm", "P5", strategy);
        int width = img.getWidth();
        assertEquals(0, width);

    }
}

