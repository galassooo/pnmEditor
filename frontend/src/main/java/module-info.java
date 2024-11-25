open module frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.prefs;
    requires backend;
    requires spring.jcl;
    requires java.logging;

    requires static org.testfx;
    requires static org.testfx.junit5;
}
