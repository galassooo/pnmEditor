package ch.supsi.application.filter;

import ch.supsi.application.filters.FilterApplication;
import ch.supsi.business.filter.FilterFactory;
import ch.supsi.business.filter.FilterPipeline;
import ch.supsi.business.filter.strategy.NamedFilterStrategy;
import ch.supsi.business.image.ImageBusiness;
import ch.supsi.business.strategy.SingleBit;
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
        NamedFilterStrategy strategy = FilterFactory.getFilters().get(key);

        assertTrue(FilterPipeline.getInstance().getPipeline().contains(strategy));
        filterApplication.clearPipeline();
    }

    @Test
    void testGetAllFilters(){

        var allFilters = filterApplication.getAllAvailableFilters();
        assertFalse(allFilters.isEmpty());
        assertTrue(allFilters.contains("Negative"));
        assertTrue(allFilters.contains("Mirror"));
        assertTrue(allFilters.contains("Rotate Right"));
        assertTrue(allFilters.contains("Rotate Left"));

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
        filterApplication.clearPipeline();
    }

    @Test
    void testProcessWithPipeline(){

        long[][] image = {
                {1, 0},
                {0, 1},
        };

        long[][] expected = {
                {0xFFFFFFFFL, 0xFF000000L},
                {0xFF000000L, 0xFFFFFFFFL}
        };

        ImageBusiness img = new ImageBusiness(image, null, null, new SingleBit());

        filterApplication.addFilterToPipeline("Mirror");
        filterApplication.processFilterPipeline(img);
        assertArrayEquals(expected, img.getPixels());
        filterApplication.clearPipeline();

    }
}
