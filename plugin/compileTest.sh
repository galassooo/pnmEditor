#!/bin/bash

/bin/bash compile.sh
# Debug info
echo "=== Environment Information ==="
echo "PWD: $(pwd)"
echo "Java Version:"
java -version
echo "Maven Version:"
mvn -version
echo "Maven Local Repository: $HOME/.m2/repository"
echo "========================="

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

# Debug: verifica JUnit prima del download
echo "=== JUnit Files Before Download ==="
ls -la $HOME/.m2/repository/org/junit/platform/junit-platform-console-standalone/1.10.1/ 2>/dev/null || echo "Directory not found"
echo "=================================="

# Scarica JUnit se necessario
JUNIT_STANDALONE="$HOME/.m2/repository/org/junit/platform/junit-platform-console-standalone/1.10.1/junit-platform-console-standalone-1.10.1.jar"

if [ ! -f "$JUNIT_STANDALONE" ]; then
    echo "Downloading JUnit..."
    mvn dependency:get -Dartifact=org.junit.platform:junit-platform-console-standalone:1.10.1
fi

# Debug: verifica JUnit dopo il download
echo "=== JUnit Files After Download ==="
ls -la $HOME/.m2/repository/org/junit/platform/junit-platform-console-standalone/1.10.1/ 2>/dev/null || echo "Directory not found"
echo "================================="

cd "$TEMP_DIR"

# Debug: mostra contenuto directory corrente e CLASSPATH
echo "=== Current Directory Content ==="
ls -la
echo "=== CLASSPATH ==="
echo $CLASSPATH
echo "==================="

# Compila i test
echo "Compiling tests..."
echo "Using javac command:"
echo "javac -cp \"$JUNIT_STANDALONE:$PLUGIN_JAR\" \\"
echo "      --add-exports jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED \\"
echo "      --add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED \\"
echo "      --add-exports jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED \\"
echo "      --add-exports jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED \\"
echo "      -d . \\"
echo "      ch/supsi/test/ImageAccessPluginTest.java"

javac -cp "$JUNIT_STANDALONE:$PLUGIN_JAR" \
      --add-exports jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED \
      --add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED \
      --add-exports jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED \
      --add-exports jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED \
      -d . \
      ch/supsi/test/ImageAccessPluginTest.java

COMPILE_RESULT=$?
echo "Compilation result: $COMPILE_RESULT"

if [ $COMPILE_RESULT -ne 0 ]; then
    echo "Compilation failed. Checking if JUnit jar exists:"
    ls -l "$JUNIT_STANDALONE"
    echo "Checking if plugin jar exists:"
    ls -l "$PLUGIN_JAR"
    cd - > /dev/null
    rm -rf "$TEMP_DIR"
    exit $COMPILE_RESULT
fi

# Esegui i test
echo "Running tests..."
java -cp ".:$JUNIT_STANDALONE:$PLUGIN_JAR" \
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