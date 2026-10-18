# Media Hub

Media Hub is a JavaFX desktop application for playing and organizing audio.
It starts as an OBJ2000 multimedia project and is intended to grow into a
full-featured media library with its own REST API and PostgreSQL database.

> Project status: early prototype. Currently, the application can play three
> local audio file. Features listed in the roadmap are planned, not implemented.

## Current functionality

- Play three bundled MP3 files using JavaFX `MediaPlayer`.
- Select a track from a playlist to start playback.
- Control playback with Play, Pause, Stop, Previous, and Next.
- Adjust volume while a track is playing. The selected volume is preserved when switching tracks.
- Stop and dispose of the previous player when switching tracks.
- Display the selected track and playback status in the application window.
- Display an error message when a media resource is missing or cannot be loaded.
- Release the media player when the application closes.
- Display playback progress and elapsed/total time.

## Requirements

- JDK 24
- Maven

## Run the current prototype

1. Open the project in IntelliJ IDEA.
2. Reload Maven dependencies if prompted.
3. Run the Maven goal `javafx:run`.
4. Select a track from the playlist, or use the playback controls.

The bundled test files is located at
`The bundled audio files are located in `src/main/resources/media/`.

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
- [ ] Keep the interface responsive and usable when resized.

### Milestone 2 — Advanced local player

- [ ] Automatically play the next track.
- [ ] Add repeat-one, repeat-all, and shuffle modes.
- [ ] Allow seeking and skipping forward/backward.
- [ ] Add search, sorting, and filtering.
- [ ] Add favorites and a playback queue.
- [ ] Add keyboard shortcuts and improve accessibility.
- [ ] Improve the layout and provide a consistent visual theme.

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