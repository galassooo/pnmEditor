
# Backend Maintenance Guide
## Table of Contents

1. [Multi-layer Architecture](#1-multi-layer-architecture)
2. [Adding New Languages](#2-adding-new-languages)
3. [Adding Properties](#3-adding-properties)
4. [Adding New Filters](#4-adding-new-filters)
5. [Adding New Image Formats](#5-adding-new-image-formats)
6. [Testing Guidelines](#6-testing-guidelines)
7. [Naming Conventions](#7-naming-conventions)
8. [Build procedures](#8-build-procedures)
9. [Additional documentation](#9-additional-documentation)

## 1 Multi-layer Architecture

The backend follows a three-layer architecture:

### Application Layer (`ch.supsi.application`)
- Entry point for all external interactions.
- Contains high-level interfaces and facades.
- **Key packages**:
    - `image`: Image manipulation interfaces.
    - `filters`: Filter pipeline management.
    - `preferences`: Application settings.
    - `translations`: Language support.
    - `state`: Application state management.

### Business Layer (`ch.supsi.business`)
- Contains core business logic.
- Implements application layer interfaces.
- **Key packages**:
    - `filter`: Filter implementation and chain management.
    - `image`: Image processing logic.
    - `strategy`: Color conversion strategies.
    - `state`: State management implementation.

### Data Access Layer (`ch.supsi.dataaccess`)
- Handles data persistence and retrieval.
- Implements file I/O operations.
- **Key packages**:
    - `image`: Image file format handlers.
    - `preferences`: User preferences storage.
    - `translations`: Language resource management.

## 1.1 Package Structure

```bash
ch.supsi
├── application        # Application layer interfaces
│   ├── filters       # Filter application interfaces
│   ├── image         # Image handling interfaces
│   └── translations  # Translation interfaces
├── business          # Business logic
│   ├── filter        # Filter implementation
│   ├── image         # Image processing
│   └── strategy      # Conversion strategies
└── dataaccess        # Data access layer
    ├── image         # Image format handlers
    └── translations  # Translation resources
```

## 1.2 Module Exports and Visibility

The backend module is structured to maintain a clear separation between layers and restrict access to internal components. Below is an overview of its export and visibility rules:

### Exported Packages
The following packages are exposed for external use, providing high-level APIs:
- `ch.supsi.application.image` - Interfaces for image manipulation.
- `ch.supsi.application.preferences` - Access to application-level settings.
- `ch.supsi.application.translations` - Support for internationalization.
- `ch.supsi.application.filters` - Management and application of filters.
- `ch.supsi.application.state` - Tools for handling application state.

### Internal Packages
These packages are not exported to ensure encapsulation and prevent misuse:
- `ch.supsi.business.*` - Core business logic.
- `ch.supsi.dataaccess.*` - Data persistence and file I/O.

### Opened Packages
To enable runtime tools (e.g., reflection), some packages are opened to specific libraries:
- `ch.supsi.business.filter.chain`
- `ch.supsi.business.filter.chain.command`

### Maintenance Guidelines
- Do not modify exports – Changing the exported packages may break external dependencies.
- Preserve structure – Avoid altering the defined layer hierarchy.
- Keep internal logic private – Ensure business and dataaccess remain hidden.

## 2 Adding New Languages

To add a new language:

1. **Create new properties files** in `/src/main/resources/i18n/labels/`:
   ```
   labels_[language-tag].properties
   ```

2. **Add the language tag** to `/src/main/resources/supported-languages.properties`:
   ```properties
   language1=en-US
   language2=it-CH
   languageN=[new-language-tag]
   ```

3. **Update all filter names** in the new language file (optional):
   ```properties
   Mirror_Horizontal=[translated_name]
   Mirror_Vertical=[translated_name]
   Rotate_Left=[translated_name]
   Rotate_Right=[translated_name]
   Negative=[translated_name]
   ```
    - Note: If a filter translation is missing, a warning will be displayed, and the filter will not be processed.

## 3 Adding Properties

To add new application properties:

1. **Update** `/src/main/resources/default-user-preferences.properties`:
   ```properties
   new-property-key=default-value
   ```

2. **Access the property in code** through `PreferencesApplication`:
   ```java
   PreferencesApplication.getInstance().getPreference("new-property-key");
   ```

3. **Add corresponding translations** in language files.

## 4 Adding New Filters

To create a new image filter:

1. Create a new class in `ch.supsi.business.filter.chain.command`:
   ```java
    package ch.supsi.business.filter.chain.command;
   
   public class NewFilterCommand implements FilterCommand {
       @Override
       public void execute(WritableImage image) {
           // Filter implementation
       }

       @Override
       public String getName() {
           return "Filter_Name";
       }
   }
   ```

2. Add translations for the filter name in language files (optional).

## 5 Adding New Image Formats

To support a new image format:

1. Create a new DataAccess class following the naming convention:
   ```java
   @ImageAccess(magicNumber = {"FORMAT1", "FORMAT2"})
   public final class PngDataAccess extends PNMDataAccess {
       private static PngDataAccess myself;

       public static ImageDataAccess getInstance() {
           if (myself == null) {
               myself = new PngDataAccess();
           }
           return myself;
       }
       // Implement required methods
   }
   ```

2. **Important requirements**:
    - Follow `XXXDataAccess` naming convention or specify extension in the annotation.
    - Implement a static `getInstance()` method returning an `ImageDataAccess`-compatible type.

## 5.1 Create appropriate color conversion strategy in `ch.supsi.business.strategy`.

Color Format and Conversion
All images are internally stored in ARGB format (32-bit):
- Alpha: bits 24-31 (always 0xFF for opaque)
- Red: bits 16-23
- Green: bits 8-15
- Blue: bits 0-7

To handle conversion between ARGB and native formats:

1. Create a conversion strategy in `ch.supsi.business.strategy` implementing `ConvertStrategy`:

```java
public class NewFormatStrategy implements ConvertStrategy {
    @Override
    public long toArgb(long pixel) {
        // Convert from native format to ARGB
        // Return value must be in format: 0xFFRRGGBB
        return 0xFF000000L | (red << 16) | (green << 8) | blue;
    }

    @Override
    public long ArgbToOriginal(long pixel) {
        // Convert from ARGB back to native format
        // Extract components:
        int r = (int) ((pixel >> 16) & 0xFF);
        int g = (int) ((pixel >> 8) & 0xFF);
        int b = (int) (pixel & 0xFF);
        // Return value in native format
    }
}
```
### Available conversion strategies:
- `SingleBit`: For binary formats (e.g., PBM)
- `SingleChannel`: For grayscale formats (e.g., PGM)
- `ThreeChannel`: For RGB formats (e.g., PPM)

Inject the strategy in your DataAccess class:
```java
protected ConvertStrategy getArgbConvertStrategy() {
return new NewFormatStrategy();
}
```

### **Important considerations:**

When converting to ARGB, always set alpha channel to 0xFF (fully opaque)<br>
For formats with higher bit depth (>8 bits per channel), normalize values to 8-bit range<br>
For formats with fewer channels, implement appropriate color space conversion (e.g., RGB to grayscale)<br>
Consider gamma correction if required by the format specification<br>
## 6 Testing Guidelines

Unit tests should be created for all new components.

### Test naming convention:
`[Class]Test`

### Test packages should mirror main packages.

### Required test categories:
```java
class NewFilterTest {
    @Test
    void testValidInput() {
        // Test normal operation
    }

    @Test
    void testNullInput() {
        // Test null handling
    }

    @Test
    void testEmptyInput() {
        // Test empty input
    }
}
```

## 7 Naming Conventions

- **Classes**: PascalCase.
- **Methods/Variables**: camelCase.
- **Constants**: UPPER_SNAKE_CASE.
- **Packages**: lowercase.
- **Test classes**: `[TestedClass]Test`.
- **Filter commands**: `[Name]Command`.
- **Data access classes**: `[Format]DataAccess` or explicit extension annotation.
- **Properties files**: lowercase with underscores.
- **Language tags**: RFC 5646 format (e.g., "en-US").

## 8. Build Procedures

### 8.1 Build Commands
```bash
# Compile project
mvn clean compile

# Create JAR
mvn package

# Install locally
mvn install

# Generate documentation
mvn site
```

## 9 Additional Documentation
### 9.1 JavaDoc

Complete API documentation available in `/Docs/documentation/backend/`
Consult for specific implementation details

### 9.2 Useful Resources
- [JavaFX Documentation](https://openjfx.io/javadoc/17/)
- [TestFX Tutorial](https://github.com/TestFX/TestFX/wiki)
- [Maven Documentation](https://maven.apache.org/guides/)
