package com.comp2042.audio;

import org.junit.jupiter.api.Test;
import javax.sound.sampled.AudioSystem;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class BackgroundMusicTest {
    private static final class FakeLoop implements BackgroundMusic.AudioLoop {
        int plays, pauses, closes;
        boolean failPlay, failPause, failClose;
        public void play() { plays++; if(failPlay)throw new IllegalStateException("play unavailable"); }
        public void pause() { pauses++; if(failPause)throw new IllegalStateException("pause unavailable"); }
        public void close() { closes++; if(failClose)throw new IllegalStateException("close unavailable"); }
    }

    @Test void startsOnlyOneBackendAndDoesNotRestartAnActiveTrack() {
        FakeLoop loop=new FakeLoop();AtomicInteger created=new AtomicInteger();
        try(BackgroundMusic music=new BackgroundMusic(()->{created.incrementAndGet();return loop;})) {
            music.start();music.start();music.setMuted(false);
            assertEquals(1,created.get());assertEquals(1,loop.plays);assertTrue(music.isAvailable());
        }
        assertEquals(1,loop.closes);
    }

    @Test void mutePausesAndUnmuteResumesSameTrack() {
        FakeLoop loop=new FakeLoop();
        try(BackgroundMusic music=new BackgroundMusic(()->loop)) {
            music.start();music.setMuted(true);music.setMuted(true);
            assertTrue(music.isMuted());assertEquals(1,loop.pauses);
            music.setMuted(false);assertFalse(music.isMuted());assertEquals(2,loop.plays);assertEquals(0,loop.closes);
        }
    }

    @Test void mutedStartupDoesNotInitializeNativeMedia() {
        FakeLoop loop=new FakeLoop();AtomicInteger created=new AtomicInteger();
        try(BackgroundMusic music=new BackgroundMusic(()->{created.incrementAndGet();return loop;})) {
            music.setMuted(true);music.start();assertEquals(0,created.get());
            music.setMuted(false);assertEquals(1,created.get());assertEquals(1,loop.plays);
        }
    }

    @Test void toggleUpdatesObservableStateForBothButtons() {
        try(BackgroundMusic music=new BackgroundMusic(FakeLoop::new)) {
            AtomicInteger changes=new AtomicInteger();
            music.mutedProperty().addListener((o,oldValue,newValue)->changes.incrementAndGet());
            music.toggleMuted();assertTrue(music.mutedProperty().get());
            music.toggleMuted();assertFalse(music.mutedProperty().get());assertEquals(2,changes.get());
        }
    }

    @Test void closeIsIdempotentAndCannotRestartDisposedAudio() {
        FakeLoop loop=new FakeLoop();BackgroundMusic music=new BackgroundMusic(()->loop);
        music.start();music.close();music.close();music.start();music.toggleMuted();
        assertEquals(1,loop.closes);assertEquals(1,loop.plays);assertFalse(music.isAvailable());
    }

    @Test void backendInitializationFailureDoesNotEscapeIntoGame() {
        AtomicInteger attempts=new AtomicInteger();
        try(BackgroundMusic music=new BackgroundMusic(()->{attempts.incrementAndGet();throw new IllegalStateException("no audio device");})) {
            assertDoesNotThrow(music::start);assertFalse(music.availableProperty().get());
            music.start();music.toggleMuted();assertEquals(1,attempts.get());
        }
    }

    @Test void playbackFailureDisposesBackendAndReportsUnavailable() {
        FakeLoop loop=new FakeLoop();loop.failPlay=true;
        try(BackgroundMusic music=new BackgroundMusic(()->loop)) {
            assertDoesNotThrow(music::start);assertFalse(music.isAvailable());assertEquals(1,loop.closes);
        }
        assertEquals(1,loop.closes);
    }

    @Test void pauseFailureAlsoDegradesGracefully() {
        FakeLoop loop=new FakeLoop();loop.failPause=true;
        try(BackgroundMusic music=new BackgroundMusic(()->loop)) {
            music.start();assertDoesNotThrow(()->music.setMuted(true));
            assertFalse(music.isAvailable());assertEquals(1,loop.closes);
        }
    }

    @Test void disposalFailureDoesNotCrashShutdown() {
        FakeLoop loop=new FakeLoop();loop.failClose=true;
        BackgroundMusic music=new BackgroundMusic(()->loop);music.start();
        assertDoesNotThrow(music::close);assertFalse(music.isAvailable());assertEquals(1,loop.closes);
    }

    @Test void rejectsMissingFactoryAndHandlesMissingBackend() {
        assertThrows(NullPointerException.class,()->new BackgroundMusic(null));
        try(BackgroundMusic music=new BackgroundMusic(()->null)) {
            assertDoesNotThrow(music::start);assertFalse(music.isAvailable());
        }
    }

    @Test void bundledMusicIsUsablePcmWaveWithoutNeedingAnAudioDevice() throws Exception {
        var resource=BackgroundMusic.class.getResource("/resources/audio/meadow-loop.wav");assertNotNull(resource);
        try(var audio=AudioSystem.getAudioInputStream(resource)) {
            assertEquals(16,audio.getFormat().getSampleSizeInBits());
            assertEquals(1,audio.getFormat().getChannels());assertEquals(44100,audio.getFormat().getSampleRate());
            double duration=audio.getFrameLength()/audio.getFormat().getFrameRate();
            assertTrue(duration>19&&duration<20);
        }
    }
}
