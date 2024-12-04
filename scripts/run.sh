#!/bin/bash

BLUE='\033[34m'
GREEN='\033[32m'
RED='\033[31m'
YELLOW='\033[33m'
BOLD='\033[1m'
NC='\033[0m'

find_java_17() {
    local java_paths=(
        "/Library/Java/JavaVirtualMachines"
        "/usr/lib/jvm"
        "/c/Program Files/Java"
    )

    for base_path in "${java_paths[@]}"; do
        if [ -d "$base_path" ]; then
            local java_home=$(find "$base_path" -maxdepth 1 -type d -name "*17*" | head -n 1)
            if [ -n "$java_home" ]; then
                if [[ "$java_home" == *"/Library/Java/JavaVirtualMachines"* ]]; then
                    echo "${java_home}/Contents/Home"
                else
                    echo "$java_home"
                fi
                return 0
            fi
        fi
    done
    return 1
}

progress_bar() {
    local duration=$1
    local steps=40
    local sleep_time=$(bc <<< "scale=4; $duration/$steps")

    echo -n "["
    for ((i=0; i<steps; i++)); do
        echo -n "=>"
        sleep $sleep_time
    done
    echo -n "]"
    echo
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

print_header "Locating Java 17"
export JAVA_HOME=$(find_java_17)

if [ -z "$JAVA_HOME" ]; then
    print_error "Java 17 not found. Please install Java 17."
    exit 1
fi

print_success "Found Java 17: $JAVA_HOME"

cd ..
# Build Plugin
print_header "Building Plugin"
echo -n "Compiling plugin..."
cd plugin
chmod +x compile.sh
./compile.sh > /dev/null 2>&1 &
progress_bar 2
wait $!

if [ $? -eq 0 ]; then
    print_success "Plugin compilation successful"
else
    print_error "Plugin compilation failed"
    exit 1
fi

echo -n "Installing plugin..."
chmod +x compileInstall.sh
./compileInstall.sh > /dev/null 2>&1 &
progress_bar 1
wait $!

if [ $? -eq 0 ]; then
    print_success "Plugin installation successful"
else
    print_error "Plugin installation failed"
    exit 1
fi

cd ..

# Build Backend
print_header "Building Backend Module"
cd backend
echo -n "Compiling and testing backend..."
JAVA_HOME="$JAVA_HOME" mvn clean test > /dev/null 2>&1 &
progress_bar 5
wait $!

if [ $? -eq 0 ]; then
    print_success "Backend tests passed"
else
    print_error "Backend tests failed"
    exit 1
fi

echo -n "Installing backend..."
JAVA_HOME="$JAVA_HOME" mvn clean install -DskipTests > /dev/null 2>&1 &
progress_bar 2
wait $!

if [ $? -eq 0 ]; then
    print_success "Backend installation successful"
else
    print_error "Backend installation failed"
    exit 1
fi

cd ..

# Build Frontend
print_header "Building Frontend Module"
cd frontend
echo -n "Compiling frontend..."
JAVA_HOME="$JAVA_HOME" mvn clean compile > /dev/null 2>&1 &
progress_bar 5
wait $!

if [ $? -eq 0 ]; then
    print_success "Frontend compilation passed"
else
    print_warning "Frontend tests failed but continuing build"
fi

echo -n "Packaging frontend..."
JAVA_HOME="$JAVA_HOME" mvn clean package -DskipTests > /dev/null 2>&1 &
progress_bar 3
wait $!

if [ $? -eq 0 ]; then
    print_success "Frontend packaging successful"
else
    print_error "Frontend packaging failed"
    exit 1
fi

cd ..

# Final Summary
print_header "Build Summary"
echo -e "${BOLD}Plugin:${NC} Installed successfully"
echo -e "${BOLD}Backend module:${NC} Installed successfully"
echo -e "${BOLD}Frontend module:${NC} Packaged successfully"
echo -e "${BOLD}Build artifacts:${NC}"
echo "  • Plugin JAR: plugin/target/plugin-1.0-SNAPSHOT.jar"
echo "  • Backend JAR: backend/target/backend-1.0-SNAPSHOT.jar"
echo "  • Frontend JAR: frontend/target/frontend-1.0-SNAPSHOT-jar-with-dependencies.jar"

print_success "Build completed successfully!"

print_header "Running frontend jar"
echo -n "Running jar file"
java -jar  frontend/target/frontend-1.0-SNAPSHOT-jar-with-dependencies.jar

