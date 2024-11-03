#!/bin/bash

# Crea una directory temporanea pulita
TEMP_DIR=$(mktemp -d)
mkdir -p "$TEMP_DIR/ch/supsi"

# Copia solo il file sorgente necessario
cp src/main/java/ch/supsi/SamplePlugin.java "$TEMP_DIR/ch/supsi/"

# Vai nella directory temporanea
cd "$TEMP_DIR"

# Compila il plugin in ambiente pulito
javac --add-exports jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED --add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED --add-exports jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED  -proc:none ch/supsi/SamplePlugin.java

# Crea il file dei servizi
mkdir -p META-INF/services
echo "ch.supsi.SamplePlugin" > META-INF/services/com.sun.source.util.Plugin

# Crea il jar
jar cf plugin.jar ch META-INF

# Torna alla directory originale
cd -

# Sposta il jar nella posizione corretta
mv "$TEMP_DIR/plugin.jar" target/plugin-1.0-SNAPSHOT.jar

# Pulisci
rm -rf "$TEMP_DIR"

# Installa in Maven
mvn install:install-file -Dfile=target/plugin-1.0-SNAPSHOT.jar -DgroupId=ch.supsi -DartifactId=plugin -Dversion=1.0-SNAPSHOT -Dpackaging=jar