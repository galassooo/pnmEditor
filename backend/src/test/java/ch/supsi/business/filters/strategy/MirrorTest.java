package ch.supsi.business.filters.strategy;

import ch.supsi.business.image.ImageBusiness;
import ch.supsi.business.filter.command.MirrorCommand;
import ch.supsi.business.strategy.ThreeChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MirrorTest {
    private MirrorCommand mirrorFilter;

    @BeforeEach
    void setup() {
        mirrorFilter = new MirrorCommand();
    }

    @Test
    void testMirrorOnSquareMatrix() {
        // Matrice quadrata 3x3
        long[][] original = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };

        long[][] expected = {
                {0xFF000003L, 0xFF000002L, 0xFF000001L},
                {0xFF000006L, 0xFF000005L, 0xFF000004L},
                {0xFF000009L, 0xFF000008L, 0xFF000007L}
        };

        ImageBusiness img = new ImageBusiness(original, "test.path", "P1", new ThreeChannel(255));
        mirrorFilter.execute(img);

        assertArrayEquals(expected, img.getPixels());
    }

    @Test
    void testMirrorOnNonSquareMatrix() {
        // Matrice rettangolare 2x3
        long[][] original = {
                {1, 2, 3},
                {4, 5, 6}
        };

        long[][] expected = {
                {0xFF000003L, 0xFF000002L, 0xFF000001L},
                {0xFF000006L, 0xFF000005L, 0xFF000004L}
        };

        ImageBusiness img = new ImageBusiness(original, "test.path", "P1", new ThreeChannel(255));
        mirrorFilter.execute(img);

        assertArrayEquals(expected, img.getPixels());
    }

    @Test
    void testMirrorOnEmptySecondDimensionMatrix() {
        // Matrice vuota
        long[][] original = new long[1][0];

        ImageBusiness img = new ImageBusiness(original, "test.path", "P1", new ThreeChannel(255));
        mirrorFilter.execute(img);

        assertEquals(0, img.getPixels()[0].length);
    }

    @Test
    void testMirrorOnEmptyMatrix() {
        // Matrice vuota
        long[][] original = new long[0][0];

        ImageBusiness img = new ImageBusiness(original, "test.path", "P1", new ThreeChannel(255));
        mirrorFilter.execute(img);

        assertEquals(0, img.getPixels().length);
    }


    @Test
    void testMirrorOnNullMatrix() {
        // Matrice vuota
        long[][] original = new long[0][0];

        ImageBusiness img = new ImageBusiness(original, "test.path", "P1", new ThreeChannel(255));
        img.setPixels(null);
        mirrorFilter.execute(img);

        assertNull(img.getPixels());
    }
    @Test
    void testGetName(){
        assertEquals("Mirror", mirrorFilter.getName());
    }
}
