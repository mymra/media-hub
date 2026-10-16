package org.example.mediahub;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;

/**
 * Entry point for the JavaFX application.
 * For now, it only displays a window; player features will be added later.
 */
public class MainApp extends Application {


    // Keep a reference to the player so it can be released when the window closes.
    private MediaPlayer mediaPlayer;

    @Override
    public void start(Stage stage) {
        Button playButton = new Button("Play");

        Label statusLabel = new Label("Ready");

        // Look up the file in src/main/resources/media.
        URL audioResource = getClass().getResource("/media/sample.mp3");

        if (audioResource == null) {
            // Disable Play if the resource is missing.
            playButton.setDisable(true);
            statusLabel.setText("Audio file not found: /media/sample.mp3");
        } else {
            // Convert the resource URL to the format required by JavaFX Media.
            Media media = new Media(audioResource.toExternalForm());
            mediaPlayer = new MediaPlayer(media);

            // Update the label when the media is ready or starts playing.
            mediaPlayer.setOnReady(() -> statusLabel.setText("Audio ready"));
            mediaPlayer.setOnPlaying(() -> statusLabel.setText("Playing"));

            // Display playback errors in the window.
            mediaPlayer.setOnError(() ->
                    statusLabel.setText("Playback error: " + mediaPlayer.getError().getMessage())
            );

            // Play the audio when the button is clicked.
            playButton.setOnAction( event-> mediaPlayer.play());
        }

        VBox root = new VBox(16, statusLabel, playButton);
        root.setStyle("-fx-padding: 24;");

        // Create a scene with an initial size of 800 × 500 pixels.
        Scene scene = new Scene(root, 800, 500);

        // Configure and display the main window.
        stage.setTitle("Media Hub");
        stage.setScene(scene);
        stage.show();
    }
    @Override
    public void stop() {
        // Release the media player when the application closes.
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
        }
    }
    public static void main(String[] args) {
        // Start JavaFX; it will call the start() method.
        launch(args);
    }
}