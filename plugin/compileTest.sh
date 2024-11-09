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

# Download JUnit and its dependencies
echo "Downloading JUnit dependencies..."
mvn dependency:get -Dartifact=org.junit.jupiter:junit-jupiter-api:5.10.1
mvn dependency:get -Dartifact=org.junit.jupiter:junit-jupiter-engine:5.10.1
mvn dependency:get -Dartifact=org.junit.platform:junit-platform-console-standalone:1.10.1

# Setup classpath with all required dependencies
JUNIT_API="$HOME/.m2/repository/org/junit/jupiter/junit-jupiter-api/5.10.1/junit-jupiter-api-5.10.1.jar"
JUNIT_ENGINE="$HOME/.m2/repository/org/junit/jupiter/junit-jupiter-engine/5.10.1/junit-jupiter-engine-5.10.1.jar"
JUNIT_STANDALONE="$HOME/.m2/repository/org/junit/platform/junit-platform-console-standalone/1.10.1/junit-platform-console-standalone-1.10.1.jar"

# Construct classpath with all dependencies
TEST_CLASSPATH="$JUNIT_API:$JUNIT_ENGINE:$JUNIT_STANDALONE:$PLUGIN_JAR"

cd "$TEMP_DIR"

# Compila i test con il classpath completo
echo "Compiling tests..."
javac -cp "$TEST_CLASSPATH" \
      --add-exports jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED \
      --add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED \
      --add-exports jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED \
      --add-exports jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED \
      -d . \
      ch/supsi/test/ImageAccessPluginTest.java

# Esegui i test
echo "Running tests..."
java -cp ".:$TEST_CLASSPATH" \
     -Dplugin.jar.path="$PLUGIN_JAR" \
     --add-exports jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED \
     --add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED \
     --add-exports jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED \
     --add-exports jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED \
     org.junit.platform.console.ConsoleLauncher \
     --select-class ch.supsi.test.ImageAccessPluginTest

TEST_RESULT=$?

# Torna alla directory originale e pulisci
cd - > /dev/null
rm -rf "$TEMP_DIR"

exit $TEST_RESULT