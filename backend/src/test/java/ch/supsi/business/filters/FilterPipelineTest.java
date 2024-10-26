package ch.supsi.business.filters;

import ch.supsi.business.filter.FilterPipeline;
import ch.supsi.business.filter.strategy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilterPipelineTest {


    FilterPipeline filterPipeline;

    @BeforeEach

    void setUp(){
        filterPipeline = FilterPipeline.getInstance();
    }
    /* --------------- singleton --------------- */
    @Test
    void singletonTest(){
        FilterPipeline filterPipeline = FilterPipeline.getInstance();
    }

    /* --------------- pipeline operations --------------- */
    @Test
    void testAdd(){
        NamedFilterStrategy expected = new MirrorFilter();
        filterPipeline.addFilter(expected);
        NamedFilterStrategy first = filterPipeline.getPipeline().get(0);
        assertEquals(expected,first );

        filterPipeline.clear();
    }

    @Test
    void testClear(){
        filterPipeline.addFilter(new NegativeFilter());
        filterPipeline.addFilter(new NegativeFilter());
        filterPipeline.addFilter(new NegativeFilter());
        filterPipeline.addFilter(new NegativeFilter());
        filterPipeline.addFilter(new NegativeFilter());

        filterPipeline.clear();

        assertEquals(0,filterPipeline.getPipeline().size());
    }

    @Test
    void testGetPipeline(){
        NamedFilterStrategy expected1 = new MirrorFilter();
        NamedFilterStrategy expected2 = new RotateRight();
        NamedFilterStrategy expected3 = new RotateLeft();

        var expected = new ArrayList<>();
        expected.add(expected1);
        expected.add(expected2);
        expected.add(expected3);

        filterPipeline.addFilter(expected1);
        filterPipeline.addFilter(expected2);
        filterPipeline.addFilter(expected3);

        assertEquals(3,filterPipeline.getPipeline().size());
        assertTrue(filterPipeline.getPipeline().containsAll(expected));
    }
}
