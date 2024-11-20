package ch.supsi.business.filter.chain.command;

import ch.supsi.application.image.WritableImage;
import ch.supsi.business.image.ImageBuilder;
import ch.supsi.business.image.ImageBusiness;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NegativeCommandTest {

    ImageBusiness createMockImage(long[][] pixels){
        ImageBuilder imageBuilder = new ImageBuilder()
                .withMagicNumber("A")
                .withPixels(pixels)
                .withFilePath("path")
                .build();
        return new ImageBusiness(imageBuilder);
    }
    @Test
    void testNegativeCommand() {
        NegativeCommand cmd = new NegativeCommand();
        long[][] pixels = {
                {0xFFFFFF, 0x000000},
                {0x808080, 0x123456}
        };
        long[][] expected = {
                {0x000000, 0xFFFFFF},
                {0x7F7F7F, 0xEDCBA9}
        };

        WritableImage img = createMockImage(pixels);
        cmd.execute(img);
        assertArrayEquals(expected, img.getPixels());
        assertEquals("Negative", cmd.getName());
    }

    @Test
    void testNullPixels() {
        NegativeCommand cmd = new NegativeCommand();
        WritableImage img = createMockImage(null);
        img.setPixels(null);
        assertDoesNotThrow(() -> cmd.execute(img));
    }

    @Test
    void testEmptyPixels() {
        NegativeCommand cmd = new NegativeCommand();
        WritableImage img = createMockImage(new long[0][]);
        assertDoesNotThrow(() -> cmd.execute(img));
    }

    @Test
    void testEmptySecondArrayPixels() {
        NegativeCommand cmd = new NegativeCommand();
        WritableImage img = createMockImage(new long[1][0]);
        assertDoesNotThrow(() -> cmd.execute(img));
    }
}