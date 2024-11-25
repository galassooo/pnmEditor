package org.supsi.view.basic;

import com.sun.javafx.scene.control.ContextMenuContent;
import javafx.scene.control.MenuItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.supsi.view.AbstractGUITest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;

import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class MenuBarTest extends AbstractGUITest {

    @Test
    public void walkThrough() {
        testMainScene();
        testClickOnMenuItem();
    }

    void testMainScene() {
        step("menu bar structure", () -> {
            verifyThat("#fileMenu", isVisible());
            verifyThat("#edit", isVisible());
            verifyThat("#help", isVisible());
        });
    }

    void testClickOnMenuItem() {
        step("file menu", () -> {
            sleep(SLEEP_INTERVAL);
            clickOn("#fileMenu");
            sleep(SLEEP_INTERVAL);
            clickOn("#fileMenu");
            sleep(SLEEP_INTERVAL);


            sleep(SLEEP_INTERVAL);
            verifyThat("#openMenuItem", isVisible());
        });
    }
}
