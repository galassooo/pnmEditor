package ch.supsi;

import ch.supsi.controller.errors.ErrorController;
import ch.supsi.controller.errors.IErrorController;
import ch.supsi.controller.filter.FilterController;
import ch.supsi.controller.filter.IFilterController;
import ch.supsi.controller.image.IImageController;
import ch.supsi.controller.image.ImageController;
import ch.supsi.view.filter.IFilteredListView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

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
        IFilterController filterController = FilterController.getInstance();
        IImageController imageController = ImageController.getInstance();
        IErrorController errorController = ErrorController.getInstance();

        //VA CHIAMATO PER GESTIRE L'EVENTO 'MANUALMENTE'
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            primaryStage.close();
        });


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
            loader = new FXMLLoader(fxmlUrl);
            MenuBar menuBar = loader.load();
            root.setTop(menuBar);

            fxmlUrl = getClass().getResource("/layout/FilterColumn.fxml");
            if (fxmlUrl == null) {
                return;
            }
            loader = new FXMLLoader(fxmlUrl);
            VBox filterColumn = loader.load();

            fxmlUrl = getClass().getResource("/layout/FilterList.fxml");
            if (fxmlUrl == null) {
                return;
            }
            loader = new FXMLLoader(fxmlUrl);
            ScrollPane filterList = loader.load();

            IFilteredListView controller = loader.getController();
            filterController.addEventPublisher(controller);

            filterColumn.getChildren().add(1, filterList);

            fxmlUrl = getClass().getResource("/layout/InfoBar.fxml");
            if (fxmlUrl == null) {
                return;
            }
            loader = new FXMLLoader(fxmlUrl);
            HBox infoBar = loader.load();
            filterColumn.getChildren().add(infoBar);

            root.setLeft(filterColumn);

            fxmlUrl = getClass().getResource("/layout/Image.fxml");
            if (fxmlUrl == null) {
                return;
            }
            loader = new FXMLLoader(fxmlUrl);
            ImageView image = loader.load();
            root.setCenter(image);

            imageController.setImage(loader.getController());
            imageController.setStage(primaryStage);
            errorController.setStage(primaryStage);

            fxmlUrl = getClass().getResource("/layout/HistoryColumn.fxml");
            if (fxmlUrl == null) {
                return;
            }
            loader = new FXMLLoader(fxmlUrl);
            VBox historyBox = loader.load();



            fxmlUrl = getClass().getResource("/layout/InfoColumn.fxml");
            if (fxmlUrl == null) {
                return;
            }
            loader = new FXMLLoader(fxmlUrl);
            VBox infoCol = loader.load();

            root.setRight(new VBox(historyBox, infoCol));

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

