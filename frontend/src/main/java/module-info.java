open module frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.prefs;
    requires backend;
    requires spring.jcl;
    requires java.logging;

    // Test requirements
    requires static org.testfx;
    requires static org.testfx.junit5;

    exports org.supsi.view;


    // Required for TestFX
    exports org.supsi to org.testfx;
}
