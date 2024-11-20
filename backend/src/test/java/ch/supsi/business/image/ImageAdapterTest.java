package ch.supsi.business.image;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ch.supsi.business.strategy.ConvertStrategy;
import static org.junit.jupiter.api.Assertions.*;

class ImageAdapterTest {

    private ImageAdapter adapter;
    private ConvertStrategy mockStrategy;

    @BeforeEach
    void setUp() {
        //mock convert strategy (it is that simple that using mocks with mockito
        // sarebbe stato piu complicato inutilmente )
        mockStrategy = new ConvertStrategy() {
            @Override
            public long toArgb(long pixel) {
                return pixel * 2;
            }

            @Override
            public long ArgbToOriginal(long pixel) {
                return pixel / 2;
            }
        };
        adapter = new ImageAdapter(mockStrategy);
    }

    @Test
    void testRawToArgbWithValidInput() {
        long[][] input = {{1, 2, 3}, {4, 5, 6}};
        long[][] expected = {{2, 4, 6}, {8, 10, 12}};

        long[][] result = adapter.rawToArgb(input);

        assertArrayEquals(expected, result);
    }

    @Test
    void testArgbToRawWithValidInput() {
        long[][] input = {{2, 4, 6}, {8, 10, 12}};
        long[][] expected = {{1, 2, 3}, {4, 5, 6}};

        long[][] result = adapter.argbToRaw(input);

        assertArrayEquals(expected, result);
    }

    @Test
    void testRawToArgbWithNullInput() {
        long[][] result = adapter.rawToArgb(null);

        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void testArgbToRawWithNullInput() {
        long[][] result = adapter.argbToRaw(null);

        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void testRawToArgbWithEmptyInput() {
        long[][] input = new long[0][];
        long[][] result = adapter.rawToArgb(input);

        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void testArgbToRawWithEmptyInput() {
        long[][] input = new long[0][];
        long[][] result = adapter.argbToRaw(input);

        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void testEqualsAndHashCode() {
        ImageAdapter adapter1 = new ImageAdapter(mockStrategy);
        ImageAdapter adapter2 = new ImageAdapter(mockStrategy);
        ImageAdapter adapter3 = new ImageAdapter(new ConvertStrategy() {
            @Override
            public long toArgb(long pixel) {
                return pixel;
            }

            @Override
            public long ArgbToOriginal(long pixel) {
                return pixel;
            }
        });

        // Test equals
        assertEquals(adapter1, adapter2);
        assertEquals(adapter2, adapter1);
        assertNotEquals(adapter1, adapter3);
        assertNotEquals(null, adapter1);
        assertNotEquals(adapter1, new Object());

        // Test hashCode
        assertEquals(adapter1.hashCode(), adapter2.hashCode());
        assertNotEquals(adapter1.hashCode(), adapter3.hashCode());
    }

    @Test
    void testProcessingLargeImage() {
        int size = 1000;
        long[][] input = new long[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                input[i][j] = i + j;
            }
        }

        long[][] result = adapter.rawToArgb(input);

        assertNotNull(result);
        assertEquals(size, result.length);
        assertEquals(size, result[0].length);
        assertEquals(0, result[0][0]);
        assertEquals(2 * (size - 1 + size - 1), result[size - 1][size - 1]);
    }

    @Test
    void testEqualsSameObj() {
        assertEquals(adapter, adapter);
    }
}