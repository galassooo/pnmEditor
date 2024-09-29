package ch.supsi;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


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
            primaryStage.close(); //TODO EXIT CONTROLLER
        });


        Scene scene = new Scene(new BorderPane());
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
