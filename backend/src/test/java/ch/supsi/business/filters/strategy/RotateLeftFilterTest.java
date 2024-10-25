package ch.supsi.business.filters.strategy;

import ch.supsi.business.filter.strategy.RotateLeft;
import ch.supsi.business.image.ImageBusiness;
import ch.supsi.business.filter.strategy.NamedFilterStrategy;
import ch.supsi.business.strategy.ThreeChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RotateLeftFilterTest {

    NamedFilterStrategy rotateLeft;

    @BeforeEach
    void setup(){
        rotateLeft = new RotateLeft();
    }


    @Test
    void testRotate90Left() {
        long[][] original = {
                {1, 2},
                {3, 4},
        };
        ImageBusiness img = new ImageBusiness(original,"test.path", "P1", new ThreeChannel(255));

        long[][] expected ={
                {0xFF000002L, 0xFF000004L},
                {0xFF000001L, 0xFF000003L}
        };

        rotateLeft.applyFilter(img);
        long[][] originalArgb = img.getPixels();
        assertArrayEquals(expected, originalArgb);
    }
    @Test
    void testRotate90LeftNonSquare() {
        long[][] original = {
                {1, 2, 3},
                {4, 5,  6},
        };
        ImageBusiness img = new ImageBusiness(original,"test.path", "P1", new ThreeChannel(255));

        long[][] expected ={
                {0xFF000003L, 0xFF000006L},
                {0xFF000002L, 0xFF000005L},
                {0xFF000001L, 0xFF000004L}
        };

        rotateLeft.applyFilter(img);
        long[][] originalArgb = img.getPixels();
        assertArrayEquals(expected, originalArgb);
    }

    @Test
    void testRotate90Right() {
        long[][] original = {
                {1, 2},
                {3, 4},
        };
        ImageBusiness img = new ImageBusiness(original,"test.path", "P1", new ThreeChannel(255));

        long[][] expected ={
                {0xFF000002L, 0xFF000004L},
                {0xFF000001L, 0xFF000003L}
        };

        rotateLeft.applyFilter(img);
        long[][] originalArgb = img.getPixels();
        assertArrayEquals(expected, originalArgb);
    }

    @Test
    void testEmptyImage(){
        ImageBusiness img = new ImageBusiness(new long[0][0], "path", "P1", new ThreeChannel(255));
        rotateLeft.applyFilter(img);

        assertArrayEquals(new long[0][0], img.getPixels());
    }
    @Test
    void testMirrorOnEmptySecondDimensionMatrix() {
        long[][] original = new long[1][0];

        ImageBusiness img = new ImageBusiness(original, "test.path", "P1", new ThreeChannel(255));
        rotateLeft.applyFilter(img);

        assertEquals(0, img.getPixels()[0].length);
    }

    @Test
    void testMirrorOnEmptyMatrix() {
        long[][] original = new long[0][0];

        ImageBusiness img = new ImageBusiness(original, "test.path", "P1", new ThreeChannel(255));
        rotateLeft.applyFilter(img);

        assertEquals(0, img.getPixels().length);
    }


    @Test
    void testMirrorOnNullMatrix() {
        long[][] original = new long[0][0];

        ImageBusiness img = new ImageBusiness(original, "test.path", "P1", new ThreeChannel(255));
        img.setPixels(null);
        rotateLeft.applyFilter(img);

        assertNull(img.getPixels());
    }
    @Test
    void testGetName(){
        assertEquals("Rotate Left", rotateLeft.getName());
    }
}
