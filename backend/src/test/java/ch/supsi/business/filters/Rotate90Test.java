package ch.supsi.business.filters;

import ch.supsi.business.image.ImageBusiness;
import ch.supsi.business.filter.FilterStrategy;
import ch.supsi.business.filter.Rotate90;
import ch.supsi.business.strategy.ThreeChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Rotate90Test {

    FilterStrategy rotateLeft;
    FilterStrategy rotateRight;

    @BeforeEach
    void setup(){
        rotateLeft = new Rotate90(false);
        rotateRight = new Rotate90(true);
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
                {0xFF000003L, 0xFF000001L},
                {0xFF000004L, 0xFF000002L}
        };

        rotateRight.applyFilter(img);
        long[][] originalArgb = img.getPixels();
        assertArrayEquals(expected, originalArgb);
    }
    @Test
    void testRotate90RightNonSquare() {
        long[][] original = {
                {1, 2, 3},
                {4, 5,  6},
        };
        ImageBusiness img = new ImageBusiness(original,"test.path", "P1", new ThreeChannel(255));

        long[][] expected ={
                {0xFF000004L, 0xFF000001L},
                {0xFF000005L, 0xFF000002L},
                {0xFF000006L, 0xFF000003L}
        };

        rotateRight.applyFilter(img);
        long[][] originalArgb = img.getPixels();
        assertArrayEquals(expected, originalArgb);
    }

    @Test
    void testEmptyImage(){
        ImageBusiness img = new ImageBusiness(new long[0][0], "path", "P1", new ThreeChannel(255));
        rotateRight.applyFilter(img);

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
}
