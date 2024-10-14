package business.filters;

import ch.supsi.business.Image.ImageBusiness;
import ch.supsi.business.filter.FilterStrategy;
import ch.supsi.business.filter.Rotate90;
import ch.supsi.business.strategy.ArgbThreeChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

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
        ImageBusiness img = new ImageBusiness(original,"test.path", "P1", new ArgbThreeChannel(255));

        long[][] expected ={
                {0xFF000002L, 0xFF000004L},
                {0xFF000001L, 0xFF000003L}
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
        ImageBusiness img = new ImageBusiness(original,"test.path", "P1", new ArgbThreeChannel(255));

        long[][] expected ={
                {0xFF000003L, 0xFF000001L},
                {0xFF000004L, 0xFF000002L}
        };

        rotateRight.applyFilter(img);
        long[][] originalArgb = img.getPixels();
        assertArrayEquals(expected, originalArgb);
    }
    @Test
    void testNullMatrix(){
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, ()->rotateRight.applyFilter(null));
        assertEquals("Cannot rotate a null image", e.getMessage());
    }

    @Test
    void testEmptyImage(){
        ImageBusiness img = new ImageBusiness(new long[0][0], "path", "P1", new ArgbThreeChannel(255));
        rotateRight.applyFilter(img);

        assertArrayEquals(new long[0][0], img.getPixels());
    }

    @Test
    void testNullPixels(){
        ImageBusiness img = new ImageBusiness(new long[0][0], "path", "P1", new ArgbThreeChannel(255));
        img.setPixels(null);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, ()->rotateRight.applyFilter(img));
        assertEquals("Cannot rotate an empty image", e.getMessage());

    }

}
