# Media Hub

Media Hub is a JavaFX desktop application for playing and organizing audio.
It starts as an OBJ2000 multimedia project and is intended to grow into a
full-featured media library with its own REST API and PostgreSQL database.

> Project status: early prototype. Currently, the application can play one
> local audio file. Features listed in the roadmap are planned, not implemented.

## Current functionality

- Play one bundled MP3 file using JavaFX `MediaPlayer`.
- Select a track from a playlist to start playback.
- Stop and dispose of the previous player when switching tracks.
- Display playback status or an error message in the application window.
- Release the media player when the application closes.

## Requirements

- JDK 24
- Maven

## Run the current prototype

1. Open the project in IntelliJ IDEA.
2. Reload Maven dependencies if prompted.
3. Run the Maven goal `javafx:run`.
4. Click **Play**.

The bundled test file is located at
`src/main/resources/media/sample.mp3`.

## Roadmap

The roadmap is divided into milestones. Each milestone should leave the
application in a working state.

### Milestone 1 — OBJ2000 requirements

- [x] Create a JavaFX project and open the application window.
- [x] Play one bundled audio file.
- [x] Provide a predefined playlist with at least three audio files.
- [ ] Add Play, Pause, Stop, Next, and Previous controls.
- [x] Stop and dispose of the old player when switching tracks.
- [ ] Add volume control.
- [ ] Display playback progress and elapsed/total time.
- [ ] Synchronize player state and UI with JavaFX properties and bindings.
- [ ] Highlight the active track or otherwise indicate playback status.
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