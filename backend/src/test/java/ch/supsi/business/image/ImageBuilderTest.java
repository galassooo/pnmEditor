package ch.supsi.business.image;

import ch.supsi.business.strategy.SingleBit;
import ch.supsi.business.strategy.ThreeChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ImageBuilderTest {

    ImageBuilder imageBuilder;

    @BeforeEach
    void setUp() {
        imageBuilder = new ImageBuilder();
    }

    @Test
    void testBuilder() {
        long[][] data = {{1, 2}, {3, 4}};
        String magicNumber = "magicNumber";
        String path = "src/test/resources/test.png";
        imageBuilder.withPixels(data)
                .withFilePath(path)
                .withMagicNumber(magicNumber)
                .build();

        assertArrayEquals(imageBuilder.getPixels(), data);
        assertEquals(imageBuilder.getMagicNumber(), magicNumber);
        assertEquals(imageBuilder.getFilePath(), path);
    }

    @Test
    void testBuilderWithAdapter() {
        long[][] data = {{1, 2}, {3, 4}};
        String magicNumber = "magicNumber";
        String path = "src/test/resources/test.png";
        ImageAdapter adapter = new ImageAdapter(new SingleBit());
        imageBuilder.withPixels(data)
                .withFilePath(path)
                .withMagicNumber(magicNumber)
                .withImageAdapter(adapter);

        assertDoesNotThrow(imageBuilder::build);
    }

    @Test
    void testEqualsAndHashCode() {
        long[][] data = {{1, 2}, {3, 4}};
        String magicNumber = "magicNumber";
        String path = "src/test/resources/test.png";
        ImageAdapter adapter = new ImageAdapter(new SingleBit());
        imageBuilder.withPixels(data)
                .withFilePath(path)
                .withMagicNumber(magicNumber)
                .withImageAdapter(adapter)
                .build();

        ImageBuilder imageBuilder1 = new ImageBuilder();
        imageBuilder1.withPixels(data)
                .withFilePath(path)
                .withMagicNumber(magicNumber)
                .withImageAdapter(adapter)
                .build();

        assertEquals(imageBuilder, imageBuilder1);
        assertEquals(imageBuilder, imageBuilder);
        assertEquals(imageBuilder.hashCode(), imageBuilder1.hashCode());
    }

    @Test
    void testNonEqualsOnPixels() {
        long[][] data = {{1, 2}, {3, 4}};
        String magicNumber = "magicNumber";
        String path = "src/test/resources/test.png";
        ImageAdapter adapter = new ImageAdapter(new SingleBit());
        imageBuilder.withPixels(data)
                .withFilePath(path)
                .withMagicNumber(magicNumber)
                .withImageAdapter(adapter)
                .build();

        long[][] data1 = {{4, 2}, {3, 4}};


        ImageBuilder imageBuilder1 = new ImageBuilder();
        imageBuilder1.withPixels(data1)
                .withFilePath(path)
                .withMagicNumber(magicNumber)
                .withImageAdapter(adapter)
                .build();

        assertNotEquals(imageBuilder, imageBuilder1);
    }

    @Test
    void testNonEqualsOnPath() {
        long[][] data = {{1, 2}, {3, 4}};
        String magicNumber = "magicNumber";
        String path = "src/test/resources/test.png";
        ImageAdapter adapter = new ImageAdapter(new SingleBit());
        imageBuilder.withPixels(data)
                .withFilePath(path)
                .withMagicNumber(magicNumber)
                .withImageAdapter(adapter)
                .build();

        String path1 = "src/test/resources/ciao.png";

        ImageBuilder imageBuilder1 = new ImageBuilder();
        imageBuilder1.withPixels(data)
                .withFilePath(path1)
                .withMagicNumber(magicNumber)
                .withImageAdapter(adapter)
                .build();

        assertNotEquals(imageBuilder, imageBuilder1);
    }

    @Test
    void testNonEqualsOnMagicNumber() {
        long[][] data = {{1, 2}, {3, 4}};
        String magicNumber = "magicNumber";
        String path = "src/test/resources/test.png";
        ImageAdapter adapter = new ImageAdapter(new SingleBit());
        imageBuilder.withPixels(data)
                .withFilePath(path)
                .withMagicNumber(magicNumber)
                .withImageAdapter(adapter)
                .build();

        String magicNumber1 = "diverso";

        ImageBuilder imageBuilder1 = new ImageBuilder();
        imageBuilder1.withPixels(data)
                .withFilePath(path)
                .withMagicNumber(magicNumber1)
                .withImageAdapter(adapter)
                .build();

        assertNotEquals(imageBuilder, imageBuilder1);
    }

    @Test
    void testNonEqualsOnAdapter() {
        long[][] data = {{1, 2}, {3, 4}};
        String magicNumber = "magicNumber";
        String path = "src/test/resources/test.png";
        ImageAdapter adapter = new ImageAdapter(new SingleBit());
        imageBuilder.withPixels(data)
                .withFilePath(path)
                .withMagicNumber(magicNumber)
                .withImageAdapter(adapter)
                .build();

        ImageBuilder imageBuilder1 = new ImageBuilder();
        ImageAdapter adapter1 = new ImageAdapter(new ThreeChannel(255));
        imageBuilder1.withPixels(data)
                .withFilePath(path)
                .withMagicNumber(magicNumber)
                .withImageAdapter(adapter1)
                .build();

        assertNotEquals(imageBuilder, imageBuilder1);
    }

    @Test
    void testEqualsValueAdapters() {
        long[][] data = {{1, 2}, {3, 4}};
        String magicNumber = "magicNumber";
        String path = "src/test/resources/test.png";
        ImageAdapter adapter = new ImageAdapter(new ThreeChannel(255));
        imageBuilder.withPixels(data)
                .withFilePath(path)
                .withMagicNumber(magicNumber)
                .withImageAdapter(adapter)
                .build();

        ImageBuilder imageBuilder1 = new ImageBuilder();
        ImageAdapter adapter1 = new ImageAdapter(new ThreeChannel(255));
        imageBuilder1.withPixels(data)
                .withFilePath(path)
                .withMagicNumber(magicNumber)
                .withImageAdapter(adapter1)
                .build();

        assertEquals(imageBuilder, imageBuilder1);
    }

    @Test
    void testDifferentAdapters() {
        long[][] data = {{1, 2}, {3, 4}};
        String magicNumber = "magicNumber";
        String path = "src/test/resources/test.png";
        ImageAdapter adapter = new ImageAdapter(new ThreeChannel(255));
        imageBuilder.withPixels(data)
                .withFilePath(path)
                .withMagicNumber(magicNumber)
                .withImageAdapter(adapter)
                .build();

        ImageBuilder imageBuilder1 = new ImageBuilder();
        ImageAdapter adapter1 = new ImageAdapter(new ThreeChannel(326));
        imageBuilder1.withPixels(data)
                .withFilePath(path)
                .withMagicNumber(magicNumber)
                .withImageAdapter(adapter1)
                .build();

        assertNotEquals(imageBuilder, imageBuilder1);
    }

    @Test
    void testNullAdapters() {
        long[][] data = {{1, 2}, {3, 4}};
        String magicNumber = "magicNumber";
        String path = "src/test/resources/test.png";
        ImageAdapter adapter = new ImageAdapter(new ThreeChannel(255));
        imageBuilder.withPixels(data)
                .withFilePath(path)
                .withMagicNumber(magicNumber)
                .withImageAdapter(adapter)
                .build();

        ImageBuilder imageBuilder1 = new ImageBuilder();
        imageBuilder1.withPixels(data)
                .withFilePath(path)
                .withMagicNumber(magicNumber)
                .build();

        assertNotEquals(imageBuilder, imageBuilder1);
    }

    @Test
    void testDifferentAddressAdapter() {
        long[][] data = {{1, 2}, {3, 4}};
        String magicNumber = "magicNumber";
        String path = "src/test/resources/test.png";
        ImageAdapter adapter = new ImageAdapter(new SingleBit());
        imageBuilder.withPixels(data)
                .withFilePath(path)
                .withMagicNumber(magicNumber)
                .withImageAdapter(adapter)
                .build();

        ImageBuilder imageBuilder1 = new ImageBuilder();
        ImageAdapter adapter1 = new ImageAdapter(new SingleBit());
        imageBuilder1.withPixels(data)
                .withFilePath(path)
                .withMagicNumber(magicNumber)
                .withImageAdapter(adapter1)
                .build();

        assertNotEquals(imageBuilder, imageBuilder1);
    }

    @Test
    void testNonEqualsOnDifferentTypes() {
        long[][] data = {{1, 2}, {3, 4}};
        String magicNumber = "magicNumber";
        String path = "src/test/resources/test.png";
        ImageAdapter adapter = new ImageAdapter(new SingleBit());
        imageBuilder.withPixels(data)
                .withFilePath(path)
                .withMagicNumber(magicNumber)
                .withImageAdapter(adapter)
                .build();

        assertNotEquals(imageBuilder, "");
    }

    @Test
    void testBuilderWithoutPixels() {
        String magicNumber = "magicNumber";
        String path = "src/test/resources/test.png";
        ImageAdapter adapter = new ImageAdapter(new SingleBit());
        imageBuilder.withFilePath(path)
                .withMagicNumber(magicNumber)
                .withImageAdapter(adapter);

        assertDoesNotThrow(imageBuilder::build); //should use new long[0][0]
    }

    @Test
    void testBuilderWithoutMagicNumber() {
        long[][] data = {{1, 2}, {3, 4}};
        String path = "src/test/resources/test.png";
        ImageAdapter adapter = new ImageAdapter(new SingleBit());
        imageBuilder.withPixels(data)
                .withFilePath(path)
                .withImageAdapter(adapter);

        IllegalStateException ia = assertThrows(IllegalStateException.class, imageBuilder::build);
        assertEquals("MagicNumber is required", ia.getMessage());
    }

    @Test
    void testBuilderWithoutPath() {
        long[][] data = {{1, 2}, {3, 4}};
        String magicNumber = "magicNumber";
        ImageAdapter adapter = new ImageAdapter(new SingleBit());
        imageBuilder.withPixels(data)
                .withMagicNumber(magicNumber)
                .withImageAdapter(adapter);

        IllegalStateException ia = assertThrows(IllegalStateException.class, imageBuilder::build);
        assertEquals("FilePath is required", ia.getMessage());
    }

    @Test
    void testBuilderWithEmptyMagicNumber() {
        long[][] data = {{1, 2}, {3, 4}};
        String path = "src/test/resources/test.png";
        ImageAdapter adapter = new ImageAdapter(new SingleBit());
        imageBuilder.withPixels(data)
                .withFilePath(path)
                .withMagicNumber("")
                .withImageAdapter(adapter);

        IllegalStateException ia = assertThrows(IllegalStateException.class, imageBuilder::build);
        assertEquals("MagicNumber is required", ia.getMessage());
    }

    @Test
    void testBuilderWithEmptyPath() {
        long[][] data = {{1, 2}, {3, 4}};
        String magicNumber = "magicNumber";
        ImageAdapter adapter = new ImageAdapter(new SingleBit());
        imageBuilder.withPixels(data)
                .withMagicNumber(magicNumber)
                .withFilePath("")
                .withImageAdapter(adapter);

        IllegalStateException ia = assertThrows(IllegalStateException.class, imageBuilder::build);
        assertEquals("FilePath is required", ia.getMessage());
    }

}
