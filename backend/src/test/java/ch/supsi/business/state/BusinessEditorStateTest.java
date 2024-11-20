package ch.supsi.business.state;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import ch.supsi.application.state.StateChangeListener;

public class BusinessEditorStateTest {
    private BusinessEditorState state;
    private boolean listenerCalled;
    private StateChangeListener testListener;

    @BeforeEach
    void setUp() {
        state = BusinessEditorState.getInstance();
        listenerCalled = false;
        testListener = () -> listenerCalled = true;

        state.registerStateListener(testListener);
    }
    @AfterEach
    void tearDown() {
        state.deregisterStateListener(testListener);
    }

    @Test
    void testImageLoadedState() {
        state.onImageLoaded();
        assertTrue(listenerCalled);
        assertTrue(state.canApplyFilters());
        assertTrue(state.canSave());
        assertTrue(state.canSaveAs());
        assertTrue(state.canAddFilter());
        assertTrue(state.canExport());
        assertTrue(state.isRefreshRequired());
        assertFalse(state.areChangesPending());
    }

    @Test
    void testFilterPendingState() {
        state.onFilterAdded();
        assertTrue(listenerCalled);
        assertTrue(state.canApplyFilters());
        assertTrue(state.canSave());
        assertTrue(state.canSaveAs());
        assertTrue(state.canAddFilter());
        assertTrue(state.canExport());
        assertFalse(state.isRefreshRequired());
        assertTrue(state.areChangesPending());
    }

    @Test
    void testLoadingState() {
        state.onLoading();
        assertTrue(listenerCalled);
        assertFalse(state.canApplyFilters());
        assertFalse(state.canSave());
        assertFalse(state.canSaveAs());
        assertFalse(state.canAddFilter());
        assertFalse(state.canExport());
        assertFalse(state.isRefreshRequired());
        assertTrue(state.areChangesPending());
    }

    @Test
    void testEditedImageState() {
        state.onFilterProcessed();
        assertTrue(listenerCalled);
        assertTrue(state.canApplyFilters());
        assertTrue(state.canSave());
        assertTrue(state.canSaveAs());
        assertTrue(state.canAddFilter());
        assertTrue(state.canExport());
        assertTrue(state.isRefreshRequired());
        assertTrue(state.areChangesPending());
    }

    @Test
    void testLoadingErrorState() {
        state.onLoadingError();
        assertTrue(listenerCalled);
        assertFalse(state.canApplyFilters());
        assertFalse(state.canSave());
        assertFalse(state.canSaveAs());
        assertFalse(state.canAddFilter());
        assertFalse(state.canExport());
        assertFalse(state.isRefreshRequired());
        assertFalse(state.areChangesPending());
    }

    @Test
    void testFiltersRemovedState() {
        state.onFiltersRemoved();
        assertTrue(listenerCalled);
        assertTrue(state.canApplyFilters());
        assertTrue(state.canSave());
        assertTrue(state.canSaveAs());
        assertTrue(state.canAddFilter());
        assertTrue(state.canExport());
        assertTrue(state.isRefreshRequired());
        assertFalse(state.areChangesPending());
    }

    @Test
    void testStateListenerRegistration() {
        StateChangeListener listener = () -> {};
        state.registerStateListener(listener);
        state.deregisterStateListener(listener);
        // If no exception is thrown, test passes
    }

    @Test
    void testSingleton() {
        BusinessEditorState instance1 = BusinessEditorState.getInstance();
        BusinessEditorState instance2 = BusinessEditorState.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    void testStateTransitions() {
        state.onImageLoaded();
        assertTrue(state.canAddFilter());

        state.onFilterAdded();
        assertTrue(state.areChangesPending());

        state.onFilterProcessed();
        assertTrue(state.isRefreshRequired());
        assertTrue(state.areChangesPending());

        state.onFiltersRemoved();
        assertTrue(state.isRefreshRequired());
        assertFalse(state.areChangesPending());
    }
}