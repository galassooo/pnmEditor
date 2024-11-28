package org.supsi.view.filter;

import com.sun.javafx.scene.control.ContextMenuContent;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.supsi.model.filters.FilterModel;
import org.supsi.view.AbstractGUITest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class FilterViewTest extends AbstractGUITest {

    @Override
    public void start(Stage stage) throws Exception {
        mockedFileChooser = mockConstruction(FileChooser.class,
                (mock, context) -> {
                    File testFile = new File(getClass().getResource("/image.ppm").getFile());


                    when(mock.showOpenDialog(any())).thenReturn(testFile);
                    when(mock.showSaveDialog(any())).thenReturn(testFile);
                });
        super.start(stage);
    }

    @Override
    public void stop() throws Exception {
        mockedFileChooser.close();
        super.stop();
    }

    @Test
    void walkThrough() {
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#root");
        openImage();
        testFilterMenu();
        testFilterLine();
        testFilterAdd();
        testDragAndDrop();
        testContextMenuDelete();
        testKeyboardShortcuts();
        executeFilters();
    }

    protected void openImage() {
        step("menu file click", () -> {
            sleep(SLEEP_INTERVAL);
            clickOn("#fileMenu");
            sleep(SLEEP_INTERVAL);
            clickOn("#openMenuItem");
            sleep(SLEEP_INTERVAL);
        });
    }

    protected void testFilterMenu() {
        step("filter menu", () -> {
            sleep(SLEEP_INTERVAL);
            clickOn("#filterMenu");
            sleep(SLEEP_INTERVAL);
            clickOn("#Negative");
            sleep(SLEEP_INTERVAL);
            clickOn("#root");

            assertTrue(FilterModel.getInstance().getFilterPipeline().contains("Negative"));
        });
    }

    protected void testFilterLine() {
        step("filter line", () -> {
            sleep(SLEEP_INTERVAL);
            clickOn("#root");
            sleep(SLEEP_INTERVAL);

            verifyThat("#Negative_line", isVisible());
            verifyThat("#Rotate_Right_line", isVisible());
            verifyThat("#Rotate_Left_line", isVisible());
            verifyThat("#Mirror_Horizontal_line", isVisible());
            verifyThat("#Mirror_Vertical_line", isVisible());

            Button negative = lookup("#Negative_line").queryAs(Button.class);
            Button rotateRight = lookup("#Rotate_Right_line").queryAs(Button.class);
            Button rotateLeft = lookup("#Rotate_Left_line").queryAs(Button.class);
            Button mirrorH = lookup("#Mirror_Horizontal_line").queryAs(Button.class);
            Button mirrorV = lookup("#Mirror_Vertical_line").queryAs(Button.class);

            assertFalse(negative.isDisable());
            assertFalse(rotateRight.isDisable());
            assertFalse(rotateLeft.isDisable());
            assertFalse(mirrorH.isDisable());
            assertFalse(mirrorV.isDisable());
        });
    }

    protected void testFilterAdd() {
        step("filter add", () -> {
            sleep(SLEEP_INTERVAL);
            clickOn("#root");
            sleep(SLEEP_INTERVAL);

            // Add some filters for subsequent tests
            clickOn("#Negative_line");
            sleep(SLEEP_INTERVAL);
            clickOn("#Rotate_Right_line");
            sleep(SLEEP_INTERVAL);
            clickOn("#Mirror_Horizontal_line");
            sleep(SLEEP_INTERVAL);

            assertTrue(FilterModel.getInstance().getFilterPipeline().contains("Negative"));
            assertTrue(FilterModel.getInstance().getFilterPipeline().contains("Rotate Right"));
            assertTrue(FilterModel.getInstance().getFilterPipeline().contains("Mirror Horizontally"));
        });
    }


    protected void testContextMenuDelete() {
        step("test context menu deletion", () -> {
            sleep(SLEEP_INTERVAL);

            clickOn("#Rotate_Left_line");
            sleep(SLEEP_INTERVAL);

            assertTrue(FilterModel.getInstance().getFilterPipeline().contains("Rotate Left"));

            rightClickOn(".list-cell:nth-child(0)");
            sleep(SLEEP_INTERVAL);
            clickOn("Delete");
            sleep(SLEEP_INTERVAL);

            assertFalse(FilterModel.getInstance().getFilterPipeline().contains("Rotate Left"));

            clickOn("#Mirror_Vertical_line");
            sleep(SLEEP_INTERVAL);

            assertTrue(FilterModel.getInstance().getFilterPipeline().contains("Mirror Vertically"));
            rightClickOn(".list-cell:nth-last-child(0)");
            sleep(SLEEP_INTERVAL);
            clickOn("Delete");
            sleep(SLEEP_INTERVAL);
            assertFalse(FilterModel.getInstance().getFilterPipeline().contains("Mirror Vertically"));
        });
    }

    protected void testDragAndDrop() {
        step("test drag and drop functionality", () -> {

            Node cell = lookup(".list-cell").nth(0).query();
            moveTo(cell);
            sleep(SLEEP_INTERVAL);

            press(MouseButton.PRIMARY);
            sleep(SLEEP_INTERVAL);
            moveBy(0, 50);
            sleep(SLEEP_INTERVAL);
            release(MouseButton.PRIMARY);
            sleep(SLEEP_INTERVAL);

            cell = lookup(".list-cell").nth(0).query();
            moveTo(cell);
            sleep(SLEEP_INTERVAL);

            press(MouseButton.PRIMARY);
            sleep(SLEEP_INTERVAL);
            moveBy(0, 50);
            sleep(SLEEP_INTERVAL);
            moveBy(0, -50);
            sleep(SLEEP_INTERVAL);
            release(MouseButton.PRIMARY);
            sleep(SLEEP_INTERVAL);


            cell = lookup(".list-cell").nth(0).query();
            moveTo(cell);
            sleep(SLEEP_INTERVAL);

            press(MouseButton.PRIMARY);
            sleep(SLEEP_INTERVAL);
            moveBy(0, 120);
            sleep(SLEEP_INTERVAL);
            release(MouseButton.PRIMARY);
            sleep(SLEEP_INTERVAL);

            cell = lookup(".list-cell").nth(0).query();
            moveTo(cell);
            sleep(SLEEP_INTERVAL);

            press(MouseButton.PRIMARY);
            sleep(SLEEP_INTERVAL);
            moveBy(0, 200);
            sleep(SLEEP_INTERVAL);
            release(MouseButton.PRIMARY);
            sleep(SLEEP_INTERVAL);


            cell = lookup(".list-cell").nth(3).query();
            moveTo(cell);
            sleep(SLEEP_INTERVAL);

            press(MouseButton.PRIMARY);
            sleep(SLEEP_INTERVAL);
            moveBy(0, -110);
            sleep(SLEEP_INTERVAL);
            release(MouseButton.PRIMARY);
            sleep(SLEEP_INTERVAL);

            cell = lookup(".list-cell").nth(3).query();
            moveTo(cell);
            sleep(SLEEP_INTERVAL);

            press(MouseButton.PRIMARY);
            sleep(SLEEP_INTERVAL);
            moveBy(0, 50);
            sleep(SLEEP_INTERVAL);
            release(MouseButton.PRIMARY);
            sleep(SLEEP_INTERVAL);
        });
    }

    protected void testKeyboardShortcuts() {
        step("test keyboard shortcuts", () -> {

            moveTo("#root");
            press(KeyCode.COMMAND).press(KeyCode.C).release(KeyCode.C).release(KeyCode.COMMAND);
            sleep(SLEEP_INTERVAL);
            press(KeyCode.COMMAND).press(KeyCode.V).release(KeyCode.V).release(KeyCode.COMMAND);
            sleep(SLEEP_INTERVAL);

            clickOn("#Rotate_Left_line");
            sleep(SLEEP_INTERVAL);

            clickOn(".list-cell:nth-child(0)");
            sleep(SLEEP_INTERVAL);
            press(KeyCode.C);
            press(KeyCode.V);
            press(KeyCode.K);
            //(Windows/Linux)
            press(KeyCode.CONTROL).press(KeyCode.C).release(KeyCode.C).release(KeyCode.CONTROL);
            sleep(SLEEP_INTERVAL);
            press(KeyCode.CONTROL).press(KeyCode.V).release(KeyCode.V).release(KeyCode.CONTROL);
            sleep(SLEEP_INTERVAL);

            clickOn("#Rotate_Left_line");
            sleep(SLEEP_INTERVAL);

            clickOn(".list-cell:nth-child(0)");
            sleep(SLEEP_INTERVAL);
            //(Mac)
            press(KeyCode.COMMAND).press(KeyCode.C).release(KeyCode.C).release(KeyCode.COMMAND);
            sleep(SLEEP_INTERVAL);
            press(KeyCode.COMMAND).press(KeyCode.V).release(KeyCode.V).release(KeyCode.COMMAND);
            sleep(SLEEP_INTERVAL);
        });
    }

    void executeFilters() {
        step("test execute filters", () -> {
            clickOn("#activate");
            sleep(SLEEP_INTERVAL);

            assertTrue(FilterModel.getInstance().getFilterPipeline().isEmpty());
        });
    }
}