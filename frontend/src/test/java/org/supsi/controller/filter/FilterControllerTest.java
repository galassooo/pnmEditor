package org.supsi.controller.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.supsi.model.event.EventManager;
import org.supsi.model.event.EventSubscriber;
import org.supsi.model.filters.FilterModel;
import org.supsi.model.image.ImageModel;
import org.supsi.model.info.LoggerModel;
import org.supsi.view.filter.FilterEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FilterControllerTest {

    @Mock
    private FilterModel mockFilterModel;

    @Mock
    private LoggerModel mockLoggerModel;

    @Mock
    private ImageModel mockImageModel;

    @Mock
    private EventSubscriber mockEventSubscriber;

    @BeforeEach
    public void setUp() throws Exception {
        //reset singleton
        var field = FilterController.class.getDeclaredField("myself");
        field.setAccessible(true);
        field.set(null, null);
    }

    @Test
    void testSingleton() {
        try (MockedStatic<FilterModel> filterModelMock = mockStatic(FilterModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedStatic<EventManager> eventManagerMock = mockStatic(EventManager.class)) {

            filterModelMock.when(FilterModel::getInstance).thenReturn(mockFilterModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);
            eventManagerMock.when(EventManager::getSubscriber).thenReturn(mockEventSubscriber);

            FilterController controller1 = FilterController.getInstance();
            FilterController controller2 = FilterController.getInstance();

            assertEquals(controller1, controller2); // Singleton test
        }
    }

    @Test
    void testOnFilterAddedNotNullImage() {
        try (MockedStatic<FilterModel> filterModelMock = mockStatic(FilterModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedStatic<ImageModel> imageModelMock = mockStatic(ImageModel.class)) {

            filterModelMock.when(FilterModel::getInstance).thenReturn(mockFilterModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);
            imageModelMock.when(ImageModel::getInstance).thenReturn(mockImageModel);

            when(mockImageModel.getImageName()).thenReturn("simpleName"); // Simulate no image loaded

            FilterController controller = FilterController.getInstance();
            FilterEvent.FilterAddRequested event = new FilterEvent.FilterAddRequested("BlurFilter");

            controller.onFilterAdded(event);

            verify(mockLoggerModel).addDebug("ui_filter_added");
            verify(mockFilterModel).addFilterToPipeline("BlurFilter");
        }
    }

    @Test
    void testOnFilterAddedNullImage() {
        try (MockedStatic<FilterModel> filterModelMock = mockStatic(FilterModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class);
             MockedStatic<ImageModel> imageModelMock = mockStatic(ImageModel.class)) {

            filterModelMock.when(FilterModel::getInstance).thenReturn(mockFilterModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);
            imageModelMock.when(ImageModel::getInstance).thenReturn(mockImageModel);

            when(mockImageModel.getImageName()).thenReturn(null); // Simulate no image loaded

            FilterController controller = FilterController.getInstance();
            FilterEvent.FilterAddRequested event = new FilterEvent.FilterAddRequested("BlurFilter");

            controller.onFilterAdded(event);

            verify(mockLoggerModel).addWarning("ui_no_image_added");
            verify(mockLoggerModel).addDebug("ui_filter_added");
            verify(mockFilterModel).addFilterToPipeline("BlurFilter");
        }
    }

    @Test
    void testOnFilterMoved() {
        try (MockedStatic<FilterModel> filterModelMock = mockStatic(FilterModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class)) {

            filterModelMock.when(FilterModel::getInstance).thenReturn(mockFilterModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);

            FilterController controller = FilterController.getInstance();
            FilterEvent.FilterMoveRequested event = new FilterEvent.FilterMoveRequested(2, 5);

            controller.onFilterMoved(event);

            verify(mockLoggerModel).addDebug("ui_filter_moved");
            verify(mockFilterModel).moveFilter(2, 5);
        }
    }

    @Test
    void testOnFiltersActivated() {
        try (MockedStatic<FilterModel> filterModelMock = mockStatic(FilterModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class)) {

            filterModelMock.when(FilterModel::getInstance).thenReturn(mockFilterModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);

            FilterController controller = FilterController.getInstance();
            FilterEvent.FilterExecutionRequested event = new FilterEvent.FilterExecutionRequested();

            controller.onFiltersActivated(event);

            verify(mockLoggerModel).addInfo("ui_pipeline_processed");
            verify(mockFilterModel).processFilters();
        }
    }

    @Test
    void testOnFilterRemoved() {
        try (MockedStatic<FilterModel> filterModelMock = mockStatic(FilterModel.class);
             MockedStatic<LoggerModel> loggerModelMock = mockStatic(LoggerModel.class)) {

            filterModelMock.when(FilterModel::getInstance).thenReturn(mockFilterModel);
            loggerModelMock.when(LoggerModel::getInstance).thenReturn(mockLoggerModel);

            FilterController controller = FilterController.getInstance();
            FilterEvent.FilterRemoveRequested event = new FilterEvent.FilterRemoveRequested(3);

            controller.onFilterRemoved(event);

            verify(mockLoggerModel).addDebug("ui_filter_removed");
            verify(mockFilterModel).removeFilter(3);
        }
    }
}
