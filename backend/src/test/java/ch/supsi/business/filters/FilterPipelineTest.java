package ch.supsi.business.filters;

import ch.supsi.business.filter.FilterManager;
import ch.supsi.business.filter.chain.command.NegativeCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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



    }

    @Test
    void testClear(){
        filterPipeline.addFilter(new NegativeCommand());
        filterPipeline.addFilter(new NegativeCommand());
        filterPipeline.addFilter(new NegativeCommand());
        filterPipeline.addFilter(new NegativeCommand());
        filterPipeline.addFilter(new NegativeCommand());



    }
}
