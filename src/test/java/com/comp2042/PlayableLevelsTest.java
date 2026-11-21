package com.comp2042;

import com.comp2042.model.Level;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import java.awt.Point;
import static org.junit.jupiter.api.Assertions.*;

class PlayableLevelsTest {
    @ParameterizedTest @EnumSource(Level.class)
    void restartRestoresChosenLevelLayoutAndScore(Level start) {
        Game game=new Game(15,15,start);Obstacle obstacle=new Obstacle(15,15,start);
        for(int i=0;i<start.goal();i++)game.incScore();
        assertTrue(game.isLevelComplete());
        if(game.hasNextLevel()){game.advanceLevel();obstacle.setLevel(game.getLevel());}
        game.resetScore();obstacle.setLevel(game.getLevel());
        assertEquals(start,game.getLevel());assertEquals(start.obstacles(15,15),obstacle.getObstacles());
        assertEquals(0,game.getScorePoints());assertFalse(game.gameWon());assertEquals(start.speed(),game.getSpeed());
    }

    @Test void advancingChangesLayoutAndSpeedAndKeepsPoints() {
        Game game=new Game(15,15,Level.MEADOW);Obstacle obstacle=new Obstacle(15,15);
        for(int i=0;i<5;i++)game.incScore();
        assertTrue(game.hasNextLevel());assertFalse(game.gameWon());
        game.advanceLevel();obstacle.setLevel(game.getLevel());
        assertEquals(25,game.getScorePoints());assertEquals(0,game.getLevelApples());
        assertEquals(12,obstacle.getObstacles().size());assertEquals(8.5,game.getSpeed());
    }

    @Test void obstacleCopiesCannotAlterActiveLayoutAndResetRestoresIt() {
        Obstacle obstacle=new Obstacle(15,15,Level.RUINS);
        var copy=obstacle.getObstacles();copy.get(0).setLocation(-1,-1);copy.clear();
        assertEquals(Level.RUINS.obstacles(15,15),obstacle.getObstacles());
        obstacle.reset();assertEquals(24,obstacle.getObstacles().size());
        assertTrue(obstacle.getObstacles().stream().noneMatch(p->p.equals(new Point(-1,-1))));
    }

    @Test void invalidBoardOrStartingLevelIsRejected() {
        assertThrows(IllegalArgumentException.class,()->new Game(14,15,Level.MEADOW));
        assertThrows(NullPointerException.class,()->new Game(15,15,null));
    }
}
