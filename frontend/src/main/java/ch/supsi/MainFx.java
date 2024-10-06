package ch.supsi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
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

        //VA CHIAMATO PER GESTIRE L'EVENTO 'MANUALMENTE'
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            primaryStage.close();
        });

        try {
            URL fxmlUrl = getClass().getResource("/layout/MainScreen.fxml");
            if (fxmlUrl == null) {
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            BorderPane msContent = loader.load();

            fxmlUrl = getClass().getResource("/layout/MenuBar.fxml");
            if (fxmlUrl == null) {
                return;
            }
            loader = new FXMLLoader(fxmlUrl);
            MenuBar menuBar = loader.load();

            msContent.setTop(menuBar);

            Scene scene = new Scene(msContent);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
