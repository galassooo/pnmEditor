package org.supsi;

import ch.supsi.application.preferences.PreferencesApplication;
import org.supsi.controller.errors.ErrorController;
import org.supsi.controller.exit.ExitController;
import org.supsi.controller.exit.IExitController;
import org.supsi.controller.filter.FilterController;
import org.supsi.controller.image.IImageController;
import org.supsi.controller.image.ImageController;
import org.supsi.controller.logger.ILoggerController;
import org.supsi.controller.logger.LoggerController;
import org.supsi.dispatcher.MenuDispatcher;
import org.supsi.model.translations.ITranslationsModel;
import org.supsi.model.translations.TranslationModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Main JavaFX application class that initializes and manages the application's user interface.
 * This class is responsible for loading all FXML layouts, setting up controllers, and arranging
 * the main application structure.
 */
public class MainFx extends Application {

    private IImageController imageController;
    private ITranslationsModel translationsModel;
    private ILoggerController loggerController;
    private Stage primaryStage;
    private BorderPane root;
    private BorderPane centerPane;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        initializeControllers();
        setupStage();
        loadMainInterface();
    }

    /**
     * Initializes all necessary controllers for the application.
     */
    private void initializeControllers() {
        imageController = ImageController.getInstance();
        translationsModel = TranslationModel.getInstance();
        loggerController = LoggerController.getInstance();
        FilterController.getInstance(); // Force initialize filter controller
        ErrorController.getInstance();
    }

    /**
     * Sets up the primary stage and its event handlers.
     */
    private void setupStage() {
        IExitController exitController = ExitController.getInstance();
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            exitController.handleExit(primaryStage);
        });
        PreferencesApplication.getInstance().getPreference("language-tag");
    }

    /**
     * Loads the main interface components and arranges them in the scene.
     */
    private void loadMainInterface() throws IOException {
        try {
            loadBaseLayout();
            setupMenuBar();
            setupCenterPane();
            finalizeStage();
            loggerController.addDebug("ui_application_started");
        } catch (IOException ignored) {
            throw ignored;
            //unmanaged ex, if the main components fails to load
            // the application should not start
        }
    }

    /**
     * Loads the base layout from FXML.
     * @throws IOException if the FXML file cannot be loaded
     */
    private void loadBaseLayout() throws IOException {
        URL baseUrl = getClass().getResource("/layout/BasePane.fxml");
        if (baseUrl == null) return;
        FXMLLoader loader = new FXMLLoader(baseUrl);
        root = loader.load();

        centerPane = new BorderPane();
        root.setCenter(centerPane);
    }

    /**
     * Sets up the menu bar with all its components.
     * @throws IOException if any menu FXML files cannot be loaded
     */
    private void setupMenuBar() throws IOException {
        MenuBar menuBar = loadMenuBar();
        if (menuBar == null) return;

        Menu exportMenu = loadExportMenu();
        Menu filterMenu = loadFilterMenu();

        int indexBeforeHelp = menuBar.getMenus().size();
        if (exportMenu != null) {
            menuBar.getMenus().add(indexBeforeHelp, exportMenu);
        }
        if (filterMenu != null) {
            menuBar.getMenus().add(filterMenu);
        }

        root.setTop(menuBar);
    }

    /**
     * Loads the main menu bar from FXML.
     * @return MenuBar component
     * @throws IOException if the FXML file cannot be loaded
     */
    private MenuBar loadMenuBar() throws IOException {
        URL menuUrl = getClass().getResource("/layout/MenuBar.fxml");
        if (menuUrl == null) return null;

        FXMLLoader loader = new FXMLLoader(menuUrl, translationsModel.getUiBundle());
        MenuBar menuBar = loader.load();

        MenuDispatcher dispatcher = loader.getController();
        dispatcher.setStage(primaryStage);

        return menuBar;
    }

    /**
     * Loads the export menu from FXML.
     * @return Menu component
     * @throws IOException if the FXML file cannot be loaded
     */
    private Menu loadExportMenu() throws IOException {
        URL exportUrl = getClass().getResource("/layout/ExportMenu.fxml");
        if (exportUrl == null) return null;

        FXMLLoader loader = new FXMLLoader(exportUrl, translationsModel.getUiBundle());
        return loader.load();
    }

    /**
     * Loads the filter menu from FXML.
     * @return Menu component
     * @throws IOException if the FXML file cannot be loaded
     */
    private Menu loadFilterMenu() throws IOException {
        URL filterUrl = getClass().getResource("/layout/FilterMenu.fxml");
        if (filterUrl == null) return null;

        FXMLLoader loader = new FXMLLoader(filterUrl, translationsModel.getUiBundle());
        return loader.load();
    }

    /**
     * Sets up the center pane with filter components, image view, and side panels.
     * @throws IOException if any FXML files cannot be loaded
     */
    private void setupCenterPane() throws IOException {
        setupFilterComponents();
        setupImageView();
        setupSidePanels();
    }

    /**
     * Sets up filter-related components in the center pane.
     * @throws IOException if any filter-related FXML files cannot be loaded
     */
    private void setupFilterComponents() throws IOException {
        VBox filterLine = loadFilterLine();
        if (filterLine != null) {
            centerPane.setTop(filterLine);
        }

        VBox filterColumn = createFilterColumn();
        ScrollPane filterList = loadFilterList();
        HBox infoBar = loadInfoBar();

        if (filterList != null) {
            filterColumn.getChildren().add(0, filterList);
        }
        if (infoBar != null) {
            filterColumn.getChildren().add(infoBar);
        }

        centerPane.setLeft(filterColumn);
    }

    /**
     * Creates the filter column container.
     * @return VBox configured as filter column
     */
    private VBox createFilterColumn() {
        VBox filterColumn = new VBox();
        filterColumn.setPrefHeight(779);
        filterColumn.setPrefWidth(250);
        filterColumn.getStylesheets().add("/style/style.css");
        filterColumn.getStyleClass().add("filter-vbox");
        return filterColumn;
    }

    /**
     * Sets up the main image view in the center pane.
     * @throws IOException if the image FXML file cannot be loaded
     */
    private void setupImageView() throws IOException {
        URL imageUrl = getClass().getResource("/layout/Image.fxml");
        if (imageUrl == null) return;

        FXMLLoader loader = new FXMLLoader(imageUrl, translationsModel.getUiBundle());
        ImageView image = loader.load();
        centerPane.setCenter(image);

        imageController.setImage(loader.getController());
        imageController.setStage(primaryStage);
    }

    /**
     * Sets up the side panels (history and info columns).
     * @throws IOException if any side panel FXML files cannot be loaded
     */
    private void setupSidePanels() throws IOException {
        VBox historyBox = loadHistoryColumn();
        VBox infoCol = loadInfoColumn();

        if (historyBox != null && infoCol != null) {
            centerPane.setRight(new VBox(historyBox, infoCol));
        }
    }

    /**
     * Helper method to load FXML components with proper error handling.
     * @param fxmlPath path to the FXML file
     * @return loaded FXML component or null if loading fails
     * @throws IOException if the FXML file cannot be loaded
     */
    private <T> T loadFXML(String fxmlPath) throws IOException {
        URL fxmlUrl = getClass().getResource(fxmlPath);
        if (fxmlUrl == null) return null;

        FXMLLoader loader = new FXMLLoader(fxmlUrl, translationsModel.getUiBundle());
        return loader.load();
    }

    private VBox loadFilterLine() throws IOException {
        return loadFXML("/layout/FilterLine.fxml");
    }

    private ScrollPane loadFilterList() throws IOException {
        return loadFXML("/layout/FilterList.fxml");
    }

    private HBox loadInfoBar() throws IOException {
        return loadFXML("/layout/InfoBar.fxml");
    }

    private VBox loadHistoryColumn() throws IOException {
        return loadFXML("/layout/HistoryColumn.fxml");
    }

    private VBox loadInfoColumn() throws IOException {
        return loadFXML("/layout/InfoColumn.fxml");
    }

    /**
     * Finalizes the stage setup by creating and showing the scene.
     */
    private void finalizeStage() {
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}