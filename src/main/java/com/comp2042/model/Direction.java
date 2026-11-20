package com.comp2042.model;

/** The four grid directions, independent of keyboard or rendering APIs. */
public enum Direction {
    UP(0, -1), RIGHT(1, 0), DOWN(0, 1), LEFT(-1, 0);

    private final int dx;
    private final int dy;

    Direction(int dx, int dy) { this.dx = dx; this.dy = dy; }

    /** @return the horizontal displacement of one step */
    public int dx() { return dx; }

    /** @return the vertical displacement of one step */
    public int dy() { return dy; }

    /**
     * Checks whether another direction would reverse movement.
     * @param other direction to compare, or null
     * @return whether it reverses this direction
     */
    public boolean isOppositeTo(Direction other) {
        return other != null && dx == -other.dx && dy == -other.dy;
    }
}
