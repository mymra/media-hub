package org.example.mediahub;

import javafx.application.Application;
import javafx.scene.Scene;
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
                new Track("Sample track 2", "/media/sample2.mp3")
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
        Scene scene = new Scene(layout.getRoot(), 800, 500);

        // Configure and display the main window.
        stage.setTitle("Media Hub");
        stage.setScene(scene);
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