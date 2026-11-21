package com.comp2042;

import com.comp2042.model.Level;
import com.comp2042.model.LevelProgress;
import java.util.Objects;

/** Board configuration and adventure progress; independent of JavaFX. */
public final class Game {
    private final int width;
    private final int height;
    private final LevelProgress progress;

    /**
     * @param width board columns, at least 15
     * @param height board rows, at least 15
     * @param startingLevel challenge chosen in the menu
     */
    public Game(int width, int height, Level startingLevel) {
        Objects.requireNonNull(startingLevel, "startingLevel").obstacles(width, height);
        this.width = width; this.height = height; this.progress = new LevelProgress(startingLevel);
    }
    /** @return whether the final challenge has been completed */
    public boolean gameWon() { return progress.isAdventureComplete(); }
    /** @return whether the current apple objective has been met */
    public boolean isLevelComplete() { return progress.isLevelComplete(); }
    /** @return whether a completed challenge can advance to another level */
    public boolean hasNextLevel() { return isLevelComplete() && getLevel().next() != null; }
    /** @return whether progression advanced; an incomplete objective throws IllegalStateException */
    public boolean advanceLevel() { return progress.advance(); }
    /** Records one apple and awards its points through the progress model. */
    public void incScore() { progress.collectApple(); }
    /** Restarts the originally selected adventure, clearing all accumulated points. */
    public void resetScore() { progress.reset(); }
    /** @return the active obstacle/speed/objective definition */
    public Level getLevel() { return progress.level(); }
    /** @return apples collected since entering the active level */
    public int getLevelApples() { return progress.levelApples(); }
    /** @return the number of board columns */
    public int getWidth() { return width; }
    /** @return the number of board rows */
    public int getHeight() { return height; }
    /** @return the default windowed cell size in pixels, before responsive scaling */
    public double getMultiplier() { return 44; }
    /** @return apples collected across this adventure */
    public int getScore() { return progress.totalApples(); }
    /** @return five points per collected apple across this adventure */
    public int getScorePoints() { return progress.points(); }
    /** @return movement steps per second for the active level */
    public double getSpeed() { return getLevel().speed(); }
    /** @return false because all three challenges have solid boundary walls */
    public boolean isNoBorder() { return false; }
}
