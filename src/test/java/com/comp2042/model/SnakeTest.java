package com.comp2042.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.CsvSource;
import java.awt.Point;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SnakeTest {
    private Snake snake = new Snake(15, 15, false);

    @Test void startsWithFourCellsFacingLeft() {
        assertEquals(new Point(7, 7), snake.getHead());
        assertEquals(List.of(new Point(10, 7), new Point(9, 7), new Point(8, 7)), snake.getBody());
        assertEquals(Direction.LEFT, snake.getDirection());
        assertFalse(snake.isDead());
    }

    @ParameterizedTest @EnumSource(Direction.class)
    void movesOneCellWithoutChangingLength(Direction direction) {
        if (direction == Direction.RIGHT) snake.advance(Direction.UP, null, List.of());
        Point before = snake.getHead();
        assertFalse(snake.advance(direction, null, List.of()));
        assertEquals(new Point(before.x + direction.dx(), before.y + direction.dy()), snake.getHead());
        assertEquals(3, snake.getBody().size());
        assertEquals(direction, snake.getDirection());
    }

    @Test void foodGrowsExactlyOneSegment() {
        assertTrue(snake.advance(Direction.LEFT, new Point(6, 7), List.of()));
        assertEquals(4, snake.getBody().size());
        assertEquals(new Point(10, 7), snake.getBody().get(0));
        assertFalse(snake.advance(Direction.LEFT, new Point(6, 7), List.of()));
        assertEquals(4, snake.getBody().size());
    }

    @Test void ordinaryStepVacatesTail() {
        snake.advance(Direction.LEFT, null, List.of());
        assertEquals(new Point(9, 7), snake.getBody().get(0));
    }

    @Test void reversingIsIgnoredWithoutChangingState() {
        var body = snake.getBody();
        assertFalse(snake.advance(Direction.RIGHT, new Point(8, 7), List.of()));
        assertEquals(new Point(7, 7), snake.getHead());
        assertEquals(body, snake.getBody());
        assertFalse(snake.isDead());
        assertEquals(Direction.LEFT, snake.getDirection());
    }

    @ParameterizedTest @EnumSource(Direction.class)
    void hittingAnyWallKeepsLastValidPositionAndLength(Direction direction) {
        if (direction == Direction.RIGHT) snake.advance(Direction.UP, null, List.of());
        Point previous = null;
        for (int step = 0; step < 16 && !snake.isDead(); step++) {
            previous = snake.getHead();
            snake.advance(direction, null, List.of());
        }
        assertTrue(snake.isDead());
        assertEquals(previous, snake.getHead());
        assertEquals(3, snake.getBody().size());
    }

    @Test void obstacleCollisionDoesNotEatFoodOrMutateLength() {
        assertFalse(snake.advance(Direction.LEFT, new Point(6, 7), List.of(new Point(6, 7))));
        assertTrue(snake.isDead());
        assertEquals(new Point(7, 7), snake.getHead());
        assertEquals(3, snake.getBody().size());
    }

    @Test void collidesWithNonTailBodyCell() {
        snake.advance(Direction.LEFT, new Point(6, 7), List.of());
        snake.advance(Direction.DOWN, new Point(6, 8), List.of());
        snake.advance(Direction.RIGHT, new Point(7, 8), List.of());
        var before = snake.getBody();
        assertFalse(snake.advance(Direction.UP, null, List.of()));
        assertTrue(snake.isDead());
        assertEquals(new Point(7, 8), snake.getHead());
        assertEquals(before, snake.getBody());
    }

    private void turnTowardTail() {
        snake.advance(Direction.UP, null, List.of());
        snake.advance(Direction.RIGHT, null, List.of());
    }

    @Test void canEnterTailCellWhenTailVacatesIt() {
        turnTowardTail();
        assertEquals(new Point(8, 7), snake.getBody().get(0));
        snake.advance(Direction.DOWN, null, List.of());
        assertFalse(snake.isDead());
        assertEquals(new Point(8, 7), snake.getHead());
        assertEquals(3, snake.getBody().size());
    }

    @Test void cannotEnterTailCellWhenFoodWouldKeepTailThere() {
        turnTowardTail();
        assertFalse(snake.advance(Direction.DOWN, new Point(8, 7), List.of()));
        assertTrue(snake.isDead());
        assertEquals(new Point(8, 6), snake.getHead());
    }

    @ParameterizedTest @EnumSource(Direction.class)
    void wrapsAtEachEdge(Direction direction) {
        snake = new Snake(15, 15, true);
        if (direction == Direction.RIGHT) snake.advance(Direction.UP, null, List.of());
        Point start = snake.getHead();
        int steps = switch (direction) {
            case UP -> start.y + 1;
            case DOWN -> 15 - start.y;
            case LEFT -> start.x + 1;
            case RIGHT -> 15 - start.x;
        };
        for (int i = 0; i < steps; i++) snake.advance(direction, null, List.of());
        assertFalse(snake.isDead());
        assertEquals(new Point(Math.floorMod(start.x + direction.dx() * steps, 15),
                Math.floorMod(start.y + direction.dy() * steps, 15)), snake.getHead());
        assertEquals(3, snake.getBody().size());
    }

    @Test void wrappingStillChecksDestinationForObstacles() {
        snake = new Snake(15, 15, true);
        for (int i = 0; i < 7; i++) snake.advance(Direction.LEFT, null, List.of());
        snake.advance(Direction.LEFT, null, List.of(new Point(14, 7)));
        assertTrue(snake.isDead());
        assertEquals(new Point(0, 7), snake.getHead());
    }

    @Test void positionsAreDeepDefensiveCopies() {
        snake.getHead().setLocation(0, 0);
        var copy = snake.getBody(); copy.get(0).setLocation(0, 0); copy.clear();
        assertEquals(new Point(7, 7), snake.getHead());
        assertEquals(new Point(10, 7), snake.getBody().get(0));
        assertEquals(3, snake.getBody().size());
    }

    @Test void deadSnakeCannotMoveOrGrow() {
        snake.advance(Direction.LEFT, null, List.of(new Point(6, 7)));
        var body = snake.getBody();
        snake.advance(Direction.UP, new Point(7, 6), List.of());
        assertEquals(new Point(7, 7), snake.getHead());
        assertEquals(body, snake.getBody());
    }

    @ParameterizedTest @CsvSource({"3,15", "15,0", "-1,15", "15,-1"})
    void rejectsBoardsThatCannotFitInitialSnake(int width, int height) {
        assertThrows(IllegalArgumentException.class, () -> new Snake(width, height, false));
    }

    @Test void initialSnakeFitsMinimumBoard() {
        Snake small = new Snake(4, 1, false);
        assertEquals(new Point(0, 0), small.getHead());
        assertEquals(new Point(3, 0), small.getBody().get(0));
    }

    @Test void rejectsMissingDirectionOrObstacleCollection() {
        assertThrows(NullPointerException.class, () -> snake.advance(null, null, List.of()));
        assertThrows(NullPointerException.class, () -> snake.advance(Direction.LEFT, null, null));
    }
}
