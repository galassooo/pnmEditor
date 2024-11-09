package ch.supsi.business.filters;

import ch.supsi.business.filter.FilterManager;
import ch.supsi.business.filter.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilterPipelineTest {


    FilterManager filterPipeline;

    @BeforeEach

    void setUp(){
        filterPipeline = FilterManager.getInstance();
    }
    /* --------------- singleton --------------- */
    @Test
    void singletonTest(){
        FilterManager filterPipeline = FilterManager.getInstance();
    }

    /* --------------- pipeline operations --------------- */
    @Test
    void testAdd(){
        FilterCommand expected = new MirrorCommand();
        filterPipeline.addFilter(expected);
        FilterCommand first = filterPipeline.getPipeline().get(0);
        assertEquals(expected,first );


    }

    @Test
    void testClear(){
        filterPipeline.addFilter(new NegativeCommand());
        filterPipeline.addFilter(new NegativeCommand());
        filterPipeline.addFilter(new NegativeCommand());
        filterPipeline.addFilter(new NegativeCommand());
        filterPipeline.addFilter(new NegativeCommand());



        assertEquals(0,filterPipeline.getPipeline().size());
    }
}
