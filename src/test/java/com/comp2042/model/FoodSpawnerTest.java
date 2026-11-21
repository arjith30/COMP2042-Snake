package com.comp2042.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import java.awt.Point;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class FoodSpawnerTest {
    @ParameterizedTest @EnumSource(Level.class)
    void foodAvoidsSnakeAndLevelObstaclesAndStaysInsideBoard(Level level) {
        Snake snake=new Snake(15,15,false);var obstacles=level.obstacles(15,15);
        Random random=new Random(42);
        for(int i=0;i<100;i++) {
            Point p=FoodSpawner.choose(15,15,snake,obstacles,random).orElseThrow();
            assertTrue(p.x>=0&&p.x<15&&p.y>=0&&p.y<15);
            assertNotEquals(snake.getHead(),p);assertFalse(snake.getBody().contains(p));assertFalse(obstacles.contains(p));
        }
        assertEquals(new Point(7,7),snake.getHead());
    }

    @Test void fullBoardReturnsEmptyInsteadOfRetryingForever() {
        assertTimeoutPreemptively(Duration.ofSeconds(1),()->
                assertTrue(FoodSpawner.choose(4,1,new Snake(4,1,false),List.of(),new Random(1)).isEmpty()));
    }

    @Test void choosesTheOnlyFreeCellEvenWhenNearlyFull() {
        Snake snake=new Snake(4,2,false);
        var obstacles=List.of(new Point(0,0),new Point(1,0),new Point(2,0));
        for(int i=0;i<20;i++)assertEquals(new Point(3,0),FoodSpawner.choose(4,2,snake,obstacles,new Random(i)).orElseThrow());
        assertEquals(3,obstacles.size());
    }

    @Test void injectedRandomMakesSpawnsReproducible() {
        Snake snake=new Snake(15,15,false);
        Random a=new Random(123),b=new Random(123);
        for(int i=0;i<20;i++)assertEquals(FoodSpawner.choose(15,15,snake,List.of(),a),FoodSpawner.choose(15,15,snake,List.of(),b));
    }

    @Test void rejectsInvalidDimensionsAndMissingInputs() {
        Snake snake=new Snake(15,15,false);
        assertThrows(IllegalArgumentException.class,()->FoodSpawner.choose(0,15,snake,List.of(),new Random()));
        assertThrows(IllegalArgumentException.class,()->FoodSpawner.choose(15,-1,snake,List.of(),new Random()));
        assertThrows(NullPointerException.class,()->FoodSpawner.choose(15,15,null,List.of(),new Random()));
        assertThrows(NullPointerException.class,()->FoodSpawner.choose(15,15,snake,null,new Random()));
        assertThrows(NullPointerException.class,()->FoodSpawner.choose(15,15,snake,List.of(),null));
    }
}
