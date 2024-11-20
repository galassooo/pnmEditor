package ch.supsi.business.filter;

import ch.supsi.application.image.WritableImage;
import ch.supsi.business.filter.chain.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FilterManagerTest {

    private FilterManager filterManager;

    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        filterManager = FilterManager.getInstance();

        Field instanceField = FilterManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    @Test
    void testSingletonInstance() {
        FilterManager anotherInstance = FilterManager.getInstance();
        FilterManager filterManager1 = FilterManager.getInstance();
        assertSame(filterManager1, anotherInstance, "Instances should be the same for Singleton");
    }

    @Test
    void testAddFilterAndGetSize() {
        FilterCommand mockCommand = mock(FilterCommand.class);

        filterManager.addFilter(mockCommand);
        assertEquals(1, filterManager.getSize(), "Size should be 1 after adding a filter");
    }

    @Test
    void testExecutePipeline() {
        WritableImage mockImage = mock(WritableImage.class);
        FilterCommand mockCommand = mock(FilterCommand.class);

        filterManager.addFilter(mockCommand);
        filterManager.executePipeline(mockImage);

        verify(mockCommand).execute(mockImage);
        assertEquals(0, filterManager.getSize(), "Size should be 0 after executing the pipeline");
    }

    @Test
    void testAddFilterAtSpecificIndex() {
        FilterCommand firstCommand = mock(FilterCommand.class);
        FilterCommand secondCommand = mock(FilterCommand.class);

        filterManager.addFilter(firstCommand);
        filterManager.addFilter(secondCommand, 0);

        assertEquals(2, filterManager.getSize(), "Size should be 2 after adding two filters");
    }

    @Test
    void testRemoveFilter() {
        FilterCommand mockCommand = new NegativeCommand();
        FilterCommand anotherMockCommand = new HorizontalMirrorCommand();

        filterManager.addFilter(mockCommand);
        filterManager.addFilter(anotherMockCommand);

        String removedFilterName = filterManager.remove(0);

        assertNotNull(removedFilterName, "Removed filter name should not be null");
        assertEquals(1, filterManager.getSize(), "Size should be 1 after removing a filter");
    }

    @Test
    void testRemoveInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> filterManager.remove(0), "Should throw an exception for invalid index");
    }

    @Test
    void testAddFilterInvalidIndex() {
        FilterCommand mockCommand = mock(FilterCommand.class);

        assertThrows(IndexOutOfBoundsException.class, () -> filterManager.addFilter(mockCommand, -1), "Should throw an exception for invalid index");
    }

    @Test
    void testExecuteDuplicateCommands(){
        FilterCommand firstCommand = new HorizontalMirrorCommand();
        FilterCommand secondCommand = new HorizontalMirrorCommand();

        filterManager.addFilter(firstCommand);
        filterManager.addFilter(secondCommand);

        assertEquals(2, filterManager.getSize(), "Size should be 2 after adding two filtesr");
        assertDoesNotThrow(() -> filterManager.executePipeline(mock(WritableImage.class)));
    }

    @Test
    void testPipelineExecutionWithNullImage() {
        FilterCommand mockCommand = mock(FilterCommand.class);
        filterManager.addFilter(mockCommand);

        filterManager.executePipeline(null);

        verify(mockCommand, never()).execute(any());
        assertEquals(1, filterManager.getSize(), "Size should remain unchanged when executing with null image");
    }

    @Test
    void testAddDifferentIndex(){
        FilterCommand firstCommand = new HorizontalMirrorCommand();
        FilterCommand secondCommand = new RotateLeftCommand();
        FilterCommand thirdCommand = new NegativeCommand();
        FilterCommand fourthCommand = new HorizontalMirrorCommand();

        filterManager.addFilter(firstCommand);
        filterManager.addFilter(secondCommand);
        filterManager.addFilter(thirdCommand,1);
        filterManager.addFilter(fourthCommand, 2);

        assertEquals(4, filterManager.getSize(), "Size should be 4 after adding four filtesr");
        assertDoesNotThrow(() -> filterManager.executePipeline(mock(WritableImage.class)));
    }

    @Test
    void testExecuteEmptyPipeline(){
        assertDoesNotThrow(() -> filterManager.executePipeline(mock(WritableImage.class)));
    }

    @Test
    void testAddFilterOutOfBounds(){
        filterManager.addFilter(new HorizontalMirrorCommand());
        filterManager.addFilter(new RotateLeftCommand());

        assertThrows(IndexOutOfBoundsException.class, ()-> filterManager.addFilter(new HorizontalMirrorCommand(), 5));
    }

    @ParameterizedTest //testa tutti i valori sotto con un unico test
    @MethodSource("getFilters")
    void TestInsertionWithoutIndex(FilterCommand filterCommand1, FilterCommand filterCommand2, FilterCommand filterCommand3){
        assertDoesNotThrow(() -> filterManager.addFilter(filterCommand1));
        assertDoesNotThrow(() -> filterManager.addFilter(filterCommand2));
        assertDoesNotThrow(() -> filterManager.addFilter(filterCommand3));
    }

    private static Stream<Arguments> getFilters() {
        return Stream.of(
                Arguments.of(new HorizontalMirrorCommand(), new HorizontalMirrorCommand(), new HorizontalMirrorCommand(),
                Arguments.of(new RotateLeftCommand(), new RotateRightCommand(), new RotateLeftCommand()),
                Arguments.of(new NegativeCommand()), new NegativeCommand(), new VerticalMirrorCommand(),
                Arguments.of(new RotateLeftCommand(), new HorizontalMirrorCommand(), new NegativeCommand())
                )
        );
    }

    @Test
    void testRemoveNegativeIndex(){
        IndexOutOfBoundsException ex = assertThrows(IndexOutOfBoundsException.class, () -> filterManager.remove(-2));
        assertEquals("invalid index", ex.getMessage());
    }

    @Test
    void testRemoveOnMiddle(){
        filterManager.addFilter(new HorizontalMirrorCommand());
        filterManager.addFilter(new RotateLeftCommand());
        filterManager.addFilter(new RotateRightCommand());
        filterManager.addFilter(new HorizontalMirrorCommand());
        filterManager.addFilter(new RotateLeftCommand());

        assertDoesNotThrow(() -> filterManager.remove(3));
    }

    @Test
    void testRemoveOutOfBounds(){
        filterManager.addFilter(new HorizontalMirrorCommand());
        filterManager.addFilter(new RotateLeftCommand());
        filterManager.addFilter(new RotateRightCommand());

        IndexOutOfBoundsException ex = assertThrows(IndexOutOfBoundsException.class, () -> filterManager.remove(100));
        assertEquals("invalid index", ex.getMessage());
    }
}

