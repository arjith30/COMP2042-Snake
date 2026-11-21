package com.comp2042.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/** Fixed gameplay challenges: increasing apple targets, speed and obstacle complexity. */
public enum Level {
    MEADOW("1 · Open Meadow", "Collect 5 apples. Learn the route on an open field.", 5, 7),
    HEDGES("2 · Hedge Gates", "Collect 8 apples. Navigate two hedges through wide gates.", 8, 8.5),
    RUINS("3 · Garden Ruins", "Collect 12 apples. Master narrow passages at higher speed.", 12, 10.5);

    private final String title;
    private final String description;
    private final int goal;
    private final double speed;

    Level(String title, String description, int goal, double speed) {
        this.title = title; this.description = description; this.goal = goal; this.speed = speed;
    }

    /** @return player-facing name and level number */
    @Override public String toString() { return title; }
    /** @return the level objective and challenge description */
    public String description() { return description; }
    /** @return apples required to clear this level */
    public int goal() { return goal; }
    /** @return snake movement steps per second */
    public double speed() { return speed; }
    /** @return the following challenge, or null for the final level */
    public Level next() { return ordinal() + 1 < values().length ? values()[ordinal() + 1] : null; }

    /**
     * Builds a fresh layout with a clear spawn corridor and connected free space.
     * @param width board columns, at least 15
     * @param height board rows, at least 15
     * @return independent obstacle positions
     * @throws IllegalArgumentException when the board is too small for these layouts
     */
    public List<Point> obstacles(int width, int height) {
        if (width < 15 || height < 15) throw new IllegalArgumentException("Levels require at least a 15 by 15 board");
        int cx = width / 2, cy = height / 2;
        ArrayList<Point> cells = new ArrayList<>();
        if (this == HEDGES) {
            for (int y : new int[]{cy - 3, cy + 3})
                for (int offset = -4; offset <= 4; offset++)
                    if (Math.abs(offset) >= 2) cells.add(new Point(cx + offset, y));
        } else if (this == RUINS) {
            for (int x : new int[]{cx - 4, cx + 4})
                for (int offset = -5; offset <= 5; offset++)
                    if (Math.abs(offset) >= 2) cells.add(new Point(x, cy + offset));
            for (int y : new int[]{cy - 4, cy + 4})
                for (int offset = -2; offset <= 2; offset++)
                    if (offset != 0) cells.add(new Point(cx + offset, y));
        }
        return cells;
    }
}
