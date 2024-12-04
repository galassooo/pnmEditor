# Frontend Maintenance Guide

## Table of Contents
1. [Architecture](#1-architecture)
2. [Views](#2-views)
3. [Models](#3-models)
4. [Controllers](#4-controllers)
5. [Events](#5-events)
6. [Styles](#6-styles)
7. [Adding New Features](#7-adding-new-features)
8. [Testing Guidelines](#8-testing-guidelines)
9. [Build Procedures](#9-build-procedures)

## 1 Architecture
The frontend follows MVC pattern with event-driven communication:

### Structure
```bash
org.supsi
├── controller      # Controllers for UI logic
├── model          # Models for data/state
├── view           # Views and FXML files
├── dispatcher     # Menu action handlers 

```

### Module Requirements
- JavaFX for UI
- Backend module for core functionality
- Test dependencies for TestFX/JUnit
- Mockito for tests

## 2 Views

### 2.1 FXML Files
Located in `/src/main/resources/layout/`:
- `BasePane.fxml` - Main application layout
- `MenuBar.fxml` - Application menu
- `FilterLine.fxml` - Filter toolbar
- `FilterList.fxml` - Filter pipeline view
- `Image.fxml` - Main image display
- `InfoColumn.fxml` - Information panel
- `About.fxml` - About dialog

### 2.2 View Controllers
Implement `IView<T>` interface:
```java
public interface IView<T> {
    void build();
    void show();
    void setModel(T model);
}
```

## 3 Models
Models manage application state and data:

### Key Models
- `FilterModel` - Filter pipeline management
- `ImageModel` - Image handling
- `StateModel` - Application state
- `TranslationModel` - Internationalization
- `LoggerModel` - Application logging

### Example Implementation
```java
public class FilterModel implements IFilterModel {
    private static FilterModel myself;
    private final ObservableList<String> filterPipeline;
    
    public static FilterModel getInstance() {
        if (myself == null) {
            myself = new FilterModel();
        }
        return myself;
    }
}
```

## 4 Controllers
Handle user interactions and business logic:

### Key Controllers
- `FilterController` - Filter operations
- `ImageController` - Image operations
- `PreferencesController` - Settings management
- `ErrorController` - Error handling

### Implementation Pattern
```java
public class ImageController implements IImageController {
    private static ImageController myself;
    private final ImageApplication backendController;
    
    protected ImageController() {
        backendController = ImageApplication.getInstance();
    }
}
```

## 5 Events
Event system for decoupled communication:

### Event Types
- `FilterEvent` - Filter operations
- `ExportEvent` - Image export
- `PreferenceEvent` - Settings changes

### Publishing Events
```java
EventPublisher publisher = EventManager.getPublisher();
publisher.publish(new FilterEvent.FilterAddRequested(filterName));
```

### Subscribing to Events
```java
EventSubscriber subscriber = EventManager.getSubscriber();
subscriber.subscribe(FilterEvent.FilterAddRequested.class, this::onFilterAdded);
```

## 6 Styles
CSS files in `/src/main/resources/style/`:
- `style.css` - Main application styles


### Color Scheme
```css
.root {
    -fx-background-color: #262626;
}
.menu-bar {
    -fx-background-color: #2f343f;
}
.button {
    -fx-background-color: #474747;
}
```

## 7 Adding New Features

### 7.1 Adding UI Components
1. Create FXML file in `/src/main/resources/layout/`
2. Create controller class implementing relevant interface
3. Add styles to appropriate CSS file
4. Implement event handling if needed
5. Update translation files for new labels/text
6. Add model binding for state management
7. Create unit tests for new components

### 7.2 Adding Model Features
1. Define interface in appropriate package
2. Implement model class with singleton pattern
3. Create unit tests for new model
4. Update documentation

## 8 Testing Guidelines

### 8.1 Test Structure
```java
class ComponentTest extends AbstractGUITest {
    @Test
    void walkthrough() {
        test1();
        test2();
        // other tests
    }
    private void test1(){}
    private void test2(){}
}
```

### 8.2 Test Categories
- UI component tests using TestFX
- Model unit tests with JUnit
- Event system integration tests
- Controller unit tests
- End-to-end tests for critical paths

### 8.3 Testing Best Practices
1. Mock backend dependencies
2. Test UI interactions thoroughly
3. Verify event publishing/handling
4. Test error scenarios
5. Check state management
6. Validate model updates
7. Test internationalization

## 9 Build Procedures

### 9.1 Commands
```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Package
mvn package

# Generate documentation
mvn javadoc:javadoc
```

### 9.2 Dependencies
Remember to compile and install backend module first:
```bash
cd backend
mvn install
cd ../frontend
mvn compile
```

### 9.3 Important Maven Configuration

The frontend module requires specific Maven configuration for JavaFX and modular development:

```xml
<properties>
    <javafx.version>20.0.1</javafx.version>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
</properties>

<dependencies>
    <!-- JavaFX dependencies -->
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>${javafx.version}</version>
    </dependency>
    
    <!-- Backend module -->
    <dependency>
        <groupId>org.supsi</groupId>
        <artifactId>backend</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    
    <!-- Testing dependencies -->
    <dependency>
        <groupId>org.testfx</groupId>
        <artifactId>testfx-junit5</artifactId>
        <version>4.0.18</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 9.4 Application Properties

Application properties and resources should be properly configured:

```bash
src/main/resources/
├── application.properties    # Application configuration
├── build.properties         # Build information
├── i18n/                    # Internationalization
│   └── ui_labels_*.properties
├── layout/                  # FXML files
└── style/                   # CSS files
```

The `application.properties` file should contain necessary configuration:

```properties
frontend.labels.path=/i18n/ui_labels
```

## Additional Resources

- [JavaFX Documentation](https://openjfx.io/javadoc/17/)
- [TestFX Wiki](https://github.com/TestFX/TestFX/wiki)
- [Maven Documentation](https://maven.apache.org/guides/)
- Backend module documentation available in `Docs/documentation/backend`
- Frontend module documentation available in `Docs/documentation/frontend`