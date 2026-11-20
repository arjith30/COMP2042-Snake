package com.comp2042;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public final class Leaderboard {
    private static final class LocalHolder {
        private static final Leaderboard INSTANCE = new Leaderboard(
                Path.of(System.getProperty("snake.data.dir", "data"), "leaderboard.txt"));
    }
    private final Path file;
    private final ArrayList<Integer> scores = new ArrayList<>();
    private boolean persistent = true;

    public Leaderboard(Path file) {
        this.file = file.toAbsolutePath();
        if (Files.exists(this.file)) {
            try (Stream<String> lines = Files.lines(this.file, StandardCharsets.UTF_8)) {
                lines.limit(1000).forEach(line -> {
                    try {
                        int score = Integer.parseInt(line.trim());
                        if (score >= 0) scores.add(score);
                    } catch (NumberFormatException ignored) { }
                });
            } catch (IOException | UncheckedIOException | SecurityException exception) { persistent = false; }
        }
        trim();
    }

    public static Leaderboard local() { return LocalHolder.INSTANCE; }
    public List<Integer> scores() { return List.copyOf(scores); }
    public boolean isPersistent() { return persistent; }

    public void record(int score) {
        if (score < 0) throw new IllegalArgumentException("Score must not be negative");
        scores.add(score); trim();
        Path temporary = null;
        try {
            Files.createDirectories(file.getParent());
            temporary = Files.createTempFile(file.getParent(), "leaderboard-", ".tmp");
            Files.write(temporary, scores.stream().map(String::valueOf).toList(), StandardCharsets.UTF_8);
            try {
                Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
            }
            persistent = true;
        } catch (IOException | SecurityException exception) { persistent = false; }
        finally {
            if (temporary != null) {
                try { Files.deleteIfExists(temporary); }
                catch (IOException | SecurityException ignored) { }
            }
        }
    }

    private void trim() {
        scores.sort(Comparator.reverseOrder());
        if (scores.size() > 5) scores.subList(5, scores.size()).clear();
    }
}
