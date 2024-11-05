module plugin {
    requires jdk.compiler;
    requires java.xml;

    exports ch.supsi;
    requires transitive java.compiler;

    provides com.sun.source.util.Plugin with ch.supsi.ImageAccessPlugin;
}