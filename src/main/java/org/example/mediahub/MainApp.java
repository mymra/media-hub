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
import javafx.scene.layout.HBox;
import javafx.scene.control.Slider;
import javafx.beans.binding.Bindings;
import javafx.scene.control.ProgressBar;


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
        Button pauseButton = new Button("Pause");
        Button stopButton = new Button("Stop");
        Button previousButton = new Button("Previous");
        Button nextButton = new Button("Next");

        // Use the same 0.0–1.0 range as MediaPlayer.volumeProperty().
        Slider volumeSlider = new Slider(0.0, 1.0, 0.5);
        volumeSlider.setPrefWidth(150);

        // Keep the displayed title and resource path together.
        Track sampleTrack = new Track("Sample track", "/media/sample.mp3");

        ListView<Track> trackList = new ListView<>(
                FXCollections.observableArrayList(
                        sampleTrack,
                        new Track("Sample track 1", "/media/sample1.mp3"),
                        new Track("Sample track 2", "/media/sample2.mp3")
                )
        );

        // Reserve space for the player controls below the playlist.
        trackList.setPrefHeight(250);

        // Make the initial selection visible in the playlist.
        trackList.getSelectionModel().select(sampleTrack);

        Label statusLabel = new Label("Ready: " + sampleTrack.getTitle());
        Label timeLabel = new Label("00:00");
        Label durationLabel = new Label("00:00");

        // ProgressBar expects a value between 0.0 and 1.0.
        ProgressBar progressBar = new ProgressBar(0);

        // Give the bar a visible width before the layout stretches it.
        progressBar.setPrefWidth(400);
        progressBar.setMaxWidth(Double.MAX_VALUE);

        // Play the currently selected track.
        playButton.setOnAction(event -> {
            if (mediaPlayer != null) {
                mediaPlayer.play();
            }
        });

        // Pause the currently loaded track without resetting its position.
        pauseButton.setOnAction(event -> {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
        });

        // Stop the currently loaded track and reset its position.
        stopButton.setOnAction(event -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        });

        // Select the previous track; the selection listener handles playback.
        previousButton.setOnAction(event -> {
            int index = trackList.getSelectionModel().getSelectedIndex();
            if (index > 0) {
                trackList.getSelectionModel().select(index - 1);
            }
        });

        // Select the next track; the selection listener handles playback.
        nextButton.setOnAction(event -> {
            int index = trackList.getSelectionModel().getSelectedIndex();
            if (index >= 0 && index < trackList.getItems().size() - 1) {
                trackList.getSelectionModel().select(index + 1);
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
                        mediaPlayer.volumeProperty().unbind();
                        timeLabel.textProperty().unbind();
                        timeLabel.setText("00:00");
                        durationLabel.setText("00:00");

                        // Disconnect the progress display from the previous player.
                        progressBar.progressProperty().unbind();
                        progressBar.setProgress(0);
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

                        // Let the slider control the volume of the current player.
                        mediaPlayer.volumeProperty().bind(volumeSlider.valueProperty());

                        // Keep a stable reference for callbacks belonging to this track.
                        MediaPlayer playerForTrack = mediaPlayer;


                        // Recalculate the displayed time when playback time changes.
                        timeLabel.textProperty().bind(
                                Bindings.createStringBinding(
                                        () -> {
                                            double totalSeconds = playerForTrack.getCurrentTime().toSeconds();

                                            // Avoid displaying an invalid time before the media is ready.
                                            if (!Double.isFinite(totalSeconds) || totalSeconds < 0) {
                                                return "00:00";
                                            }

                                            int minutes = (int) totalSeconds / 60;
                                            int seconds = (int) totalSeconds % 60;
                                            return String.format("%02d:%02d", minutes, seconds);
                                        },
                                        playerForTrack.currentTimeProperty()
                                )
                        );

                        // Keep the progress bar synchronized with the current player.
                        progressBar.progressProperty().bind(
                                Bindings.createDoubleBinding(
                                        () -> {
                                            double elapsed = playerForTrack.getCurrentTime().toMillis();
                                            double total = playerForTrack.getTotalDuration().toMillis();

                                            // Duration may be unknown until the media is ready.
                                            if (!Double.isFinite(elapsed) || !Double.isFinite(total) || total <= 0) {
                                                return 0.0;
                                            }

                                            return Math.max(0.0, Math.min(1.0, elapsed / total));
                                        },
                                        playerForTrack.currentTimeProperty(),
                                        playerForTrack.totalDurationProperty()
                                )
                        );
                        // Update the visible status for the selected track.
                        mediaPlayer.setOnReady(() -> {
                            statusLabel.setText("Ready: " + selectedTrack.getTitle());

                            // The total duration becomes available after the media is ready.
                            double totalSeconds = playerForTrack.getTotalDuration().toSeconds();
                            if (Double.isFinite(totalSeconds) && totalSeconds >= 0) {
                                int minutes = (int) totalSeconds / 60;
                                int seconds = (int) totalSeconds % 60;
                                durationLabel.setText(String.format("%02d:%02d", minutes, seconds));
                            }
                        });
                        mediaPlayer.setOnPlaying(() ->
                                statusLabel.setText("Playing: " + selectedTrack.getTitle())
                        );

                        // Reflect playback state changes in the user interface.
                        mediaPlayer.setOnPaused(() ->
                                statusLabel.setText("Paused: " + selectedTrack.getTitle())
                        );

                        // Show that playback reached the end of the selected track.
                        playerForTrack.setOnEndOfMedia(() ->
                                statusLabel.setText("Finished: " + selectedTrack.getTitle())
                        );
                        mediaPlayer.setOnStopped(() ->
                                statusLabel.setText("Stopped: " + selectedTrack.getTitle())
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

        // Place playback controls on one horizontal row.
        HBox playbackControls = new HBox(8, previousButton, playButton, pauseButton, stopButton, nextButton);
        HBox volumeControls = new HBox(8, new Label("Volume"), volumeSlider);

        // Display elapsed and total time on the same row.
        HBox timeControls = new HBox(
                4,
                timeLabel,
                new Label("/"),
                durationLabel
        );
        VBox root = new VBox(
                16,
                trackList,
                statusLabel,
                progressBar,
                timeControls,
                playbackControls,
                volumeControls
        );
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