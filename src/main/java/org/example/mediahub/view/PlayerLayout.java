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
import javafx.beans.binding.Bindings;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaView;
import javafx.geometry.Pos;


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

    // Allows the user to choose a playback position.
    public final Slider seekSlider = new Slider(0.0, 1.0, 0.0);
    public final ListView<Track> trackList = new ListView<>();

    public final Label statusLabel = new Label("Ready");

    // Displays the title of the selected track in the center panel.
    public final Label trackTitleLabel = new Label("No track selected");


    public final Label timeLabel = new Label("00:00");
    public final Label durationLabel = new Label("00:00");

    public final ProgressBar progressBar = new ProgressBar(0);

    public final Label artworkSymbol = new Label("♫");

    // Displays video inside the existing artwork area.
    public final MediaView mediaView = new MediaView();

    // Holds either the audio artwork symbol or the video display.
    public final StackPane artworkCard = new StackPane(artworkSymbol);

    private final BorderPane root;

    public PlayerLayout() {

        // Apply a dedicated style to the selected track title.
        trackTitleLabel.getStyleClass().add("track-title");

        // Reserve enough space for the controls below the playlist.
        volumeSlider.setPrefWidth(150);
        seekSlider.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefWidth(400);
        progressBar.setMaxWidth(Double.MAX_VALUE);

        HBox playbackControls = new HBox(
                8, previousButton, playButton, pauseButton, stopButton, nextButton
        );
        // Center playback buttons within the controls panel.
        playbackControls.setAlignment(Pos.CENTER);
        HBox volumeControls = new HBox(8, new Label("Volume"), volumeSlider);
        // Keep buttons centered while placing volume controls on the right.
        BorderPane controlRow = new BorderPane();
        controlRow.setCenter(playbackControls);
        controlRow.setRight(volumeControls);

        HBox timeControls = new HBox(4, timeLabel, new Label("/"), durationLabel);

        // Keep the playlist in a dedicated left panel.
        VBox playlistPanel = new VBox(12, new Label("Playlist"), trackList);
        playlistPanel.setPrefWidth(240);
        trackList.setPrefHeight(400);
        VBox.setVgrow(trackList, Priority.ALWAYS);

        // Give narrower videos more vertical space without cropping them.
        artworkCard.setPrefSize(480, 340);
        artworkCard.setMaxSize(480, 340);

        artworkCard.getStyleClass().add("artwork-card");
        artworkSymbol.getStyleClass().add("artwork-symbol");


        // Keep the video within the artwork card instead of adding a new layout row.
        mediaView.setFitWidth(480);
        mediaView.setFitHeight(340);
        mediaView.setPreserveRatio(true);

        mediaView.setVisible(false);

        artworkCard.getChildren().add(mediaView);

        // Center video frames that do not fill the 16:9 artwork area.
        StackPane.setAlignment(mediaView, Pos.CENTER);

// Reserve the center for track information and future visual content.
        VBox contentPanel = new VBox(
                16,
                trackTitleLabel,
                artworkCard,
                statusLabel
        );
// Keep playback controls visible at the bottom of the window.
        VBox controlsPanel = new VBox(
                8, seekSlider, timeControls, controlRow
        );

        // Style the bottom controls independently from the other panels.
        controlsPanel.getStyleClass().add("controls-panel");

        root = new BorderPane();
        root.setLeft(playlistPanel);
        root.setCenter(contentPanel);

        // Keep panel spacing in CSS instead of hard-coding it in the controller.
        playlistPanel.getStyleClass().add("playlist-panel");
        contentPanel.getStyleClass().add("content-panel");

        root.setBottom(controlsPanel);
        root.setStyle("-fx-padding: 16;");

        // Update the title automatically when the playlist selection changes.
        trackTitleLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> {
                            Track selectedTrack = trackList.getSelectionModel().getSelectedItem();
                            return selectedTrack == null
                                    ? "No track selected"
                                    : selectedTrack.getTitle();
                        },
                        trackList.getSelectionModel().selectedItemProperty()
                )
        );
    }


    public BorderPane getRoot() {
        return root;
    }
}