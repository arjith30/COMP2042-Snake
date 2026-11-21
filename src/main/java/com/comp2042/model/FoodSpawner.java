package com.comp2042.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.random.RandomGenerator;

/** Selects an unoccupied cell without an unbounded random retry loop. */
public final class FoodSpawner {
    private FoodSpawner() { }

    /**
     * @param width number of board columns
     * @param height number of board rows
     * @param snake current snake positions
     * @param obstacles blocked cells
     * @param random random source, injectable for reproducible tests
     * @return a uniformly selected free cell, or empty when no free cell remains
     * @throws IllegalArgumentException if a board dimension is not positive
     */
    public static Optional<Point> choose(int width, int height, Snake snake,
                                         Collection<Point> obstacles, RandomGenerator random) {
        if (width < 1 || height < 1) throw new IllegalArgumentException("Board dimensions must be positive");
        Objects.requireNonNull(snake, "snake");
        Objects.requireNonNull(obstacles, "obstacles");
        Objects.requireNonNull(random, "random");
        HashSet<Point> occupied = new HashSet<>(snake.getBody());
        occupied.add(snake.getHead()); occupied.addAll(obstacles);
        ArrayList<Point> free = new ArrayList<>();
        for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) {
            Point cell = new Point(x, y);
            if (!occupied.contains(cell)) free.add(cell);
        }
        return free.isEmpty() ? Optional.empty() : Optional.of(free.get(random.nextInt(free.size())));
    }
}
