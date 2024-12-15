# Image Editor Application

A Java-based image editor that supports various image formats and provides a range of image processing filters.

## Prerequisites

- Java 17
- Maven (for building from source)
- Git (for cloning the repository)

## Installation

1. Download and extract the zip file containing the project
2. Open a terminal and navigate to the extracted directory
3. Run the build script:
   ```bash
   cd os
   chmod 777 build.sh
   ./build.sh
   ```
   This script will:
    - Build the plugin module
    - Build the backend module
    - Build the frontend module with all dependencies

## Running the Application

After successful build, you can run the application using:

```bash
java -jar frontend/target/frontend-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Build Output

During the build process, you can choose to view the complete build output when prompted. This can be useful for troubleshooting if any issues occur during the build.

## Project Structure

The project consists of three main modules:

- **Plugin**: Custom annotation processor for image access
- **Backend**: Core image processing functionality
- **Frontend**: JavaFX-based user interface

## Supported Features

- Multiple image format support (PBM, PGM, PPM)
- Various image filters (Negative, Mirror, Rotate, etc.)
- Real-time filter preview
- Filter pipeline management
- Multi-language support
- Customizable preferences

## Error Handling

If you encounter any issues during the build process:
1. Check the build output for specific error messages
2. Ensure all prerequisites are installed correctly
3. Verify you have proper permissions in the installation directory

## Troubleshooting

If the application fails to start:
- Verify Java 17 is installed and set as the default Java version
- Ensure the JAR file was built successfully
- Check system logs for any error messages

For build-related issues:
- Verify Maven is installed correctly
- Ensure all dependencies are accessible
- Check network connectivity for downloading dependencies
