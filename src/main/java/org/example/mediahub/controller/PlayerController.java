package org.example.mediahub.controller;

import java.net.URL;

import javafx.beans.binding.Bindings;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.example.mediahub.model.Track;
import org.example.mediahub.view.PlayerLayout;

/**
 * Connects player controls to MediaPlayer and keeps playback state in sync
 * with the user interface.
 */
public class PlayerController {

    private final PlayerLayout layout;
    private MediaPlayer mediaPlayer;
    private boolean finishedCurrentTrack;
    private boolean draggingSeekSlider;

    public PlayerController(PlayerLayout layout) {
        this.layout = layout;

        configurePlaybackButtons();
        configureSeekSlider();
    }

    private void configurePlaybackButtons() {
        layout.playButton.setOnAction(event -> {
            if (mediaPlayer == null) {
                return;
            }

            if (finishedCurrentTrack) {
                // Reload a completed item instead of reusing the player at its end.
                loadTrack(layout.trackList.getSelectionModel().getSelectedItem());
            } else if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.play();
            }
        });

        layout.stopButton.setOnAction(event -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        });

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

    private void configureSeekSlider() {
        // Register this listener once, not every time a track is loaded.
        layout.playbackProgress.addListener((observable, oldValue, newValue) -> {
            if (!draggingSeekSlider) {
                layout.seekSlider.setValue(newValue.doubleValue());
            }
        });

        layout.seekSlider.setOnMousePressed(event -> draggingSeekSlider = true);

        layout.seekSlider.setOnMouseReleased(event -> {
            draggingSeekSlider = false;

            if (mediaPlayer == null) {
                return;
            }

            Duration totalDuration = mediaPlayer.getTotalDuration();
            if (totalDuration == null
                    || !Double.isFinite(totalDuration.toMillis())
                    || totalDuration.toMillis() <= 0) {
                return;
            }

            // Slider values range from 0.0 (start) to 1.0 (end).
            mediaPlayer.seek(totalDuration.multiply(layout.seekSlider.getValue()));
        });
    }

    /**
     * Loads and starts the selected media item.
     */
    public void loadTrack(Track track) {
        if (track == null) {
            return;
        }

        finishedCurrentTrack = false;
        releaseCurrentPlayer();

        URL resource = getClass().getResource(track.getResourcePath());
        if (resource == null) {
            layout.statusLabel.setText("Audio or video file not found: " + track.getResourcePath());
            return;
        }

        try {
            MediaPlayer player = createPlayer(resource.toExternalForm());

            configureMediaDisplay(player, track);
            bindElapsedTime(player);
            bindProgress(player);
            configureReadyStatus(player, track);
            configurePlaybackStatus(player, track);

            player.play();
        } catch (RuntimeException exception) {
            // Setup can fail after the player was created.
            releaseCurrentPlayer();
            layout.statusLabel.setText("Could not load: " + track.getTitle());
        }
    }

    private MediaPlayer createPlayer(String resourceUrl) {
        MediaPlayer player = new MediaPlayer(new Media(resourceUrl));
        mediaPlayer = player;

        player.volumeProperty().bind(layout.volumeSlider.valueProperty());
        return player;
    }

    private void configureMediaDisplay(MediaPlayer player, Track track) {
        boolean isVideo = track.getResourcePath().toLowerCase().endsWith(".mp4");

        layout.mediaView.setMediaPlayer(isVideo ? player : null);
        layout.mediaView.setVisible(isVideo);
        layout.artworkSymbol.setVisible(!isVideo);

        if (isVideo) {
            if (!layout.artworkCard.getStyleClass().contains("video-card")) {
                layout.artworkCard.getStyleClass().add("video-card");
            }
        } else {
            layout.artworkCard.getStyleClass().remove("video-card");
        }
    }

    private void bindElapsedTime(MediaPlayer player) {
        layout.timeLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> {
                            Duration currentTime = player.getCurrentTime();
                            if (currentTime == null
                                    || !Double.isFinite(currentTime.toSeconds())
                                    || currentTime.toSeconds() < 0) {
                                return "00:00";
                            }

                            int totalSeconds = (int) currentTime.toSeconds();
                            return String.format(
                                    "%02d:%02d",
                                    totalSeconds / 60,
                                    totalSeconds % 60
                            );
                        },
                        player.currentTimeProperty()
                )
        );
    }

    private void bindProgress(MediaPlayer player) {
        layout.playbackProgress.bind(
                Bindings.createDoubleBinding(
                        () -> {
                            Duration currentTime = player.getCurrentTime();
                            Duration totalDuration = player.getTotalDuration();

                            if (currentTime == null || totalDuration == null) {
                                return 0.0;
                            }

                            double elapsed = currentTime.toMillis();
                            double total = totalDuration.toMillis();

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

    private void configureReadyStatus(MediaPlayer player, Track track) {
        player.setOnReady(() -> {
            layout.statusLabel.setText("Ready: " + track.getTitle());

            Duration totalDuration = player.getTotalDuration();
            if (totalDuration == null || !Double.isFinite(totalDuration.toSeconds())) {
                return;
            }

            int totalSeconds = (int) totalDuration.toSeconds();
            layout.durationLabel.setText(
                    String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60)
            );
        });
    }

    private void configurePlaybackStatus(MediaPlayer player, Track track) {
        player.setOnPlaying(() -> {
            layout.statusLabel.setText("Playing: " + track.getTitle());
            layout.playButton.setText("⏸");
        });

        player.setOnPaused(() -> {
            layout.statusLabel.setText("Paused: " + track.getTitle());
            layout.playButton.setText("▶");
        });

        player.setOnStopped(() -> {
            layout.statusLabel.setText("Stopped: " + track.getTitle());
            layout.playButton.setText("▶");
        });

        player.setOnEndOfMedia(() -> {
            int index = layout.trackList.getSelectionModel().getSelectedIndex();
            int lastIndex = layout.trackList.getItems().size() - 1;

            if (index >= 0 && index < lastIndex) {
                // Selection change invokes loadTrack() through MainApp's listener.
                layout.trackList.getSelectionModel().select(index + 1);
            } else {
                finishedCurrentTrack = true;
                layout.statusLabel.setText("Finished: " + track.getTitle());
                layout.playButton.setText("▶");
            }
        });

        player.setOnError(() -> {
            // Read the error from the player that actually failed.
            String message = player.getError() == null
                    ? "Unknown playback error"
                    : player.getError().getMessage();

            layout.statusLabel.setText("Playback error: " + message);
            layout.playButton.setText("▶");
        });
    }

    /**
     * Stops and releases the active player and resets its UI bindings.
     * Also called when the application closes.
     */
    public void releaseCurrentPlayer() {
        if (mediaPlayer == null) {
            return;
        }

        // Old player callbacks must not overwrite the new track's status.
        mediaPlayer.setOnReady(null);
        mediaPlayer.setOnPlaying(null);
        mediaPlayer.setOnPaused(null);
        mediaPlayer.setOnStopped(null);
        mediaPlayer.setOnEndOfMedia(null);
        mediaPlayer.setOnError(null);

        mediaPlayer.stop();
        mediaPlayer.volumeProperty().unbind();

        layout.timeLabel.textProperty().unbind();
        layout.timeLabel.setText("00:00");

        layout.playbackProgress.unbind();
        layout.playbackProgress.set(0.0);
        layout.seekSlider.setValue(0.0);
        layout.durationLabel.setText("00:00");
        layout.playButton.setText("▶");

        layout.mediaView.setMediaPlayer(null);
        layout.mediaView.setVisible(false);
        layout.artworkSymbol.setVisible(true);
        layout.artworkCard.getStyleClass().remove("video-card");

        mediaPlayer.dispose();
        mediaPlayer = null;
    }
}