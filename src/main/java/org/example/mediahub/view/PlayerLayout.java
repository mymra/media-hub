package org.example.mediahub.view;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Rectangle;
import org.example.mediahub.model.Track;

/**
 * Builds the player interface and exposes controls used by PlayerController.
 */
public class PlayerLayout {

    public final Button previousButton = new Button("⏮");
    public final Button playButton = new Button("▶");
    public final Button stopButton = new Button("■");
    public final Button nextButton = new Button("⏭");

    public final Slider volumeSlider = new Slider(0.0, 1.0, 0.5);
    public final Slider seekSlider = new Slider(0.0, 1.0, 0.0);
    public final ListView<Track> trackList = new ListView<>();

    public final Label statusLabel = new Label("Ready");
    public final Label trackTitleLabel = new Label("No track selected");
    public final Label timeLabel = new Label("00:00");
    public final Label durationLabel = new Label("00:00");
    public final Label artworkSymbol = new Label("♫");

    // Playback position as a fraction from 0.0 to 1.0.
    public final DoubleProperty playbackProgress = new SimpleDoubleProperty(0.0);

    // Draw the progress line under the interactive seek slider.
    public final Rectangle progressTrack = new Rectangle();
    public final Rectangle progressFill = new Rectangle();

    public final MediaView mediaView = new MediaView();
    public final StackPane artworkCard = new StackPane(artworkSymbol);

    private final BorderPane root;

    public PlayerLayout() {
        configureControls();

        VBox playlistPanel = createPlaylistPanel();
        VBox contentPanel = createContentPanel();
        VBox controlsPanel = createControlsPanel();

        root = new BorderPane();
        root.setLeft(playlistPanel);
        root.setCenter(contentPanel);
        root.setBottom(controlsPanel);
        root.setStyle("-fx-padding: 16;");

        bindTrackTitle();
    }

    private void configureControls() {
        trackTitleLabel.getStyleClass().add("track-title");

        playButton.getStyleClass().add("primary-play-button");
        stopButton.getStyleClass().add("stop-button");
        previousButton.getStyleClass().add("navigation-button");
        nextButton.getStyleClass().add("navigation-button");

        stopButton.setTooltip(new Tooltip("Stop"));
        previousButton.setTooltip(new Tooltip("Previous track"));
        nextButton.setTooltip(new Tooltip("Next track"));

        volumeSlider.setPrefWidth(150);
        volumeSlider.getStyleClass().add("volume-slider");

        seekSlider.setMaxWidth(Double.MAX_VALUE);
        seekSlider.getStyleClass().add("seek-slider");

        artworkCard.setPrefSize(480, 340);
        artworkCard.setMaxSize(480, 340);
        artworkCard.getStyleClass().add("artwork-card");
        artworkSymbol.getStyleClass().add("artwork-symbol");

        mediaView.setFitWidth(480);
        mediaView.setFitHeight(340);
        mediaView.setPreserveRatio(true);
        mediaView.setVisible(false);

        artworkCard.getChildren().add(mediaView);
        StackPane.setAlignment(mediaView, Pos.CENTER);
    }

    private VBox createPlaylistPanel() {
        VBox playlistPanel = new VBox(12, new Label("Playlist"), trackList);
        playlistPanel.setPrefWidth(240);
        playlistPanel.getStyleClass().add("playlist-panel");

        trackList.setPrefHeight(400);
        VBox.setVgrow(trackList, Priority.ALWAYS);

        return playlistPanel;
    }

    private VBox createContentPanel() {
        VBox contentPanel = new VBox(
                16, trackTitleLabel, artworkCard, statusLabel
        );
        contentPanel.getStyleClass().add("content-panel");
        return contentPanel;
    }

    private VBox createControlsPanel() {
        HBox playbackControls = new HBox(
                8, previousButton, playButton, stopButton, nextButton
        );
        playbackControls.setAlignment(Pos.CENTER);

        Label volumeLabel = new Label("VOL");
        volumeLabel.getStyleClass().add("volume-label");

        HBox volumeControls = new HBox(8, volumeLabel, volumeSlider);
        volumeControls.setAlignment(Pos.CENTER);

        // Match the volume panel's width to center the playback buttons.
        Region leftSpacer = new Region();
        leftSpacer.prefWidthProperty().bind(volumeControls.widthProperty());

        BorderPane controlRow = new BorderPane();
        controlRow.setLeft(leftSpacer);
        controlRow.setCenter(playbackControls);
        controlRow.setRight(volumeControls);

        HBox timeControls = new HBox(4, timeLabel, new Label("/"), durationLabel);

        StackPane seekControl = createSeekControl();
        VBox controlsPanel = new VBox(8, seekControl, timeControls, controlRow);
        controlsPanel.getStyleClass().add("controls-panel");

        return controlsPanel;
    }

    private StackPane createSeekControl() {
        progressTrack.setHeight(5);
        progressTrack.setArcWidth(5);
        progressTrack.setArcHeight(5);

        progressFill.setHeight(5);
        progressFill.setArcWidth(5);
        progressFill.setArcHeight(5);

        progressTrack.getStyleClass().add("custom-progress-track");
        progressFill.getStyleClass().add("custom-progress-fill");

        StackPane progressLine = new StackPane(progressTrack, progressFill);
        progressLine.setMouseTransparent(true);

        StackPane.setAlignment(progressTrack, Pos.CENTER_LEFT);
        StackPane.setAlignment(progressFill, Pos.CENTER_LEFT);

        StackPane seekControl = new StackPane(progressLine, seekSlider);

        // Keep the track full width and fill it according to playback progress.
        progressTrack.widthProperty().bind(seekControl.widthProperty());
        progressFill.widthProperty().bind(
                progressTrack.widthProperty().multiply(playbackProgress)
        );

        return seekControl;
    }

    private void bindTrackTitle() {
        // Update the title automatically when the selected item changes.
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