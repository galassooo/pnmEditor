package org.supsi.view;


import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.supsi.MainFx;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.logging.Logger;

abstract public class AbstractGUITest extends ApplicationTest {

    protected static final int SLEEP_INTERVAL = 2000;

    protected static final Logger LOGGER = Logger.getAnonymousLogger();

    protected int stepNo;


    @BeforeAll
    public static void setupSpec() {
        if (Boolean.getBoolean("headless")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
        }
    }

    protected void step(final String step, final Runnable runnable) {
        ++stepNo;
        LOGGER.info("STEP" + stepNo + ":" + step);
        runnable.run();
        LOGGER.info("STEP" + stepNo + ":" + "end");
    }

    public void start(final Stage stage) throws Exception {
            final MainFx main = new MainFx();
            stage.toFront();
            stage.setAlwaysOnTop(true);
            main.start(stage);
    }
}
