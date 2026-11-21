package com.comp2042;

import com.comp2042.model.Level;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Objects;

/** Restorable obstacle layout for the active challenge. */
public final class Obstacle {
    private final int width;
    private final int height;
    private Level level;
    private ArrayList<Point> obstacles;

    public Obstacle(int width, int height) { this(width, height, Level.MEADOW); }
    public Obstacle(int width, int height, Level level) {
        this.width = width; this.height = height; setLevel(level);
    }
    public void setLevel(Level level) { this.level = Objects.requireNonNull(level, "level"); reset(); }
    public ArrayList<Point> getObstacles() {
        ArrayList<Point> copy = new ArrayList<>();
        for (Point point : obstacles) copy.add(new Point(point));
        return copy;
    }
    public void reset() { obstacles = new ArrayList<>(level.obstacles(width, height)); }
}
