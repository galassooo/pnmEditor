package org.supsi.view.basic;

import com.sun.javafx.scene.control.ContextMenuContent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.supsi.view.AbstractGUITest;
import org.testfx.matcher.base.NodeMatchers;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class MenuBarStartTest extends AbstractGUITest {

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
    public void stop(){
        mockedFileChooser.close();
    }

    @Test
    public void walkThrough() {
        clickOn("#root");
        testMainScene();
        testFileMenuItems();
        testEditMenuPreferences();
        testHelpMenuAbout();
        testExportMenu();
        testFilterMenu();
        testFilterLine();
    }

    private void testMainScene() {
        step("menu bar structure", () -> {
            verifyThat("#fileMenu", isVisible());
            verifyThat("#edit", isVisible());
            verifyThat("#help", isVisible());
        });
    }

    private void testFileMenuItems() {
        step("file menu disabled bindings", () -> {
            sleep(SLEEP_INTERVAL);
            clickOn("#fileMenu");
            sleep(SLEEP_INTERVAL);

            MenuItem openMenuItem = lookup("#openMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            MenuItem saveMenuItem = lookup("#saveMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            MenuItem saveAsMenuItem = lookup("#saveAsMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            MenuItem closeMenuItem = lookup("#closeMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();

            assertFalse(openMenuItem.isDisable());
            assertFalse(closeMenuItem.isDisable());
            assertTrue(saveMenuItem.isDisable());
            assertTrue(saveAsMenuItem.isDisable());

        });
    }


    private void testEditMenuPreferences() {
        step("preferences menu item", () -> {
            sleep(SLEEP_INTERVAL);
            moveTo("#edit");
            sleep(SLEEP_INTERVAL);

            Node menuContent = lookup("#preferences").query();
            MenuItem menuItem = ((ContextMenuContent.MenuItemContainer)menuContent).getItem();
            assertFalse(menuItem.isDisable());

            clickOn("#preferences");
            sleep(SLEEP_INTERVAL);

            verifyThat("#preferencesPopupRoot", NodeMatchers.isVisible());

            sleep(SLEEP_INTERVAL);
            clickOn("#preferencesPopupClose");

            sleep(SLEEP_INTERVAL);
        });
    }

    private void testHelpMenuAbout() {
        step("about dialog", () -> {
            sleep(SLEEP_INTERVAL);
            clickOn("#help");
            sleep(SLEEP_INTERVAL);
            clickOn("#about");
            sleep(SLEEP_INTERVAL);

            verifyThat("#date", isVisible());
            verifyThat("#version", isVisible());
            verifyThat("#developer", isVisible());

            clickOn("#AboutCloseButton");
            sleep(SLEEP_INTERVAL);
        });
    }
    private void testExportMenu(){
        step("export menu", () -> {
            sleep(SLEEP_INTERVAL);
            clickOn("#exportMenu");
            sleep(SLEEP_INTERVAL);

            verifyThat("#pgm", isVisible());
            verifyThat("#pbm", isVisible());
            verifyThat("#ppm", isVisible());

            MenuItem pngExport = lookup("#pgm").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            MenuItem ppmExport = lookup("#ppm").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            MenuItem pbmExport = lookup("#pbm").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();

            assertTrue(pngExport.isDisable());
            assertTrue(ppmExport.isDisable());
            assertTrue(pbmExport.isDisable());
        });
    }

    private void testFilterMenu(){
        step("filter menu", () -> {
            sleep(SLEEP_INTERVAL);
            moveTo("#filterMenu");
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

            assertTrue(negative.isDisable());
            assertTrue(rotateRight.isDisable());
            assertTrue(rotateLeft.isDisable());
            assertTrue(mirrorH.isDisable());
            assertTrue(mirrorV.isDisable());

            clickOn("#root");
        });

    }

    private void testFilterLine(){
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

            assertTrue(negative.isDisable());
            assertTrue(rotateRight.isDisable());
            assertTrue(rotateLeft.isDisable());
            assertTrue(mirrorH.isDisable());
            assertTrue(mirrorV.isDisable());
        });
    }
}
