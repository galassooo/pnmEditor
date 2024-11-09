module plugin {
    requires jdk.compiler;
    requires java.xml;
    requires java.compiler;
    requires java.management;

    exports ch.supsi;

    provides com.sun.source.util.Plugin with ch.supsi.ImageAccessPlugin;
}