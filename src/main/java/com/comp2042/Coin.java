package com.comp2042;

import com.comp2042.model.FoodSpawner;
import com.comp2042.model.LevelProgress;
import com.comp2042.model.Snake;
import javafx.scene.image.Image;
import java.awt.Point;
import java.util.concurrent.ThreadLocalRandom;

/** Retains the starter food API while using safe apple placement and lazy artwork. */
public class Coin {
    private final Point pos;
    public Coin(int width, int height, Snake snake, Obstacle obstacle) {
        pos = FoodSpawner.choose(width, height, snake, obstacle.getObstacles(), ThreadLocalRandom.current())
                .orElseThrow(() -> new IllegalStateException("No free cell remains for food"));
    }
    public Point getPos() { return new Point(pos); }
    public int getPoints() { return LevelProgress.POINTS_PER_APPLE; }
    public Image getImage() { return AppleArt.image(); }
}
