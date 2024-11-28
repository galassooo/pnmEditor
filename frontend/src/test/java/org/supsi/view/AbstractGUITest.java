package org.supsi.view;


import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockedConstruction;
import org.supsi.MainFx;
import org.testfx.framework.junit5.ApplicationTest;

import javax.sound.midi.Soundbank;
import java.io.File;
import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

abstract public class AbstractGUITest extends ApplicationTest {

    protected static final int SLEEP_INTERVAL = 500;

    protected static final Logger LOGGER = Logger.getAnonymousLogger();

    protected int stepNo;

    protected Stage stage;


    protected boolean requestErrorImage = false;
    protected static MockedConstruction<FileChooser> mockedFileChooser;

    @BeforeAll
    public static void setupSpec() {
        if (Boolean.getBoolean("headless")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("glass.platform", "Monocle");
            System.setProperty("monocle.platform", "Headless");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
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
