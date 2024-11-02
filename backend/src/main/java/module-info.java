module backend {
    requires jsr305;
    requires org.jetbrains.annotations;
    requires org.reflections;
    requires spring.core;
    requires spring.context;
    requires javassist;
    requires spring.beans;
    requires java.desktop;
    requires plugin;
    requires jdk.compiler;

    exports ch.supsi.application.image;
    exports ch.supsi.application.preferences;
    exports ch.supsi.application.translations;
    exports ch.supsi.application.filters;
    exports ch.supsi.business.image;
    exports ch.supsi.business.translations;
    exports ch.supsi.business.preferences;
    exports ch.supsi.business.strategy;
    exports ch.supsi.business.filter.strategy;
    exports ch.supsi.business.filter;

    exports ch.supsi.dataaccess.translations;
    exports ch.supsi.dataaccess.image;
    exports ch.supsi.dataaccess.preferences;

    opens ch.supsi.dataaccess.image to org.reflections;
    opens ch.supsi.business.filter.strategy to org.reflections;
}