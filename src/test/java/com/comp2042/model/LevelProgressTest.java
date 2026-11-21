package com.comp2042.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.junit.jupiter.api.Assertions.*;

class LevelProgressTest {
    @ParameterizedTest @EnumSource(Level.class)
    void eachLevelCompletesOnlyAtItsOwnGoal(Level level) {
        LevelProgress progress=new LevelProgress(level);
        for(int i=0;i<level.goal()-1;i++){progress.collectApple();assertFalse(progress.isLevelComplete());}
        progress.collectApple();assertTrue(progress.isLevelComplete());
        assertEquals(level.goal(),progress.levelApples());
        assertEquals(level.goal()*5,progress.points());
        assertEquals(level==Level.RUINS,progress.isAdventureComplete());
        assertThrows(IllegalStateException.class,progress::collectApple);
        assertEquals(level.goal(),progress.totalApples());
    }

    @Test void cannotSkipAnIncompleteLevel() {
        LevelProgress progress=new LevelProgress(Level.MEADOW);
        assertThrows(IllegalStateException.class,progress::advance);
        assertEquals(Level.MEADOW,progress.level());assertEquals(0,progress.points());
    }

    @Test void fullAdventurePreservesScoreAcrossFreshLevelObjectives() {
        LevelProgress progress=new LevelProgress(Level.MEADOW);
        for(int i=0;i<5;i++)progress.collectApple();
        assertTrue(progress.advance());assertEquals(Level.HEDGES,progress.level());
        assertEquals(0,progress.levelApples());assertEquals(25,progress.points());
        for(int i=0;i<8;i++)progress.collectApple();
        assertTrue(progress.advance());assertEquals(Level.RUINS,progress.level());assertEquals(65,progress.points());
        for(int i=0;i<12;i++)progress.collectApple();
        assertTrue(progress.isAdventureComplete());assertEquals(25,progress.totalApples());assertEquals(125,progress.points());
        assertFalse(progress.advance());assertEquals(125,progress.points());
    }

    @ParameterizedTest @EnumSource(Level.class)
    void resetReturnsToSelectedStartingLevelAndClearsAllPoints(Level start) {
        LevelProgress progress=new LevelProgress(start);
        for(int i=0;i<start.goal();i++)progress.collectApple();
        if(start.next()!=null)progress.advance();
        progress.reset();
        assertEquals(start,progress.level());assertEquals(0,progress.levelApples());
        assertEquals(0,progress.totalApples());assertEquals(0,progress.points());assertFalse(progress.isLevelComplete());
    }

    @Test void requiresStartingLevel() { assertThrows(NullPointerException.class,()->new LevelProgress(null)); }
}
