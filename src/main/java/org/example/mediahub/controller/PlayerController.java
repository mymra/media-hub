package org.example.mediahub.controller;

import javafx.scene.media.MediaPlayer;
import org.example.mediahub.view.PlayerLayout;
import javafx.scene.media.Media;
import javafx.beans.binding.Bindings;
import org.example.mediahub.model.Track;
import java.net.URL;


/**
 * Handles playback button actions using the currently selected media player.
 */
public class PlayerController {
    // The player changes whenever a different track is selected.
    private MediaPlayer mediaPlayer;

    // Controls and status labels owned by the player view.
    private final PlayerLayout layout;

    /**
     * Creates the player and binds its volume to the view's volume control.
     */
    public MediaPlayer createPlayer(String resourceUrl) {
        Media media = new Media(resourceUrl);
        MediaPlayer newPlayer = new MediaPlayer(media);
        this.mediaPlayer = newPlayer;

        // Keep the new player's volume synchronized with the slider.
        newPlayer.volumeProperty().bind(layout.volumeSlider.valueProperty());

        return newPlayer;
    }

        /**
         * Binds the elapsed-time label to the current player's playback time.
         */
        public void bindElapsedTime(MediaPlayer player) {
            layout.timeLabel.textProperty().bind(
                    Bindings.createStringBinding(
                            () -> {
                                double totalSeconds = player.getCurrentTime().toSeconds();

                                // Display a safe default until playback time is valid.
                                if (!Double.isFinite(totalSeconds) || totalSeconds < 0) {
                                    return "00:00";
                                }

                                int minutes = (int) totalSeconds / 60;
                                int seconds = (int) totalSeconds % 60;
                                return String.format("%02d:%02d", minutes, seconds);
                            },
                            player.currentTimeProperty()
                    )
            );

    }

    /**
     * Binds playback progress to the current time and total duration.
     */
    public void bindProgress(MediaPlayer player) {
        layout.progressBar.progressProperty().bind(
                Bindings.createDoubleBinding(
                        () -> {
                            double elapsed = player.getCurrentTime().toMillis();
                            double total = player.getTotalDuration().toMillis();

                            // The duration may be unknown before the media is ready.
                            if (!Double.isFinite(elapsed)
                                    || !Double.isFinite(total)
                                    || total <= 0) {
                                return 0.0;
                            }

                            return Math.max(0.0, Math.min(1.0, elapsed / total));
                        },
                        player.currentTimeProperty(),
                        player.totalDurationProperty()
                )
        );
    }

    /**
     * Displays the selected track and its duration once the media is ready.
     */
    public void configureReadyStatus(MediaPlayer player, Track track) {
        player.setOnReady(() -> {
            layout.statusLabel.setText("Ready: " + track.getTitle());

            // Total duration is available after the media becomes ready.
            double totalSeconds = player.getTotalDuration().toSeconds();
            if (Double.isFinite(totalSeconds) && totalSeconds >= 0) {
                int minutes = (int) totalSeconds / 60;
                int seconds = (int) totalSeconds % 60;
                layout.durationLabel.setText(String.format("%02d:%02d", minutes, seconds));
            }
        });
    }

    /**
     * Updates the visible status when playback state changes.
     */
    public void configurePlaybackStatus(MediaPlayer player, Track track) {
        player.setOnPlaying(() ->
                layout.statusLabel.setText("Playing: " + track.getTitle())
        );

        player.setOnPaused(() ->
                layout.statusLabel.setText("Paused: " + track.getTitle())
        );

        player.setOnStopped(() ->
                layout.statusLabel.setText("Stopped: " + track.getTitle())
        );

        player.setOnEndOfMedia(() ->
                layout.statusLabel.setText("Finished: " + track.getTitle())
        );

        player.setOnError(() -> {
            // Read the error from this player, not a different selected track.
            String message = player.getError() == null
                    ? "Unknown playback error"
                    : player.getError().getMessage();

            layout.statusLabel.setText("Playback error: " + message);
        });
    }


    public PlayerController(PlayerLayout layout) {
        this.layout = layout;
        // Read the current player on each click because track changes replace it.
        layout.playButton.setOnAction(event -> {
            MediaPlayer player = mediaPlayer;
            if (player != null) {
                player.play();
            }
        });


        layout.pauseButton.setOnAction(event -> {
            MediaPlayer player = mediaPlayer;
            if (player != null) {
                player.pause();
            }
        });

        layout.stopButton.setOnAction(event -> {
            MediaPlayer player = mediaPlayer;
            if (player != null) {
                player.stop();
            }
        });

        // Changing the selection triggers the existing track-loading listener.
        layout.previousButton.setOnAction(event -> {
            int index = layout.trackList.getSelectionModel().getSelectedIndex();
            if (index > 0) {
                layout.trackList.getSelectionModel().select(index - 1);
            }
        });

        layout.nextButton.setOnAction(event -> {
            int index = layout.trackList.getSelectionModel().getSelectedIndex();
            if (index >= 0 && index < layout.trackList.getItems().size() - 1) {
                layout.trackList.getSelectionModel().select(index + 1);
            }
        });
    }
    /**
     * Stops and releases the current player before another track is loaded.
     */
    public void releaseCurrentPlayer() {
        if (mediaPlayer == null) {
            return;
        }

        // Prevent callbacks from the old player from changing the new track's status.
        mediaPlayer.setOnReady(null);
        mediaPlayer.setOnPlaying(null);
        mediaPlayer.setOnPaused(null);
        mediaPlayer.setOnStopped(null);
        mediaPlayer.setOnEndOfMedia(null);
        mediaPlayer.setOnError(null);

        mediaPlayer.stop();
        mediaPlayer.volumeProperty().unbind();

        // Disconnect UI bindings before disposing of their source player.
        layout.timeLabel.textProperty().unbind();
        layout.timeLabel.setText("00:00");

        layout.progressBar.progressProperty().unbind();
        layout.progressBar.setProgress(0);
        layout.durationLabel.setText("00:00");

        mediaPlayer.dispose();
        mediaPlayer = null;
    }

    /**
     * Loads and starts the selected track.
     */
    public void loadTrack(Track track) {
        if (track == null) {
            return;
        }

        releaseCurrentPlayer();

        URL audioResource = getClass().getResource(track.getResourcePath());
        if (audioResource == null) {
            layout.statusLabel.setText("Audio file not found: " + track.getResourcePath());
            return;
        }

        try {
            // Create the player and connect it to the current UI controls.
            MediaPlayer player = createPlayer(audioResource.toExternalForm());
            bindElapsedTime(player);
            bindProgress(player);
            configureReadyStatus(player, track);
            configurePlaybackStatus(player, track);

            player.play();

        } catch (RuntimeException exception) {

            // A player may already exist if a later setup step failed.
            releaseCurrentPlayer();
            layout.statusLabel.setText("Could not load: " + track.getTitle());
        }
    }
}