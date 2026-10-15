package org.example.mediahub;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Entry point for the JavaFX application.
 * For now, it only displays a window; player features will be added later.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        // StackPane places its content in the center of the window.
        StackPane root = new StackPane(
                new Label("Media Hub is running!")
        );

        // Create a scene with an initial size of 800 × 500 pixels.
        Scene scene = new Scene(root, 800, 500);

        // Configure and display the main window.
        stage.setTitle("Media Hub");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Start JavaFX; it will call the start() method.
        launch(args);
    }
}