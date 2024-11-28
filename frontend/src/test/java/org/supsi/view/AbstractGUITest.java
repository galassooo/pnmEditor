package org.supsi.view;


import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.MockedConstruction;
import org.supsi.MainFx;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.logging.Logger;

abstract public class AbstractGUITest extends ApplicationTest {

    protected static final int SLEEP_INTERVAL = 500;

    protected static final Logger LOGGER = Logger.getAnonymousLogger();

    protected int stepNo;

    protected Stage stage;

    protected static MockedConstruction<FileChooser> mockedFileChooser;

    @BeforeAll
    public static void setupSpec() {
        if (Boolean.getBoolean("headless")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("glass.platform", "Monocle");
            System.setProperty("prism.order", "sw");
            System.setProperty("java.awt.headless", "true");
        }

        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("prism.order", "sw");
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
        this.stage = stage;
        main.start(stage);
        stage.toFront();
    }
}
