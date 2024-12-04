#!/bin/bash

# Crea una directory temporanea pulita
TEMP_DIR=$(mktemp -d)
mkdir -p "$TEMP_DIR/ch/supsi/annotation"
mkdir -p "$TEMP_DIR/ch/supsi"

# Copia tutti i file necessari
cp src/main/java/ch/supsi/ImageAccessPlugin.java "$TEMP_DIR/ch/supsi/"
cp src/main/java/ch/supsi/annotation/ImageAccess.java "$TEMP_DIR/ch/supsi/annotation/"
cp src/main/java/ch/supsi/annotation/ImageAccessFactory.java "$TEMP_DIR/ch/supsi/annotation/"
cp src/main/java/ch/supsi/annotation/Register.java "$TEMP_DIR/ch/supsi/annotation/"
cp src/main/java/ch/supsi/DataAccessComponent.java "$TEMP_DIR/ch/supsi/"
cp src/main/java/module-info.java "$TEMP_DIR/"

# Vai nella directory temporanea
cd "$TEMP_DIR"

# Compila prima l'annotazione
javac ch/supsi/annotation/ImageAccess.java
javac ch/supsi/annotation/Register.java
javac ch/supsi/annotation/ImageAccessFactory.java
javac ch/supsi/DataAccessComponent.java

# Compila il plugin usando l'annotazione appena compilata
javac -encoding UTF8 \
      --patch-module plugin=. \
      --add-reads plugin=jdk.compiler \
      --add-exports jdk.compiler/com.sun.tools.javac.code=plugin \
      --add-exports jdk.compiler/com.sun.tools.javac.api=plugin \
      --add-exports jdk.compiler/com.sun.tools.javac.tree=plugin \
      --add-exports jdk.compiler/com.sun.tools.javac.util=plugin \
      -proc:none \
      module-info.java \
      ch/supsi/ImageAccessPlugin.java

# Crea il file dei servizi
mkdir -p META-INF/services
echo "ch.supsi.ImageAccessPlugin" > META-INF/services/com.sun.source.util.Plugin

# Crea il jar
jar cf plugin.jar ch META-INF module-info.class

# Torna alla directory originale e crea la directory target se non esiste
cd - > /dev/null
mkdir -p target

# Sposta il jar nella posizione corretta
mv "$TEMP_DIR/plugin.jar" target/plugin-1.0-SNAPSHOT.jar

# Pulisci
rm -rf "$TEMP_DIR"