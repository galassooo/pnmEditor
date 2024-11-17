package org.supsi;

import ch.supsi.application.preferences.PreferencesApplication;
import org.supsi.controller.errors.ErrorController;
import org.supsi.controller.errors.IErrorController;
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
import org.supsi.view.image.ExportMenuItem;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainFx extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Loads application components
     *
     * @param primaryStage root
     */
    @Override
    public void start(Stage primaryStage) {

        IImageController imageController = ImageController.getInstance();
        IErrorController errorController = ErrorController.getInstance();
        ITranslationsModel translationsModel = TranslationModel.getInstance();
        ILoggerController loggerController = LoggerController.getInstance();
        FilterController filterController = FilterController.getInstance();

        //VA CHIAMATO PER GESTIRE L'EVENTO 'MANUALMENTE'
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            primaryStage.close();
        });

        PreferencesApplication.getInstance().getPreference("language-tag");

        try {
            URL fxmlUrl = getClass().getResource("/layout/BasePane.fxml");
            if (fxmlUrl == null) {
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            BorderPane root = loader.load();


            fxmlUrl = getClass().getResource("/layout/MenuBar.fxml");
            if (fxmlUrl == null) {
                return;
            }
            ResourceBundle bundle = translationsModel.getUiBundle();
            loader = new FXMLLoader(fxmlUrl, bundle);
            MenuBar menuBar = loader.load();

            MenuDispatcher dispatcher = loader.getController();

            root.setTop(menuBar);

            BorderPane centerPane = new BorderPane();
            root.setCenter(centerPane);

            fxmlUrl = getClass().getResource("/layout/ExportMenu.fxml");
            if (fxmlUrl == null) {
                return;
            }
            loader = new FXMLLoader(fxmlUrl,translationsModel.getUiBundle());
            Menu menu = loader.load();
            ExportMenuItem exportMenuItem = loader.getController();
            int indexBeforeHelp = menuBar.getMenus().size();
            menuBar.getMenus().add(indexBeforeHelp, menu);


            fxmlUrl = getClass().getResource("/layout/FilterMenu.fxml");
            if (fxmlUrl == null) {
                return;
            }
            loader = new FXMLLoader(fxmlUrl,translationsModel.getUiBundle());
            Menu filterMenu = loader.load();

            menuBar.getMenus().add(filterMenu);


            fxmlUrl = getClass().getResource("/layout/FilterLine.fxml");
            if (fxmlUrl == null) {
                return;
            }
            loader = new FXMLLoader(fxmlUrl,translationsModel.getUiBundle());
            VBox filterLine = loader.load();

            centerPane.setTop(filterLine);

            VBox filterColumn = new VBox();
            filterColumn.setPrefHeight(779);
            filterColumn.setPrefWidth(250);
            filterColumn.getStylesheets().add("/style/style.css");
            filterColumn.getStyleClass().add("filter-vbox");


            fxmlUrl = getClass().getResource("/layout/FilterList.fxml");
            if (fxmlUrl == null) {
                return;
            }
            loader = new FXMLLoader(fxmlUrl,translationsModel.getUiBundle());
            ScrollPane filterList = loader.load();


            filterColumn.getChildren().add(0, filterList);

            fxmlUrl = getClass().getResource("/layout/InfoBar.fxml");
            if (fxmlUrl == null) {
                return;
            }
            loader = new FXMLLoader(fxmlUrl,translationsModel.getUiBundle());
            HBox infoBar = loader.load();
            filterColumn.getChildren().add(infoBar);




            centerPane.setLeft(filterColumn);

            fxmlUrl = getClass().getResource("/layout/Image.fxml");
            if (fxmlUrl == null) {
                return;
            }
            loader = new FXMLLoader(fxmlUrl,translationsModel.getUiBundle());
            ImageView image = loader.load();
            centerPane.setCenter(image);

            imageController.setImage(loader.getController());
            imageController.setStage(primaryStage);
            errorController.setStage(primaryStage);

            fxmlUrl = getClass().getResource("/layout/HistoryColumn.fxml");
            if (fxmlUrl == null) {
                return;
            }
            loader = new FXMLLoader(fxmlUrl,translationsModel.getUiBundle());
            VBox historyBox = loader.load();



            fxmlUrl = getClass().getResource("/layout/InfoColumn.fxml");
            if (fxmlUrl == null) {
                return;
            }
            loader = new FXMLLoader(fxmlUrl,translationsModel.getUiBundle());
            VBox infoCol = loader.load();

            centerPane.setRight(new VBox(historyBox, infoCol));

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();

            loggerController.addDebug("ui_application_started");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

