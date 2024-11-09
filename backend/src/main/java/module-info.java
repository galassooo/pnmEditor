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

    exports ch.supsi.application.image;
    exports ch.supsi.application.preferences;
    exports ch.supsi.application.translations;
    exports ch.supsi.application.filters;


    opens ch.supsi.business.filter.command to org.reflections;
}