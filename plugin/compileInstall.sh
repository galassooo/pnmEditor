#!/bin/bash

/bin/bash compile.sh

# Installa in Maven
mvn install:install-file -Dfile=target/plugin-1.0-SNAPSHOT.jar \
    -DgroupId=ch.supsi \
    -DartifactId=plugin \
    -Dversion=1.0-SNAPSHOT \
    -Dpackaging=jar
