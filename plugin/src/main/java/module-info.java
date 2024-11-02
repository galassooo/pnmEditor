module plugin {
    requires jdk.compiler;

    exports ch.supsi;

    provides com.sun.source.util.Plugin with ch.supsi.SamplePlugin;
}