package com.comp2042.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/** Owns snake movement, growth and collisions without JavaFX, skins or food artwork. */
public final class Snake {
    public static final int INITIAL_LENGTH = 4;
    private final int width;
    private final int height;
    private final boolean wrapBorders;
    private Point head;
    private final ArrayList<Point> body = new ArrayList<>();
    private Direction direction = Direction.LEFT;
    private boolean dead;

    /**
     * Creates a left-facing snake with its body ordered from tail to neck.
     * @param width board columns, at least four
     * @param height board rows, at least one
     * @param wrapBorders whether crossing an edge wraps to the opposite edge
     * @throws IllegalArgumentException if the initial snake cannot fit
     */
    public Snake(int width, int height, boolean wrapBorders) {
        if (width < INITIAL_LENGTH || height < 1)
            throw new IllegalArgumentException("Board must be at least four columns by one row");
        this.width = width; this.height = height; this.wrapBorders = wrapBorders;
        head = new Point(Math.min(width / 2, width - INITIAL_LENGTH), height / 2);
        for (int offset = INITIAL_LENGTH - 1; offset >= 1; offset--)
            body.add(new Point(head.x + offset, head.y));
    }

    /**
     * Resolves one movement and any growth atomically. A reverse input is ignored;
     * a collision ends the round without changing the last valid position or length.
     * The tail's cell can be entered only when the tail will vacate it this step.
     * @param requested desired movement direction
     * @param food food position, or null when no food is present
     * @param obstacles occupied obstacle cells
     * @return true only when food was eaten during this successful step
     * @throws NullPointerException if direction or obstacles is null
     */
    public boolean advance(Direction requested, Point food, Collection<Point> obstacles) {
        Objects.requireNonNull(requested, "direction");
        Objects.requireNonNull(obstacles, "obstacles");
        if (dead || direction.isOppositeTo(requested)) return false;
        Point next = new Point(head.x + requested.dx(), head.y + requested.dy());
        direction = requested;
        if (wrapBorders) {
            next.x = Math.floorMod(next.x, width);
            next.y = Math.floorMod(next.y, height);
        }
        boolean grows = next.equals(food);
        boolean hitsBody = false;
        for (int index = grows ? 0 : 1; index < body.size(); index++)
            if (body.get(index).equals(next)) { hitsBody = true; break; }
        if (next.x < 0 || next.x >= width || next.y < 0 || next.y >= height
                || obstacles.contains(next) || hitsBody) {
            dead = true;
            return false;
        }
        body.add(new Point(head));
        if (!grows) body.remove(0);
        head = next;
        return grows;
    }

    /** @return a copy of the head position */
    public Point getHead() { return new Point(head); }

    /** @return a deep copy of the body, from tail to neck */
    public ArrayList<Point> getBody() {
        ArrayList<Point> copy = new ArrayList<>();
        for (Point point : body) copy.add(new Point(point));
        return copy;
    }

    /** @return the most recently accepted movement direction */
    public Direction getDirection() { return direction; }

    /** @return whether a wall, obstacle or body collision has ended the round */
    public boolean isDead() { return dead; }
}
