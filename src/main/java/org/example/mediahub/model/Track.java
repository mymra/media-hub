package org.example.mediahub.model;

/**
 * Describes one audio track in the playlist.
 * The media path points to a file in src/main/resources.
 */
public class Track {

    private final String title;
    private final String resourcePath;

    public Track(String title, String resourcePath) {
        this.title = title;
        this.resourcePath = resourcePath;
    }

    public String getTitle() {
        return title;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    @Override
    public String toString() {
        // ListView uses toString() to display the track title.
        return title;
    }
}