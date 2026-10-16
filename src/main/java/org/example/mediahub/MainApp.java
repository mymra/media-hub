package org.example.mediahub;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.mediahub.model.Track;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;

import java.net.URL;

/**
 * Entry point for the JavaFX media player prototype.
 */
public class MainApp extends Application {


    // Keep a reference to the player so it can be released when the window closes.
    private MediaPlayer mediaPlayer;

    @Override
    public void start(Stage stage) {
        Button playButton = new Button("Play");

        // Keep the displayed title and resource path together.
        Track sampleTrack = new Track("Sample track", "/media/sample.mp3");

        ListView<Track> trackList = new ListView<>(
                FXCollections.observableArrayList(
                        sampleTrack,
                        new Track("Sample track 1", "/media/sample1.mp3"),
                        new Track("Sample track 2", "/media/sample2.mp3")
                )
        );

// Make the initial selection visible in the playlist.
        trackList.getSelectionModel().select(sampleTrack);

        Label statusLabel = new Label("Ready: " + sampleTrack.getTitle());

        // Play the currently selected track.
        playButton.setOnAction(event -> {
            if (mediaPlayer != null) {
                mediaPlayer.play();
            }
        });

        // Load and play a new track when the selection changes.
        trackList.getSelectionModel().selectedItemProperty().addListener(
                (observable, previousTrack, selectedTrack) -> {
                    if (selectedTrack == null) {
                        return;
                    }

                    // Stop the previous track and release its resources.
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.dispose();
                        mediaPlayer = null;
                    }

                    URL audioResource = getClass().getResource(selectedTrack.getResourcePath());
                    if (audioResource == null) {
                        statusLabel.setText("Audio file not found: " + selectedTrack.getResourcePath());
                        return;
                    }

                    try {
                        Media media = new Media(audioResource.toExternalForm());
                        mediaPlayer = new MediaPlayer(media);

                        // Keep a stable reference for callbacks belonging to this track.
                        MediaPlayer playerForTrack = mediaPlayer;

                        // Update the visible status for the selected track.
                        mediaPlayer.setOnReady(() ->
                                statusLabel.setText("Ready: " + selectedTrack.getTitle())
                        );
                        mediaPlayer.setOnPlaying(() ->
                                statusLabel.setText("Playing: " + selectedTrack.getTitle())
                        );
                        mediaPlayer.setOnError(() ->
                                statusLabel.setText("Playback error: " + playerForTrack.getError().getMessage())
                        );

                        mediaPlayer.play();
                    } catch (RuntimeException exception) {
                        statusLabel.setText("Could not load: " + selectedTrack.getTitle());
                    }
                }
        );

        // Trigger the selection listener for the initial track.
        trackList.getSelectionModel().clearSelection();
        trackList.getSelectionModel().select(sampleTrack);
        VBox root = new VBox(16, trackList, statusLabel, playButton);
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