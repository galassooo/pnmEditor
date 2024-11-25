package org.supsi.view.basic;

import com.sun.javafx.scene.control.ContextMenuContent;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.supsi.model.state.IStateModel;
import org.supsi.model.state.StateModel;
import org.supsi.view.AbstractGUITest;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;

import static org.testfx.matcher.base.NodeMatchers.isDisabled;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class MenuBarTest extends AbstractGUITest {

    @Test
    public void walkThrough() {
        testMainScene();
        testClickOnMenuItem();
        testFileMenuItems();
        testEditMenuPreferences();
        testHelpMenuAbout();
        testExportMenuInitialState();
    }

    private void testMainScene() {
        step("menu bar structure", () -> {
            verifyThat("#fileMenu", isVisible());
            verifyThat("#edit", isVisible());
            verifyThat("#help", isVisible());
        });
    }

    private void testClickOnMenuItem() {
        step("file menu", () -> {
            clickOn("#root");
            sleep(SLEEP_INTERVAL);
            clickOn("#fileMenu");
            sleep(SLEEP_INTERVAL);


            sleep(SLEEP_INTERVAL);
            verifyThat("#openMenuItem", isVisible());

            press(KeyCode.ESCAPE);
        });
    }

    private void testFileMenuItems() {
        step("file menu disabled bindings", () -> {
            sleep(SLEEP_INTERVAL);
            clickOn("#fileMenu");
            sleep(SLEEP_INTERVAL);

            MenuItem saveMenuItem = lookup("#saveMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            MenuItem saveAsMenuItem = lookup("#saveAsMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();

            assertTrue(saveMenuItem.isDisable());
            assertTrue(saveAsMenuItem.isDisable());

            press(KeyCode.ESCAPE);
        });
    }


    private void testEditMenuPreferences() {
        step("preferences dialog", () -> {
            clickOn("#edit");
            clickOn("#preferences");

            // Verify preferences dialog components
            verifyThat("#choiceBox", isVisible());
            verifyThat("#errorCB", isVisible());
            verifyThat("#warningCB", isVisible());
            verifyThat("#debugCB", isVisible());

            clickOn("#closeButton");
            press(KeyCode.ESCAPE);
        });
    }

    private void testHelpMenuAbout() {
        step("about dialog", () -> {
            clickOn("#help");
            clickOn("About");

            verifyThat("#date", isVisible());
            verifyThat("#version", isVisible());
            verifyThat("#developer", isVisible());

            clickOn("#closeButton");
            press(KeyCode.ESCAPE);
        });
    }

    private void testExportMenuInitialState() {
        step("export menu initial disable state", () -> {
            clickOn("#fileMenu");

            // Should be disabled initially with no image loaded
            verifyThat("#exportMenu", isDisabled());

            press(KeyCode.ESCAPE);
        });
    }

}
