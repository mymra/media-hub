package org.example.mediahub;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.example.mediahub.model.Track;
import javafx.scene.control.ListView;
import org.example.mediahub.view.PlayerLayout;
import org.example.mediahub.controller.PlayerController;


/**
 * Entry point for the JavaFX media player prototype.
 */
public class MainApp extends Application {


    // Keep the controller so its player can be released when JavaFX stops.
    private PlayerController controller;

    @Override
    public void start(Stage stage) {
        // Create the controls used by this window.
        PlayerLayout layout = new PlayerLayout();

        ListView<Track> trackList = layout.trackList;

        // Add each track as a separate item in the playlist.
        Track sampleTrack = new Track("Sample track", "/media/sample.mp3");

        trackList.getItems().addAll(
                sampleTrack,
                new Track("Sample track 1", "/media/sample1.mp3"),
                new Track("Sample track 2", "/media/sample2.mp3"),
                new Track("Sample video ", "/media/sample_video.mp4"),
                new Track("u.d.o.mp4", "/media/u.d.o.mp4"),
                new Track("Danzig's _mother", "Danzig's _mother.mp4"),
                new Track("Danzig's _mother480", "Danzig's _mother(480p).mp4"),
                new Track("Danzig's _mother720", "Danzig's _mother(720p).mp4")
        );

        controller = new PlayerController(layout);

        // Let the controller load the newly selected track.
        trackList.getSelectionModel().selectedItemProperty().addListener(
                (observable, previousTrack, selectedTrack) ->
                        controller.loadTrack(selectedTrack)
        );

        // The listener loads the initial track when it is selected.
        trackList.getSelectionModel().select(sampleTrack);

        // Display the controls assembled by PlayerLayout.
        Scene scene = new Scene(layout.getRoot(), 785, 575);

        // Load the JavaFX stylesheet from the resources directory.
        var stylesheet = getClass().getResource("/styles/player.css");

        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }


        // Configure and display the main window.
        stage.setTitle("Media Hub");
        // Load the window icon from the application's resources.
        var iconResource = getClass().getResource("/images/media-hub-icon.png");
        if (iconResource != null) {
            stage.getIcons().add(new Image(iconResource.toExternalForm()));
        }
        stage.setScene(scene);

        // Prevent resizing the window below the usable desktop layout size.
        stage.setMinWidth(800);
        stage.setMinHeight(635);

        stage.show();
    }
    @Override
    public void stop() {
        // Release the active player when the application closes.
        if (controller != null) {
            controller.releaseCurrentPlayer();
        }
    }
    public static void main(String[] args) {
        // Start JavaFX; it will call the start() method.
        launch(args);
    }
}