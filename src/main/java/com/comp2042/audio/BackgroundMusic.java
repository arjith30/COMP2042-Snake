package com.comp2042.audio;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;
import java.util.Objects;
import java.util.function.Supplier;

/** Owns one quiet looping track, with mute and safe fallback when audio is unavailable. */
public final class BackgroundMusic implements AutoCloseable {
    /** Small audio boundary so lifecycle rules can be tested without speakers or native media. */
    public interface AudioLoop extends AutoCloseable {
        void play();
        void pause();
        @Override void close();
    }

    private final Supplier<AudioLoop> factory;
    private final ReadOnlyBooleanWrapper muted = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyBooleanWrapper available = new ReadOnlyBooleanWrapper(true);
    private AudioLoop loop;
    private boolean requested;
    private boolean playing;
    private boolean closed;

    /** Uses the bundled original PCM music loop; does not initialize media until started. */
    public BackgroundMusic() { factory = this::createMediaLoop; }
    /** @param factory audio backend factory, injectable for isolated unit tests */
    public BackgroundMusic(Supplier<AudioLoop> factory) { this.factory = Objects.requireNonNull(factory); }
    /** Requests playback, without recreating a track that is already playing. */
    public void start() { if (!closed) { requested = true; update(); } }
    /** @return whether the player has muted music */
    public boolean isMuted() { return muted.get(); }
    /** @return whether the audio backend remains usable */
    public boolean isAvailable() { return available.get(); }
    /** @return observable mute state for menu and HUD buttons */
    public ReadOnlyBooleanProperty mutedProperty() { return muted.getReadOnlyProperty(); }
    /** @return observable backend status */
    public ReadOnlyBooleanProperty availableProperty() { return available.getReadOnlyProperty(); }
    /** Toggles mute without changing gameplay or creating duplicate players. */
    public void toggleMuted() { setMuted(!isMuted()); }
    /** @param value true to pause music, false to resume it */
    public void setMuted(boolean value) { if (!closed) { muted.set(value); update(); } }

    private void update() {
        if (closed || !requested || !isAvailable()) return;
        try {
            if (isMuted()) {
                if (loop != null && playing) { loop.pause(); playing = false; }
                return;
            }
            if (loop == null) loop = Objects.requireNonNull(factory.get(), "audio backend");
            if (!isAvailable()) { release(); return; }
            if (!playing) { loop.play(); playing = true; }
        } catch (RuntimeException exception) { fail(); }
    }

    private AudioLoop createMediaLoop() {
        URL resource = BackgroundMusic.class.getResource("/resources/audio/meadow-loop.wav");
        if (resource == null) throw new IllegalStateException("Music resource missing");
        Media media = new Media(resource.toExternalForm());
        MediaPlayer player = new MediaPlayer(media);
        player.setVolume(.18); player.setCycleCount(MediaPlayer.INDEFINITE);
        media.setOnError(this::fail); player.setOnError(this::fail);
        return new AudioLoop() {
            public void play() { player.play(); }
            public void pause() { player.pause(); }
            public void close() { player.dispose(); }
        };
    }

    private void fail() { available.set(false); release(); }
    private void release() {
        AudioLoop previous = loop; loop = null; playing = false;
        if (previous != null) {
            try { previous.close(); } catch (RuntimeException ignored) { }
        }
    }
    /** Releases native audio resources. Safe to call repeatedly, including after an error. */
    @Override public void close() {
        if (!closed) { closed = true; requested = false; release(); available.set(false); }
    }
}
