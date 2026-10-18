package org.example.mediahub.view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.mediahub.model.Track;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;


/**
 * Builds and owns the JavaFX controls displayed by the media player.
 * Playback behavior will be connected by a separate controller.
 */
public class PlayerLayout {

    public final Button playButton = new Button("Play");
    public final Button pauseButton = new Button("Pause");
    public final Button stopButton = new Button("Stop");
    public final Button previousButton = new Button("Previous");
    public final Button nextButton = new Button("Next");

    public final Slider volumeSlider = new Slider(0.0, 1.0, 0.5);
    public final ListView<Track> trackList = new ListView<>();

    public final Label statusLabel = new Label("Ready");
    public final Label timeLabel = new Label("00:00");
    public final Label durationLabel = new Label("00:00");

    public final ProgressBar progressBar = new ProgressBar(0);


    private final BorderPane root;

    public PlayerLayout() {
        // Reserve enough space for the controls below the playlist.
        volumeSlider.setPrefWidth(150);
        progressBar.setPrefWidth(400);
        progressBar.setMaxWidth(Double.MAX_VALUE);

        HBox playbackControls = new HBox(
                8, previousButton, playButton, pauseButton, stopButton, nextButton
        );
        HBox volumeControls = new HBox(8, new Label("Volume"), volumeSlider);
        HBox timeControls = new HBox(4, timeLabel, new Label("/"), durationLabel);

        // Keep the playlist in a dedicated left panel.
        VBox playlistPanel = new VBox(12, new Label("Playlist"), trackList);
        playlistPanel.setPrefWidth(240);
        trackList.setPrefHeight(400);
        VBox.setVgrow(trackList, Priority.ALWAYS);

// Reserve the center for track information and future visual content.
        VBox contentPanel = new VBox(16, new Label("Now playing"), statusLabel);
        contentPanel.setStyle("-fx-padding: 20;");

// Keep playback controls visible at the bottom of the window.
        VBox controlsPanel = new VBox(
                12, progressBar, timeControls, playbackControls, volumeControls
        );
        controlsPanel.setStyle("-fx-padding: 16;");

        root = new BorderPane();
        root.setLeft(playlistPanel);
        root.setCenter(contentPanel);
        root.setBottom(controlsPanel);
        root.setStyle("-fx-padding: 16;");
    }

    public BorderPane getRoot() {
        return root;
    }
}