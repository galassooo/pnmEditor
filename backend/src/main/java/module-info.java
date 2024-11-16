module backend {
    requires jsr305;
    requires org.jetbrains.annotations;
    requires org.reflections;
    requires spring.core;
    requires spring.context;
    requires javassist;
    requires spring.beans;
    requires java.desktop;

    requires static plugin;
    requires transitive jdk.compiler;
    requires jdk.jdi;

    exports ch.supsi.application.image;
    exports ch.supsi.application.preferences;
    exports ch.supsi.application.translations;
    exports ch.supsi.application.filters;
    exports ch.supsi.application.state;

    opens ch.supsi.business.filter.chain to org.reflections;
    opens ch.supsi.business.filter.chain.command to org.reflections;
}