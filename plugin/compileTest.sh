#!/bin/bash
/bin/bash compile.sh

# Verifica che il plugin esista
if [ ! -f "target/plugin-1.0-SNAPSHOT.jar" ]; then
    echo "Plugin jar not found at target/plugin-1.0-SNAPSHOT.jar"
    exit 1
fi

PLUGIN_JAR="$(cd target; pwd)/plugin-1.0-SNAPSHOT.jar"
echo "Using plugin jar: $PLUGIN_JAR"

# Crea una directory temporanea per i test
TEMP_DIR=$(mktemp -d)
mkdir -p "$TEMP_DIR/ch/supsi/test"

# Copia il file di test
cp src/test/java/ch/supsi/test/ImageAccessPluginTest.java "$TEMP_DIR/ch/supsi/test/"

# scarica junit standalone se non c'è nella local repo
JUNIT_STANDALONE="$HOME/.m2/repository/org/junit/platform/junit-platform-console-standalone/1.10.1/junit-platform-console-standalone-1.10.1.jar"

if [ ! -f "$JUNIT_STANDALONE" ]; then
    echo "Downloading JUnit..."
    mvn dependency:get -Dartifact=org.junit.platform:junit-platform-console-standalone:1.10.1
fi

cd "$TEMP_DIR"


echo "Compiling tests..."
javac -cp "$JUNIT_STANDALONE:$PLUGIN_JAR" \
      --add-exports jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED \
      --add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED \
      --add-exports jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED \
      --add-exports jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED \
      -d . \
      ch/supsi/test/ImageAccessPluginTest.java


echo "Running tests..."
java -cp ".:$PLUGIN_JAR:$JUNIT_STANDALONE" \
     -Dplugin.jar.path="$PLUGIN_JAR" \
     --add-exports jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED \
     --add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED \
     --add-exports jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED \
     --add-exports jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED \
     org.junit.platform.console.ConsoleLauncher \
     --select-class ch.supsi.test.ImageAccessPluginTest

TEST_RESULT=$?

#ritorno alla directory originale e pulisci
cd - > /dev/null
rm -rf "$TEMP_DIR"

exit $TEST_RESULT