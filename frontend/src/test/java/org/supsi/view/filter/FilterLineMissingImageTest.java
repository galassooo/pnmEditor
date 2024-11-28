package org.supsi.view.filter;

import com.sun.javafx.scene.control.ContextMenuContent;
import javafx.collections.FXCollections;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.supsi.model.filters.FilterModel;
import org.supsi.view.AbstractGUITest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class FilterLineMissingImageTest extends AbstractGUITest {

    private MockedStatic<FilterModel> filterModelMockedStatic;

    @Override
    public void start(Stage stage) throws Exception {

        FilterModel mockFilterModel = spy(FilterModel.getInstance());

        //setup mock behavior
        Map<String, String> filters = new HashMap<>();
        filters.put("Negative", "Negative");
        filters.put("Rotate_Right", "Rotate Right");
        filters.put("Rotate_Left", "Rotate Left");
        filters.put("Mirror_Horizontal", "Mirror Horizontal");
        filters.put("Mirror_Vertical", "Mirror Vertical");
        filters.put("Ciao", "CIAOO");

        doReturn(filters).when(mockFilterModel).getFiltersKeyValues();
        doReturn(FXCollections.observableArrayList()).when(mockFilterModel).getFilterPipeline();
        doReturn(FXCollections.observableArrayList()).when(mockFilterModel).getLastApplied();

        filterModelMockedStatic = mockStatic(FilterModel.class);
        filterModelMockedStatic.when(FilterModel::getInstance).thenReturn(mockFilterModel);

        super.start(stage);
    }

    @AfterEach
    public void cleanup() throws Exception {
        if (filterModelMockedStatic != null) {
            filterModelMockedStatic.close();
        }
        super.stop();
    }

    @Test
    void test() {
        clickOn("#root");
        testFilterLineInitialState();
        testMenuBar();
    }

    protected void testMenuBar() {
        step("filter menu", () -> {
            sleep(SLEEP_INTERVAL);
            clickOn("#filterMenu");
            sleep(SLEEP_INTERVAL);

            verifyThat("#Negative", isVisible());
            verifyThat("#Rotate_Right", isVisible());
            verifyThat("#Rotate_Left", isVisible());
            verifyThat("#Mirror_Horizontal", isVisible());
            verifyThat("#Mirror_Vertical", isVisible());

            MenuItem negative = lookup("#Negative").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            MenuItem rotateRight = lookup("#Rotate_Right").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            MenuItem rotateLeft = lookup("#Rotate_Left").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            MenuItem mirrorH = lookup("#Mirror_Horizontal").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            MenuItem mirrorV = lookup("#Mirror_Vertical").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            MenuItem ciao = lookup("#Ciao").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();

            assertTrue(negative.isDisable());
            assertTrue(rotateRight.isDisable());
            assertTrue(rotateLeft.isDisable());
            assertTrue(mirrorH.isDisable());
            assertTrue(mirrorV.isDisable());
            assertTrue(ciao.isDisable());

            clickOn("#root");
        });
    }

    private void testFilterLineInitialState() {
        step("verify filter line buttons are visible and disabled", () -> {
            sleep(SLEEP_INTERVAL);
            verifyFilterButton("Negative_line", true);
            verifyFilterButton("Rotate_Right_line", true);
            verifyFilterButton("Rotate_Left_line", true);
            verifyFilterButton("Mirror_Horizontal_line", true);
            verifyFilterButton("Mirror_Vertical_line", true);

            assertThrows(Exception.class, () -> {
                verifyFilterButton("Ciao_line", false);
            });
        });
    }

    private void verifyFilterButton(String buttonId, boolean shouldBeDisabled) {
        javafx.scene.control.Button button = lookup("#" + buttonId).queryButton();
        assertTrue(button.isVisible(), "Button should be visible");
        assertEquals(button.isDisabled(), shouldBeDisabled, String.format("Button should be %s", shouldBeDisabled ? "disabled" : "enabled"));
    }
}