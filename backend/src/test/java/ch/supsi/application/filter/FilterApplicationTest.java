package ch.supsi.application.filter;

import ch.supsi.application.filters.FilterApplication;
import ch.supsi.business.filter.FilterFactory;
import ch.supsi.business.filter.FilterManager;
import ch.supsi.business.filter.command.FilterCommand;
import ch.supsi.business.image.ImageBusiness;
import ch.supsi.business.strategy.SingleBit;
import ch.supsi.business.strategy.ThreeChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FilterApplicationTest {

    private FilterApplication filterApplication;

    @BeforeEach
    public void setUp() {
        filterApplication = FilterApplication.getInstance();
    }

    /* ----------- test singleton ----------- */
    @Test
    void testSingleton(){
        FilterApplication filterApplication = FilterApplication.getInstance();
    }

    /* ----------- test methods ----------- */

    @Test
    void testAddFilter(){
        String key = "Negative";

        filterApplication.addFilterToPipeline(key);
        FilterCommand strategy = FilterFactory.getFilters().get(key);

        assertTrue(FilterManager.getInstance().getPipeline().contains(strategy));
    }

    @Test
    void testGetAllFilters(){

        var allFilters = filterApplication.getAllAvailableFilters();
        assertFalse(allFilters.isEmpty());
        assertTrue(allFilters.contains("Negative"));
        assertTrue(allFilters.contains("Mirror"));
        assertTrue(allFilters.contains("Rotate_Right"));
        assertTrue(allFilters.contains("Rotate_Left"));

    }

    @Test
    void testProcessEmptyPipeline(){

        long[][] image = {
                {1, 0},
                {0, 1},
        };

        long[][] expected = {
                {0xFF000000L, 0xFFFFFFFFL},
                {0xFFFFFFFFL, 0xFF000000L}
        };

        ImageBusiness img = new ImageBusiness(image, null, null, new SingleBit());

        filterApplication.processFilterPipeline(img);
        assertArrayEquals(expected, img.getPixels());
    }

    @Test
    void testProcessWithPipeline(){

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


        ImageBusiness img = new ImageBusiness(original, null, null, new ThreeChannel(255));

        filterApplication.addFilterToPipeline("Mirror");
        filterApplication.processFilterPipeline(img);

        //printMatrix("Expected: ",expected);
        //printMatrix("Actual: ",img.getPixels());
        assertArrayEquals(expected, img.getPixels());

    }
    private void printMatrix(String label, long[][] matrix) {
        System.out.println(label);
        for (long[] row : matrix) {
            for (long value : row) {
                System.out.printf("%08X ", value);
            }
            System.out.println();
        }
        System.out.println();
    }

}
