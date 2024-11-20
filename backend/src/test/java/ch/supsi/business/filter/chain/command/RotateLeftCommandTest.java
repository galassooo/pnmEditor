package ch.supsi.business.filter.chain.command;

import ch.supsi.application.image.WritableImage;
import ch.supsi.business.image.ImageBuilder;
import ch.supsi.business.image.ImageBusiness;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RotateLeftCommandTest {

    ImageBusiness createMockImage(long[][] pixels){
        ImageBuilder imageBuilder = new ImageBuilder()
                .withMagicNumber("A")
                .withPixels(pixels)
                .withFilePath("path")
                .build();
        return new ImageBusiness(imageBuilder);
    }

    @Test
    void testRotateLeft() {
        RotateLeftCommand cmd = new RotateLeftCommand();
        long[][] pixels = {
                {1, 2, 3},
                {4, 5, 6}
        };
        long[][] expected = {
                {3, 6},
                {2, 5},
                {1, 4}
        };

        WritableImage img = createMockImage(pixels);
        cmd.execute(img);
        assertArrayEquals(expected, img.getPixels());
        assertEquals("Rotate_Left", cmd.getName());
    }

    @Test
    void testNullPixels() {
        RotateLeftCommand cmd = new RotateLeftCommand();
        WritableImage img = createMockImage(null);
        img.setPixels(null);
        assertDoesNotThrow(() -> cmd.execute(img));
    }


    @Test
    void testEmptyPixels() {
        RotateLeftCommand cmd = new RotateLeftCommand();
        WritableImage img = createMockImage(new long[0][]);
        assertDoesNotThrow(() -> cmd.execute(img));
    }

    @Test
    void testEmptyPixelsSecondArray() {
        RotateLeftCommand cmd = new RotateLeftCommand();
        WritableImage img = createMockImage(new long[1][0]);
        assertDoesNotThrow(() -> cmd.execute(img));
    }
}
