package com.comp2042.model;

import java.util.Objects;

/** Tracks an adventure separately from movement, rendering and persistence. */
public final class LevelProgress {
    public static final int POINTS_PER_APPLE = 5;
    private final Level startingLevel;
    private Level level;
    private int levelApples;
    private int totalApples;

    /** @param startingLevel the first level selected in the menu */
    public LevelProgress(Level startingLevel) {
        this.startingLevel = Objects.requireNonNull(startingLevel, "startingLevel"); reset();
    }

    /**
     * Records one successful food collection. Completed levels must be advanced or restarted.
     * @throws IllegalStateException if the current level is already complete
     */
    public void collectApple() {
        if (isLevelComplete()) throw new IllegalStateException("Level is already complete");
        levelApples++; totalApples++;
    }

    /**
     * Begins the next level while preserving the adventure's accumulated score.
     * @return true if the next level started, false at the final completed level
     * @throws IllegalStateException if the current objective is not complete
     */
    public boolean advance() {
        if (!isLevelComplete()) throw new IllegalStateException("Complete the current objective first");
        if (level.next() == null) return false;
        level = level.next(); levelApples = 0; return true;
    }

    /** Resets all progress to the originally selected starting level. */
    public void reset() { level = startingLevel; levelApples = 0; totalApples = 0; }
    /** @return the active level */
    public Level level() { return level; }
    /** @return apples collected in the current level */
    public int levelApples() { return levelApples; }
    /** @return total apples collected since starting this adventure */
    public int totalApples() { return totalApples; }
    /** @return cumulative adventure points */
    public int points() { return totalApples * POINTS_PER_APPLE; }
    /** @return whether the current apple objective is complete */
    public boolean isLevelComplete() { return levelApples >= level.goal(); }
    /** @return whether the final level has been completed */
    public boolean isAdventureComplete() { return isLevelComplete() && level.next() == null; }
}
