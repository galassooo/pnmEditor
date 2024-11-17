/**

 A backend module for image processing and manipulation, providing core functionality
 for handling different image formats, applying filters, managing preferences, and
 supporting internationalization.

 This module is designed with a layered architecture:
 <br>
 <br>
 <strong>Application Layer:</strong> High-level interfaces and facades for business logic
 <br>
 <strong>Business Layer:</strong> Core business logic and processing
 <br>
 <strong>Data Access Layer:</strong> Data persistence and retrieval

 @since 1.0
 */

module backend {
    requires jsr305;
    requires org.jetbrains.annotations;
    requires org.reflections;
    requires spring.core;
    requires spring.context;
    requires javassist;
    requires spring.beans;
    requires java.desktop;
    requires jdk.javadoc;

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


    // used to generate documentation since un-exported modules are not included,
    // in this way all packages are documented, I know that it makes no sense to document
    //un-exported  packages, but since this is a school project I thought it could be a good idea.
    exports ch.supsi.business.filter;
    exports ch.supsi.business.filter.chain;
    exports ch.supsi.business.filter.chain.command;
    exports ch.supsi.business.image;
    exports ch.supsi.business.preferences;
    exports ch.supsi.business.state;
    exports ch.supsi.business.strategy;
    exports ch.supsi.business.translations;
    exports ch.supsi.dataaccess.image;
    exports ch.supsi.dataaccess.preferences;
    exports ch.supsi.dataaccess.translations;

}