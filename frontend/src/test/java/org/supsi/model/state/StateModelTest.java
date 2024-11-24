package org.supsi.model.state;

import ch.supsi.application.state.StateApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StateModelTest {

    @Mock
    private StateApplication mockStateApp;

    private StateModel stateModel;

    @BeforeEach
    void setUp() throws Exception {
        Field field = StateModel.class.getDeclaredField("myself");
        field.setAccessible(true);
        field.set(null, null);
    }

    @Test
    void testSingletonInstance() {
        StateModel instance1 = StateModel.getInstance();
        StateModel instance2 = StateModel.getInstance();

        assertEquals(instance1, instance2);
    }
    @Test
    void testCanSave(){
        try(MockedStatic<StateApplication> mockedStatic = mockStatic(StateApplication.class)){
            mockedStatic.when(StateApplication::getInstance).thenReturn(mockStateApp);

            when(mockStateApp.canSave()).thenReturn(true);
            stateModel = new StateModel();
            stateModel.onStateChange();

            assertTrue(stateModel.canSaveProperty().get());

            when(mockStateApp.canSave()).thenReturn(false);
            stateModel = new StateModel();
            stateModel.onStateChange();

            assertFalse(stateModel.canSaveProperty().get());
        }
    }

    @Test
    void testCanSaveAs(){
        try(MockedStatic<StateApplication> mockedStatic = mockStatic(StateApplication.class)){
            mockedStatic.when(StateApplication::getInstance).thenReturn(mockStateApp);

            when(mockStateApp.canSaveAs()).thenReturn(true);
            stateModel = new StateModel();
            stateModel.onStateChange();

            assertTrue(stateModel.canSaveAsProperty().get());

            when(mockStateApp.canSaveAs()).thenReturn(false);
            stateModel = new StateModel();
            stateModel.onStateChange();

            assertFalse(stateModel.canSaveAsProperty().get());
        }
    }

    @Test
    void testCanExport(){
        try(MockedStatic<StateApplication> mockedStatic = mockStatic(StateApplication.class)){
            mockedStatic.when(StateApplication::getInstance).thenReturn(mockStateApp);

            when(mockStateApp.canExport()).thenReturn(true);
            stateModel = new StateModel();
            stateModel.onStateChange();

            assertTrue(stateModel.canExportProperty().get());

            when(mockStateApp.canExport()).thenReturn(false);
            stateModel = new StateModel();
            stateModel.onStateChange();

            assertFalse(stateModel.canExportProperty().get());
        }
    }

    @Test
    void testCanApplyFilters(){
        try(MockedStatic<StateApplication> mockedStatic = mockStatic(StateApplication.class)){
            mockedStatic.when(StateApplication::getInstance).thenReturn(mockStateApp);

            when(mockStateApp.canApplyFilters()).thenReturn(true);
            stateModel = new StateModel();
            stateModel.onStateChange();

            assertTrue(stateModel.canApplyFiltersProperty().get());

            when(mockStateApp.canApplyFilters()).thenReturn(false);
            stateModel = new StateModel();
            stateModel.onStateChange();

            assertFalse(stateModel.canApplyFiltersProperty().get());
        }
    }

    @Test
    void testRefresh(){
        try(MockedStatic<StateApplication> mockedStatic = mockStatic(StateApplication.class)){
            mockedStatic.when(StateApplication::getInstance).thenReturn(mockStateApp);

            when(mockStateApp.isRefreshRequired()).thenReturn(true);
            stateModel = new StateModel();
            stateModel.onStateChange();

            assertTrue(stateModel.refreshRequiredProperty().get());

            when(mockStateApp.isRefreshRequired()).thenReturn(false);
            stateModel = new StateModel();
            stateModel.onStateChange();

            assertFalse(stateModel.refreshRequiredProperty().get());
        }
    }

    @Test
    void testAreChangesPending(){
        try(MockedStatic<StateApplication> mockedStatic = mockStatic(StateApplication.class)){
            mockedStatic.when(StateApplication::getInstance).thenReturn(mockStateApp);

            when(mockStateApp.areChangesPending()).thenReturn(true);
            stateModel = new StateModel();
            stateModel.onStateChange();

            assertTrue(stateModel.areChangesPending().get());

            when(mockStateApp.areChangesPending()).thenReturn(false);
            stateModel = new StateModel();
            stateModel.onStateChange();

            assertFalse(stateModel.areChangesPending().get());
        }
    }
}
