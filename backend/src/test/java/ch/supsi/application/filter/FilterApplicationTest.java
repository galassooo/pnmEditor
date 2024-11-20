package ch.supsi.application.filter;

import ch.supsi.application.filters.FilterApplication;
import ch.supsi.application.image.WritableImage;
import ch.supsi.business.filter.FilterFactory;
import ch.supsi.business.filter.FilterManager;
import ch.supsi.business.filter.chain.command.FilterCommand;
import ch.supsi.business.state.BusinessEditorState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FilterApplicationTest {

    private FilterApplication filterApplication;
    private FilterManager mockFilterManager;
    private BusinessEditorState mockStateManager;

    @BeforeEach
    void setUp() throws Exception {
        //force reset
        var field = FilterApplication.class.getDeclaredField("myself");
        field.setAccessible(true);
        field.set(null, null);

        //mock dependencies
        mockFilterManager = mock(FilterManager.class);
        mockStateManager = mock(BusinessEditorState.class);

        //mock factory
        var mockFilterFactory = mockStatic(FilterFactory.class);
        mockFilterFactory.when(FilterFactory::getFilters).thenReturn(List.of("Negative", "Mirror", "Rotate_Right", "Rotate_Left"));
        mockFilterFactory.when(() -> FilterFactory.getFilter("Mirror")).thenReturn(mock(FilterCommand.class));

        // Inject mocks
        filterApplication = FilterApplication.getInstance();
        var modelField = FilterApplication.class.getDeclaredField("model");
        modelField.setAccessible(true);
        modelField.set(filterApplication, mockFilterManager);

        var stateManagerField = FilterApplication.class.getDeclaredField("stateManager");
        stateManagerField.setAccessible(true);
        stateManagerField.set(filterApplication, mockStateManager);

        mockFilterFactory.close();
    }

    @Test
    void testSingleton() {
        FilterApplication instance = FilterApplication.getInstance();
        assertSame(filterApplication, instance, "The same instance should be returned for Singleton");
    }

    @Test
    void testAddFilter() {
        String key = "Mirror";
        FilterCommand mockFilterCommand = mock(FilterCommand.class);

        var mockFilterFactory = mockStatic(FilterFactory.class);
        mockFilterFactory.when(() -> FilterFactory.getFilter(key)).thenReturn(mockFilterCommand);

        filterApplication.addFilterToPipeline(key);

        verify(mockFilterManager, times(1)).addFilter(mockFilterCommand);
        verify(mockStateManager, times(1)).onFilterAdded();

        mockFilterFactory.close();
    }

    @Test
    void testGetAllFilters() {
        var allFilters = filterApplication.getAllAvailableFilters();
        assertFalse(allFilters.isEmpty(), "The filter list should not be empty");
        assertTrue(allFilters.contains("Negative"), "The filter list should contain 'Negative'");
        assertTrue(allFilters.contains("Mirror"), "The filter list should contain 'Mirror'");
        assertTrue(allFilters.contains("Rotate_Right"), "The filter list should contain 'Rotate_Right'");
        assertTrue(allFilters.contains("Rotate_Left"), "The filter list should contain 'Rotate_Left'");
    }

    @Test
    void testProcessEmptyPipeline() {
        WritableImage mockImage = mock(WritableImage.class);

        doThrow(new IllegalStateException("Pipeline is empty")).when(mockFilterManager).executePipeline(mockImage);

        assertThrows(IllegalStateException.class, () -> filterApplication.processFilterPipeline(mockImage), "Processing an empty pipeline should throw an exception");
        verify(mockStateManager, never()).onFilterProcessed();
    }

    @Test
    void testProcessWithPipeline() {
        WritableImage mockImage = mock(WritableImage.class);

        filterApplication.addFilterToPipeline("Mirror");
        filterApplication.processFilterPipeline(mockImage);

        verify(mockFilterManager, times(1)).executePipeline(mockImage);
        verify(mockStateManager, times(1)).onFilterProcessed();
    }

    @Test
    void testAddFilterAtIndex() {
        String key = "Rotate_Right";
        FilterCommand mockFilterCommand = mock(FilterCommand.class);

        var mockFilterFactory = mockStatic(FilterFactory.class);
        mockFilterFactory.when(() -> FilterFactory.getFilter(key)).thenReturn(mockFilterCommand);

        filterApplication.add(key, 0);

        verify(mockFilterManager, times(1)).addFilter(mockFilterCommand, 0);
        verify(mockStateManager, times(1)).onFilterAdded();

        mockFilterFactory.close();
    }

    @Test
    void testRemoveFilter() {
        when(mockFilterManager.getSize()).thenReturn(2);
        when(mockFilterManager.remove(0)).thenReturn("Mirror");

        String removedFilter = filterApplication.remove(0);

        assertEquals("Mirror", removedFilter, "The removed filter should be 'Mirror'");
        verify(mockFilterManager, times(1)).remove(0);
        verify(mockStateManager, never()).onFiltersRemoved();

        when(mockFilterManager.getSize()).thenReturn(1);
        filterApplication.remove(0);
        verify(mockStateManager, times(1)).onFiltersRemoved();
    }
}
