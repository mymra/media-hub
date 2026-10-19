# Media Hub

Media Hub is a JavaFX desktop application for playing and organizing audio.
It starts as an OBJ2000 multimedia project and is intended to grow into a
full-featured media library with its own REST API and PostgreSQL database.

> Project status: working JavaFX audio and video player. The application
> includes three bundled MP3 tracks and two MP4 video. REST API and
> PostgreSQL features are planned, not implemented.

## Current functionality

- Play three bundled MP3 tracks and two MP4 video using JavaFX `MediaPlayer`.
- Display video in the player area using JavaFX `MediaView`.
- Select a track from the playlist to start playback.
- Control playback with Play, Pause, Stop, Previous, and Next.
- Automatically play the next item when a track finishes; stop at the end of the playlist.
- Replay the last item by pressing Play after it finishes.
- Adjust volume while playing; preserve the selected volume when switching tracks.
- View elapsed time, total duration, and playback progress.
- Seek to another position using the playback slider.
- Display the selected item, playback status, and errors in the interface.
- Stop and release the previous player when switching items or closing the application.
- Use a dark-themed interface with an artwork placeholder for audio and a video display for MP4.


## Requirements

- JDK 24
- JavaFX SDK 25.0.2 with `javafx.controls` and `javafx.media`
- IntelliJ IDEA

## Run

This project uses JavaFX directly and does not use Maven.

1. Open the project folder in IntelliJ IDEA.
2. Import it as a Java project if IntelliJ does not detect a module automatically.
3. In **File → Project Structure → Modules**, mark:
    - `src/main/java` as **Sources**;
    - `src/main/resources` as **Resources**.
4. In **Project Structure → Libraries**, add the `lib` folder from your JavaFX SDK and attach that library to the project module.
5. Create an **Application** run configuration:
    - **Main class:** `org.example.mediahub.MainApp`
    - **Module:** `media-hub`
    - **VM options** (replace the path with your own JavaFX SDK location):

   ```text
   --module-path "C:\JavaFX\javafx-sdk-25.0.2\lib" --add-modules javafx.controls,javafx.media
   ```
6. Run the Application configuration.

## Roadmap

The roadmap is divided into milestones. Each milestone should leave the
application in a working state.

### Milestone 1 — OBJ2000 requirements

- [x] Create a JavaFX project and open the application window.
- [x] Play one bundled audio file.
- [x] Provide a predefined playlist with at least three audio files.
- [x] Add Play, Pause, Stop, Next, and Previous controls.
- [x] Stop and dispose of the old player when switching tracks.
- [x] Add volume control.
- [x] Display playback progress and elapsed/total time.
- [x] Synchronize player state and UI with JavaFX properties and bindings.
- [x] Highlight the active track or otherwise indicate playback status.
- [ ] Handle missing or unsupported files without crashing.
- [x] Keep the interface responsive and usable when resized.

### Milestone 2 — Advanced local player

- [x] Automatically play the next item until the playlist ends.
- [ ] Add repeat-one, repeat-all, and shuffle modes.
- [x] Allow seeking to a position in the current media item.
- [ ] Add search, sorting, and filtering.
- [ ] Add favorites and a playback queue.
- [ ] Add keyboard shortcuts and improve accessibility.
- [x] Improve the layout and provide a consistent visual theme.

> Milestones 3 and 4 are ideas for development after the OBJ2000 submission.
> They are not part of the submitted JavaFX project.

### Milestone 3 — REST API and PostgreSQL

- [ ] Build a REST API for tracks and media metadata.
- [ ] Store catalog information in PostgreSQL.
- [ ] Load the catalog asynchronously without blocking the JavaFX UI.
- [ ] Provide playable media URLs for supported files.
- [ ] Add server-side search and validation.
- [ ] Handle network and server errors in the desktop application.

### Milestone 4 — Persistent media library

- [ ] Create, edit, and delete playlists.
- [ ] Store favorites and playlists in PostgreSQL.
- [ ] Save listening history and the last playback position.
- [ ] Support importing user-selected audio files.
- [ ] Detect missing files and show clear recovery messages.

### Milestone 5 — Quality and delivery

- [ ] Add automated tests for playlist and server logic.
- [ ] Document the architecture and API.
- [ ] Provide clear instructions for running the client, server, and database.
- [ ] Add screenshots and a short demonstration of the finished application.
- [ ] Prepare a stable release.

## Intended architecture

```text
JavaFX desktop client
    ├── Player and responsive user interface
    ├── Local audio resources and user-selected files
    └── REST API client
              │
              ▼
       Node.js / Express API
              │
              ▼
         PostgreSQL database
```