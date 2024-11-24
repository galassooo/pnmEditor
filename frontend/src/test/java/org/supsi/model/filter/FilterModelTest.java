package org.supsi.model.filter;

import ch.supsi.application.filters.FilterApplication;
import ch.supsi.application.image.ImageApplication;
import ch.supsi.application.image.WritableImage;
import ch.supsi.application.translations.TranslationsApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.supsi.model.filters.FilterModel;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilterModelTest {

    @Mock
    private FilterApplication mockFilterApp;
    @Mock
    private ImageApplication mockImageApp;
    @Mock
    private TranslationsApplication mockTranslationsApp;

    @Mock
    private WritableImage mockImage;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        //reset singeleton
        Field instance = FilterModel.class.getDeclaredField("mySelf");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    void testSingleton(){
        assertEquals(FilterModel.getInstance(), FilterModel.getInstance());
    }

    @Test
    void testAddFilterToPipeline() {
        try (MockedStatic<FilterApplication> mockFilterStatic = mockStatic(FilterApplication.class);
             MockedStatic<ImageApplication> mockImageStatic = mockStatic(ImageApplication.class);
             MockedStatic<TranslationsApplication> mockTransStatic = mockStatic(TranslationsApplication.class)) {

            mockFilterStatic.when(FilterApplication::getInstance).thenReturn(mockFilterApp);
            mockImageStatic.when(ImageApplication::getInstance).thenReturn(mockImageApp);
            mockTransStatic.when(TranslationsApplication::getInstance).thenReturn(mockTranslationsApp);

            when(mockTranslationsApp.translate("TestFilter")).thenReturn(Optional.of("Translated_TestFilter"));

            FilterModel filterModel = FilterModel.getInstance();
            filterModel.addFilterToPipeline("TestFilter");

            verify(mockFilterApp).addFilterToPipeline("TestFilter");
            assertEquals(1, filterModel.getFilterPipeline().size());
            assertEquals("Translated_TestFilter", filterModel.getFilterPipeline().get(0));
        }
    }

    @Test
    void testProcessFilters() {
        //mocko i campi che servono al model per funzionare
        try (MockedStatic<FilterApplication> mockFilterStatic = mockStatic(FilterApplication.class);
             MockedStatic<ImageApplication> mockImageStatic = mockStatic(ImageApplication.class);
             MockedStatic<TranslationsApplication> mockTransStatic = mockStatic(TranslationsApplication.class)) {

            //mocko il get instance per far ritornare l'istanza mockata
            mockFilterStatic.when(FilterApplication::getInstance).thenReturn(mockFilterApp);
            mockImageStatic.when(ImageApplication::getInstance).thenReturn(mockImageApp);
            mockTransStatic.when(TranslationsApplication::getInstance).thenReturn(mockTranslationsApp);

            //mocko i metodi chiamati (non posso farlo nel setup altrimenti ->Unneccessary Stubbing exception
            when(mockTranslationsApp.translate(anyString())).thenReturn(Optional.of("Translated_Filter"));
            when(mockImageApp.getCurrentImage()).thenReturn(Optional.of(mockImage));

            FilterModel filterModel = FilterModel.getInstance();
            // Verify initial state
            assertTrue(filterModel.getFilterPipeline().isEmpty());
            assertTrue(filterModel.getLastApplied().isEmpty());

            // Add filters and test
            filterModel.addFilterToPipeline("Filter1");
            filterModel.addFilterToPipeline("Filter2");
            assertEquals(2, filterModel.getFilterPipeline().size());
            assertEquals(0, filterModel.getLastApplied().size());

            // Process and verify
            filterModel.processFilters();
            verify(mockFilterApp).processFilterPipeline(mockImage);
            assertTrue(filterModel.getFilterPipeline().isEmpty());
            assertEquals(2, filterModel.getLastApplied().size());
        }
    }

    @Test
    void testProcessFiltersNoImage() {

        /*
        è quasi uguale al precedente, non posso usare i test parametrizzati poichè
        il Arguments.of(null) ritorna errore di default, e l'inizializzazione non
        posso farla nel setup.
         */

        //mocko i campi che servono al model per funzionare
        try (MockedStatic<FilterApplication> mockFilterStatic = mockStatic(FilterApplication.class);
             MockedStatic<ImageApplication> mockImageStatic = mockStatic(ImageApplication.class);
             MockedStatic<TranslationsApplication> mockTransStatic = mockStatic(TranslationsApplication.class)) {

            //mocko il get instance per far ritornare l'istanza mockata
            mockFilterStatic.when(FilterApplication::getInstance).thenReturn(mockFilterApp);
            mockImageStatic.when(ImageApplication::getInstance).thenReturn(mockImageApp);
            mockTransStatic.when(TranslationsApplication::getInstance).thenReturn(mockTranslationsApp);

            //mocko i metodi chiamati (non posso farlo nel setup altrimenti ->Unneccessary Stubbing exception
            when(mockTranslationsApp.translate(anyString())).thenReturn(Optional.of("Translated_Filter"));

            FilterModel filterModel = FilterModel.getInstance();
            // Verify initial state
            assertTrue(filterModel.getFilterPipeline().isEmpty());
            assertTrue(filterModel.getLastApplied().isEmpty());

            // Add filters and test
            filterModel.addFilterToPipeline("Filter1");
            filterModel.addFilterToPipeline("Filter2");
            assertEquals(2, filterModel.getFilterPipeline().size());
            assertEquals(0, filterModel.getLastApplied().size());

            // Process and verify
            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    filterModel::processFilters);
            assertTrue(ex.getMessage().contains("Image not set"));
        }
    }

    @Test
    void testRemoveFilter() {
        try (MockedStatic<FilterApplication> mockFilterStatic = mockStatic(FilterApplication.class);
            MockedStatic<TranslationsApplication> translationsApplicationMockedStatic = mockStatic(TranslationsApplication.class)) {

            mockFilterStatic.when(FilterApplication::getInstance).thenReturn(mockFilterApp);
            translationsApplicationMockedStatic.when(TranslationsApplication::getInstance).thenReturn(mockTranslationsApp);

            //translations
            when(mockTranslationsApp.translate("Filter1")).thenReturn(Optional.of("Filter1"));
            when(mockTranslationsApp.translate("Filter3")).thenReturn(Optional.of("Filter3"));
            when(mockTranslationsApp.translate("Filter2")).thenReturn(Optional.of("Filter2"));

            FilterModel filterModel = FilterModel.getInstance();

            //setup pipeline
            filterModel.addFilterToPipeline("Filter1");
            filterModel.addFilterToPipeline("Filter2");
            filterModel.addFilterToPipeline("Filter3");

            //remove first
            filterModel.removeFilter(1);

            //verify
            assertEquals(2, filterModel.getFilterPipeline().size());
            assertEquals("Filter1", filterModel.getFilterPipeline().get(0));
            assertEquals("Filter3", filterModel.getFilterPipeline().get(1));

            // Verify interaction with application
            verify(mockFilterApp).remove(1);
        }
    }

    @Test
    void testGetFiltersKeyValues() {
        try (MockedStatic<TranslationsApplication> mockTransStatic = mockStatic(TranslationsApplication.class);
        MockedStatic<FilterApplication> mockFilterAppStatic = mockStatic(FilterApplication.class);) {

            //static mock return mocked instance
            mockTransStatic.when(TranslationsApplication::getInstance).thenReturn(mockTranslationsApp);
            mockFilterAppStatic.when(FilterApplication::getInstance).thenReturn(mockFilterApp);

            //translationsmock
            when(mockTranslationsApp.translate("Filter1")).thenReturn(Optional.of("Translated_Filter1"));
            when(mockTranslationsApp.translate("Filter3")).thenReturn(Optional.empty());

            FilterModel filterModel = FilterModel.getInstance();

            //mock filters
            when(mockFilterApp.getAllAvailableFilters()).thenReturn(List.of("Filter1", "Filter3"));

            Map<String, String> filtersKeyValues = filterModel.getFiltersKeyValues();

            assertEquals(2, filtersKeyValues.size());
            assertEquals("Translated_Filter1", filtersKeyValues.get("Filter1"));
            assertEquals("Filter3 N/A", filtersKeyValues.get("Filter3"));
        }
    }
    @Test
    void testMoveFilter() {
        try (MockedStatic<FilterApplication> mockFilterStatic = mockStatic(FilterApplication.class);
             MockedStatic<TranslationsApplication> mockTransStatic = mockStatic(TranslationsApplication.class)) {

            mockTransStatic.when(TranslationsApplication::getInstance).thenReturn(mockTranslationsApp);
            mockFilterStatic.when(FilterApplication::getInstance).thenReturn(mockFilterApp);

            FilterModel filterModel = FilterModel.getInstance();

            when(mockTranslationsApp.translate("Filter1")).thenReturn(Optional.of("Translated_Filter1"));
            when(mockTranslationsApp.translate("Filter2")).thenReturn(Optional.of("Translated_Filter2"));
            when(mockTranslationsApp.translate("Filter3")).thenReturn(Optional.of("Translated_Filter3"));

            filterModel.addFilterToPipeline("Filter1");
            filterModel.addFilterToPipeline("Filter2");
            filterModel.addFilterToPipeline("Filter3");

            filterModel.moveFilter(0, 2);

            //verify order
            assertEquals("Translated_Filter2", filterModel.getFilterPipeline().get(0));
            assertEquals("Translated_Filter3", filterModel.getFilterPipeline().get(1));
            assertEquals("Translated_Filter1", filterModel.getFilterPipeline().get(2));

            verify(mockFilterApp).add(eq(mockFilterApp.remove(0)), eq(2));
        }
    }

    @Test
    void testMoveFilterSameIndex() {
        try (MockedStatic<FilterApplication> mockFilterStatic = mockStatic(FilterApplication.class);
             MockedStatic<TranslationsApplication> mockTransStatic = mockStatic(TranslationsApplication.class)) {

            mockTransStatic.when(TranslationsApplication::getInstance).thenReturn(mockTranslationsApp);
            mockFilterStatic.when(FilterApplication::getInstance).thenReturn(mockFilterApp);

            FilterModel filterModel = FilterModel.getInstance();

            when(mockTranslationsApp.translate("Filter1")).thenReturn(Optional.of("Translated_Filter1"));


            filterModel.addFilterToPipeline("Filter1");

            filterModel.moveFilter(0, 0);
            //nothing should happen
            assertEquals("Translated_Filter1", filterModel.getFilterPipeline().get(0));
        }
    }
}