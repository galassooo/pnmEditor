#!/bin/bash

BLUE='\033[34m'
GREEN='\033[32m'
RED='\033[31m'
YELLOW='\033[33m'
BOLD='\033[1m'
NC='\033[0m'

OUTPUT_FILE=$(mktemp)
exec 1> >(tee -a "$OUTPUT_FILE")
exec 2> >(tee -a "$OUTPUT_FILE" >&2)

cleanup_and_ask() {
    local exit_code=$1

    echo -e "\nWould you like to see the complete output? (y/N): "
    read -r show_output

    if [[ "$show_output" =~ ^[Yy]$ ]]; then
        less "$OUTPUT_FILE"
    fi

    rm -f "$OUTPUT_FILE"
    exit $exit_code
}

find_java_17() {
    # MacOS, use java_home utility
    if [[ "$OSTYPE" == "darwin"* ]]; then
        if command -v /usr/libexec/java_home >/dev/null 2>&1; then
            echo $(/usr/libexec/java_home -v 17)
            return 0
        fi
    fi

    # on other systems, check JAVA_HOME first
    if [ -n "$JAVA_HOME" ] && [ -d "$JAVA_HOME" ]; then
        echo "$JAVA_HOME"
        return 0
    fi

    #if JAVA_HOME is not set, try to find it using java command
    if command -v java >/dev/null 2>&1; then
        local java_bin=$(readlink -f $(which java))
        local java_path=$(dirname $(dirname "$java_bin"))
        if [[ -d "$java_path" ]]; then
            echo "$java_path"
            return 0
        fi
    fi


    return 1
}

spinner() {
    local command="$1"
    local message="$2"
    local spin='-\|/'
    local tmp_file=$(mktemp)

    eval "$command" > "$tmp_file" 2>&1 &
    local pid=$!

    local i=0
    printf "  "
    while kill -0 $pid 2>/dev/null; do
        i=$(( (i+1) %4 ))
        printf "\r%s %c %s" "  " "${spin:$i:1}" "${message}"
        sleep 0.1
    done

    wait $pid
    local status=$?

    cat "$tmp_file" >> "$OUTPUT_FILE"

    printf "\r"
    rm -f "$tmp_file"

    return $status
}

print_header() {
    echo -e "\n${BLUE}${BOLD}==> $1${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}! $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

verify_jar() {
    local jar_path=$1
    if [[ -f "$jar_path" ]]; then
        print_success "Verified $jar_path"
    else
        print_error "JAR file missing: $jar_path"
        cleanup_and_ask 1
    fi
}

print_header "Locating Java 17"
export JAVA_HOME=$(find_java_17)

if [ -z "$JAVA_HOME" ]; then
    print_error "Java 17 not found. Please install Java 17."
    cleanup_and_ask 1
fi

print_success "Found Java 17: $JAVA_HOME"

print_header "Building Plugin"
cd plugin
chmod +x compile.sh compileInstall.sh compileTest.sh

spinner "./compile.sh" "Compiling plugin..."
if [ $? -eq 0 ]; then
    print_success "Plugin compilation successful"
else
    print_error "Plugin compilation failed"
    cleanup_and_ask 1
fi

spinner "./compileInstall.sh" "Installing plugin..."
if [ $? -eq 0 ]; then
    print_success "Plugin installation successful"
else
    print_error "Plugin installation failed"
    cleanup_and_ask 1
fi

print_header "Testing Plugin"
spinner "./compileTest.sh" "Running plugin tests..."
if [ $? -eq 0 ]; then
    print_success "Plugin tests passed"
else
    print_error "Plugin tests failed"
    cleanup_and_ask 1
fi
cd ..

print_header "Building and Testing Backend Module"
cd backend

spinner "JAVA_HOME=\"$JAVA_HOME\" mvn clean install" "Building and testing backend..."
if [ $? -eq 0 ]; then
    print_success "Backend build and tests successful"
else
    print_error "Backend build or tests failed"
    cleanup_and_ask 1
fi

verify_jar "target/backend-1.0-SNAPSHOT.jar"
cd ..

print_header "Building Frontend Module"
cd frontend

print_header "Testing Frontend"
spinner "JAVA_HOME=\"$JAVA_HOME\" mvn test" "Running frontend tests..."
if [ $? -eq 0 ]; then
    print_success "Frontend tests passed"
else
    print_error "Frontend tests failed"
    cleanup_and_ask 1
fi

print_header "Packaging Frontend"
spinner "JAVA_HOME=\"$JAVA_HOME\" mvn clean package -DskipTests" "Packaging frontend..."
if [ $? -eq 0 ]; then
    print_success "Frontend packaging successful"
else
    print_error "Frontend packaging failed"
    cleanup_and_ask 1
fi

verify_jar "target/frontend-1.0-SNAPSHOT-jar-with-dependencies.jar"
cd ..

print_header "Build and Test Summary"
echo -e "${BOLD}Plugin:${NC} Built, installed and tested successfully"
echo -e "${BOLD}Backend module:${NC} Built, tested and verified"
echo -e "${BOLD}Frontend module:${NC} Built, tested and verified"
echo -e "${BOLD}Build artifacts:${NC}"
echo "  • Plugin JAR: plugin/target/plugin-1.0-SNAPSHOT.jar"
echo "  • Backend JAR: backend/target/backend-1.0-SNAPSHOT.jar"
echo "  • Frontend JAR: frontend/target/frontend-1.0-SNAPSHOT-jar-with-dependencies.jar"

print_success "Build and tests completed successfully!"

cleanup_and_ask 0