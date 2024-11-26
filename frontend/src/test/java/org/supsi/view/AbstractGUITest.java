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

    protected static final int SLEEP_INTERVAL = 1000;

    protected static final Logger LOGGER = Logger.getAnonymousLogger();

    protected int stepNo;

    protected Stage stage;

    protected static MockedConstruction<FileChooser> mockedFileChooser;

    @BeforeAll
    public static void setupSpec() {
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("prism.order", "sw");
            System.setProperty("java.awt.headless", "true");
        }
    }

    @AfterAll
    public static void tearDownClass() {
        // Chiudiamo il mock solo dopo che tutti i test sono finiti
        if (mockedFileChooser != null) {
            mockedFileChooser.close();
        }
    }

    protected void step(final String step, final Runnable runnable) {
        ++stepNo;
        LOGGER.info("STEP" + stepNo + ":" + step);
        runnable.run();
        LOGGER.info("STEP" + stepNo + ":" + "end");
    }

    public void start(final Stage stage) throws Exception {
        mockedFileChooser = mockConstruction(FileChooser.class,
                (mock, context) -> {
                    File testFile = new File(getClass().getResource("/image.ppm").getFile());
                    System.out.println("--------------- test file exists? "+testFile.exists());
                    when(mock.showOpenDialog(any())).thenReturn(testFile);
                    when(mock.showSaveDialog(any())).thenReturn(testFile);
                });

        final MainFx main = new MainFx();
        this.stage = stage;
        main.start(stage);
        stage.toFront();
    }
}
