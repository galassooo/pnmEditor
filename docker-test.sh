#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

# Build Docker image
echo "Building Docker image..."
docker build -t image-editor-build .

# Run container
echo "Running build in container..."
docker run --rm image-editor-build

# Check exit code
if [ $? -eq 0 ]; then
    echo -e "${GREEN}Build successful in Docker environment${NC}"
    exit 0
else
    echo -e "${RED}Build failed in Docker environment${NC}"
    exit 1
fi