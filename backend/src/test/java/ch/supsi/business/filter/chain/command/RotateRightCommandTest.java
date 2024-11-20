package ch.supsi.business.filter.chain.command;

import ch.supsi.application.image.WritableImage;
import ch.supsi.business.image.ImageBuilder;
import ch.supsi.business.image.ImageBusiness;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class  RotateRightCommandTest {

    ImageBusiness createMockImage(long[][] pixels){
        ImageBuilder imageBuilder = new ImageBuilder()
                .withMagicNumber("A")
                .withPixels(pixels)
                .withFilePath("path")
                .build();
        return new ImageBusiness(imageBuilder);
    }

    @Test
    void testRotateRight() {
        RotateRightCommand cmd = new RotateRightCommand();
        long[][] pixels = {
                {1, 2, 3},
                {4, 5, 6}
        };
        long[][] expected = {
                {4, 1},
                {5, 2},
                {6, 3}
        };

        WritableImage img = createMockImage(pixels);
        cmd.execute(img);
        assertArrayEquals(expected, img.getPixels());
        assertEquals("Rotate_Right", cmd.getName());
    }

    @Test
    void testNullPixels() {
        RotateRightCommand cmd = new RotateRightCommand();
        WritableImage img = createMockImage(null);
        img.setPixels(null);
        assertDoesNotThrow(() -> cmd.execute(img));
    }

    @Test
    void testEmptyPixels() {
        RotateRightCommand cmd = new RotateRightCommand();
        WritableImage img = createMockImage(new long[0][]);
        assertDoesNotThrow(() -> cmd.execute(img));
    }
    @Test
    void testEmptyPixelsSecondArray() {
        RotateRightCommand cmd = new RotateRightCommand();
        WritableImage img = createMockImage(new long[1][0]);
        assertDoesNotThrow(() -> cmd.execute(img));
    }
}