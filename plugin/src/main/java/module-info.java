module plugin {
    requires jdk.compiler;
    requires java.xml;
    requires java.compiler;
    requires java.management;

    exports ch.supsi;
    exports ch.supsi.annotation;

    provides com.sun.source.util.Plugin with ch.supsi.ImageAccessPlugin;
}