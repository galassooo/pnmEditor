package ch.supsi.application.state;

import ch.supsi.business.state.BusinessEditorState;
import ch.supsi.business.state.StateChangeEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StateApplicationTest {

    private StateApplication stateApplication;
    private BusinessEditorState mockBusinessEditorState;

    @BeforeEach
    void setUp() throws Exception {
        // Resetta il Singleton
        var field = StateApplication.class.getDeclaredField("myself");
        field.setAccessible(true);
        field.set(null, null);

        // Crea un'istanza mock di BusinessEditorState
        mockBusinessEditorState = mock(BusinessEditorState.class);

        // Usa reflection per impostare il mock nel Singleton
        stateApplication = StateApplication.getInstance();
        var businessStateField = StateApplication.class.getDeclaredField("businessEditorState");
        businessStateField.setAccessible(true);
        businessStateField.set(stateApplication, mockBusinessEditorState);
    }

    @Test
    void testSingleton() {
        StateApplication instance = StateApplication.getInstance();
        assertSame(stateApplication, instance, "The same instance should be returned for Singleton");
    }

    @Test
    void testCanApplyFilters() {
        when(mockBusinessEditorState.canApplyFilters()).thenReturn(true);
        assertTrue(stateApplication.canApplyFilters(), "canApplyFilters should return true");

        when(mockBusinessEditorState.canApplyFilters()).thenReturn(false);
        assertFalse(stateApplication.canApplyFilters(), "canApplyFilters should return false");
    }

    @Test
    void testCanSave() {
        when(mockBusinessEditorState.canSave()).thenReturn(true);
        assertTrue(stateApplication.canSave(), "canSave should return true");

        when(mockBusinessEditorState.canSave()).thenReturn(false);
        assertFalse(stateApplication.canSave(), "canSave should return false");
    }

    @Test
    void testCanSaveAs() {
        when(mockBusinessEditorState.canSaveAs()).thenReturn(true);
        assertTrue(stateApplication.canSaveAs(), "canSaveAs should return true");

        when(mockBusinessEditorState.canSaveAs()).thenReturn(false);
        assertFalse(stateApplication.canSaveAs(), "canSaveAs should return false");
    }

    @Test
    void testCanAddFilter() {
        when(mockBusinessEditorState.canAddFilter()).thenReturn(true);
        assertTrue(stateApplication.canAddFilter(), "canAddFilter should return true");

        when(mockBusinessEditorState.canAddFilter()).thenReturn(false);
        assertFalse(stateApplication.canAddFilter(), "canAddFilter should return false");
    }

    @Test
    void testCanExport() {
        when(mockBusinessEditorState.canExport()).thenReturn(true);
        assertTrue(stateApplication.canExport(), "canExport should return true");

        when(mockBusinessEditorState.canExport()).thenReturn(false);
        assertFalse(stateApplication.canExport(), "canExport should return false");
    }

    @Test
    void testIsRefreshRequired() {
        when(mockBusinessEditorState.isRefreshRequired()).thenReturn(true);
        assertTrue(stateApplication.isRefreshRequired(), "isRefreshRequired should return true");

        when(mockBusinessEditorState.isRefreshRequired()).thenReturn(false);
        assertFalse(stateApplication.isRefreshRequired(), "isRefreshRequired should return false");
    }

    @Test
    void testAreChangesPending() {
        when(mockBusinessEditorState.areChangesPending()).thenReturn(true);
        assertTrue(stateApplication.areChangesPending(), "areChangesPending should return true");

        when(mockBusinessEditorState.areChangesPending()).thenReturn(false);
        assertFalse(stateApplication.areChangesPending(), "areChangesPending should return false");
    }

    @Test
    void testRegisterStateListener() {
        StateChangeListener mockListener = mock(StateChangeListener.class);

        stateApplication.registerStateListener(mockListener);

        verify((StateChangeEvent) mockBusinessEditorState, times(1)).registerStateListener(mockListener);
    }

    @Test
    void testDeregisterStateListener() {
        StateChangeListener mockListener = mock(StateChangeListener.class);

        stateApplication.deregisterStateListener(mockListener);

        verify((StateChangeEvent) mockBusinessEditorState, times(1)).deregisterStateListener(mockListener);
    }
}
