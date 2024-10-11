package business.image;

import ch.supsi.business.Image.ImageBusiness;
import ch.supsi.business.strategy.ArgbSingleBit;
import ch.supsi.business.strategy.ArgbConvertStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ImageBusinessTest {

    /* instance fields */
    private int[][] sampleMatrix;
    private int width;
    private int height;
    private ArgbConvertStrategy strategy;

    /**
     * initialize a 2x2 matrix
     */
    @BeforeEach
    void setUp() {

        sampleMatrix = new int[][]{
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
        ImageBusiness image = new ImageBusiness(sampleMatrix, width, height, 1, strategy);

        int[][] expectedArgbPixels = {
                {0xFF000000, 0xFFFFFFFF},
                {0xFFFFFFFF, 0xFF000000}
        };

        assertArrayEquals(expectedArgbPixels, image.getPixels(), "wrong pixel conversion");
        assertEquals(width, image.getWidth(), "wrong width");
        assertEquals(height, image.getHeight(), "wrong height");
    }


    /**
     * test the constructor with a non-squared image
     */
    @Test
    void testNonSquareImage() {
        int[][] nonSquareMatrix = {
                {1, 0, 1},
                {0, 1, 0}
        };
        ImageBusiness image = new ImageBusiness(nonSquareMatrix, 3, 2,1, strategy);

        int[][] expectedArgbPixels = {
                {0xFF000000, 0xFFFFFFFF, 0xFF000000},
                {0xFFFFFFFF, 0xFF000000, 0xFFFFFFFF}
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
        int[][] emptyMatrix = new int[0][0];
        ImageBusiness image = new ImageBusiness(emptyMatrix, 0, 0,1, strategy);

        assertEquals(0, image.getPixels().length, "empty matrix expected");
        assertEquals(0, image.getWidth(), "wrong width for empty matrix");
        assertEquals(0, image.getHeight(), "wrong height for empty matrix");
    }

    /* -------- conversion strategy -------- */

    @Test
    void testDifferentStrategy() {
        ArgbConvertStrategy customStrategy = new ArgbConvertStrategy() {
            @Override
            public int toArgb(int pixel, int maxValue) {
                return 0xFF123456;
            }
        };

        //construct a new matrix with the given strategy
        ImageBusiness image = new ImageBusiness(sampleMatrix, width, height,1, customStrategy);

        int[][] expectedCustomPixels = {
                {0xFF123456, 0xFF123456},
                {0xFF123456, 0xFF123456}
        };

        assertArrayEquals(expectedCustomPixels, image.getPixels(), "Pixel conversione errata con strategia personalizzata");
    }
}

