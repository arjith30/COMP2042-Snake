package com.comp2042;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class LeaderboardTest {
    @TempDir Path directory;
    private Path file() { return directory.resolve("scores.txt"); }

    @Test void missingFileStartsEmptyWithoutWriting() {
        Leaderboard board = new Leaderboard(file());
        assertTrue(board.scores().isEmpty());
        assertFalse(Files.exists(file()));
    }

    @Test void keepsOnlyHighestFiveInDescendingOrderIncludingTies() {
        Leaderboard board = new Leaderboard(file());
        for (int score : new int[]{10, 50, 15, 100, 5, 35, 50, 0}) board.record(score);
        assertEquals(List.of(100, 50, 50, 35, 15), board.scores());
    }

    @Test void scoresPersistWhenStoreIsReopened() {
        Leaderboard board = new Leaderboard(file()); board.record(45); board.record(90);
        assertTrue(board.isPersistent());
        assertEquals(List.of(90, 45), new Leaderboard(file()).scores());
    }

    @Test void createsMissingParentDirectories() {
        Path nested = directory.resolve("nested/data/scores.txt");
        new Leaderboard(nested).record(25);
        assertEquals(List.of(25), new Leaderboard(nested).scores());
    }

    @Test void toleratesMalformedNegativeAndOverflowingRows() throws Exception {
        Files.writeString(file(), "garbage\n-4\n30\n9999999999999999999\n 15 \n\n0\n");
        assertEquals(List.of(30, 15, 0), new Leaderboard(file()).scores());
    }

    @Test void loadedFileIsAlsoLimitedToFiveSortedScores() throws Exception {
        Files.writeString(file(), "5\n35\n10\n100\n15\n50\n50\n");
        assertEquals(List.of(100, 50, 50, 35, 15), new Leaderboard(file()).scores());
    }

    @Test void negativeSubmissionIsRejectedWithoutChangingStoredScores() {
        Leaderboard board = new Leaderboard(file()); board.record(25);
        assertThrows(IllegalArgumentException.class, () -> board.record(-1));
        assertEquals(List.of(25), new Leaderboard(file()).scores());
    }

    @Test void callersCannotMutateScoresAndSnapshotsStayIndependent() {
        Leaderboard board = new Leaderboard(file()); board.record(25);
        List<Integer> snapshot = board.scores();
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(999));
        board.record(50);
        assertEquals(List.of(25), snapshot);
    }

    @Test void saveFailureRetainsScoresForSession() throws Exception {
        Path blocker = directory.resolve("not-a-directory"); Files.writeString(blocker, "occupied");
        Leaderboard board = new Leaderboard(blocker.resolve("scores.txt")); board.record(55);
        assertFalse(board.isPersistent());
        assertEquals(List.of(55), board.scores());
    }

    @Test void recoversWhenStorageBecomesWritableAgain() throws Exception {
        Path blocker = directory.resolve("blocked"); Files.writeString(blocker, "occupied");
        Leaderboard board = new Leaderboard(blocker.resolve("scores.txt")); board.record(55);
        Files.delete(blocker); board.record(35);
        assertTrue(board.isPersistent());
        assertEquals(List.of(55, 35), new Leaderboard(blocker.resolve("scores.txt")).scores());
    }

    @Test void unreadableScoreLocationDoesNotCrashGame() throws Exception {
        Files.createDirectory(file());
        Leaderboard board = new Leaderboard(file());
        assertFalse(board.isPersistent());
        assertTrue(board.scores().isEmpty());
    }

    @Test void recordsZeroAsAValidCompletedRound() {
        Leaderboard board = new Leaderboard(file()); board.record(0);
        assertEquals(List.of(0), board.scores());
    }
}
