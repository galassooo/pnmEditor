package org.supsi.view.basic;

import com.sun.javafx.scene.control.ContextMenuContent;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.supsi.model.state.IStateModel;
import org.supsi.model.state.StateModel;
import org.supsi.model.translations.TranslationModel;
import org.supsi.view.AbstractGUITest;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

import static org.testfx.matcher.base.NodeMatchers.isDisabled;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class MenuBarTest extends AbstractGUITest {

    @Test
    public void walkThrough() {
        clickOn("#root");
        testMainScene();
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

    private void testFileMenuItems() {
        step("file menu disabled bindings", () -> {
            sleep(SLEEP_INTERVAL);
            clickOn("#fileMenu");
            sleep(SLEEP_INTERVAL);

            MenuItem saveMenuItem = lookup("#saveMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            MenuItem saveAsMenuItem = lookup("#saveAsMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();

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

            WaitForAsyncUtils.waitForFxEvents();
            MenuItem menuItem = ((ContextMenuContent.MenuItemContainer)menuContent).getItem();
            assertFalse(menuItem.isDisable());
            clickOn("#preferences");

            sleep(SLEEP_INTERVAL);
            // Get the popup stage
            TranslationModel translationModel = TranslationModel.getInstance();
            Window preferencesPopup = robotContext().getWindowFinder().window("");

            verifyThat(preferencesPopup.getScene().getRoot().lookup("#choiceBox"), isVisible());
            clickOn("#closeButton");
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
