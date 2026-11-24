package com.comp2042.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import java.awt.Point;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class LevelTest {
    @Test void challengesHaveDistinctGoalsSpeedsAndOrderedProgression() {
        assertEquals(5, Level.MEADOW.goal()); assertEquals(8, Level.HEDGES.goal()); assertEquals(10, Level.RUINS.goal());
        assertTrue(Level.MEADOW.speed() < Level.HEDGES.speed());
        assertTrue(Level.HEDGES.speed() < Level.RUINS.speed());
        assertEquals(Level.HEDGES, Level.MEADOW.next()); assertEquals(Level.RUINS, Level.HEDGES.next());
        assertNull(Level.RUINS.next());
        assertTrue(Level.MEADOW.obstacles(15,15).isEmpty());
        assertEquals(12,Level.HEDGES.obstacles(15,15).size());
        assertEquals(16,Level.RUINS.obstacles(15,15).size());
    }


    @Test void ruinsHasWideHorizontalAndVerticalEscapeLanes() {
        var obstacles=Level.RUINS.obstacles(15,15);
        for(int y=5;y<=9;y++)for(int x=0;x<15;x++)
            assertFalse(obstacles.contains(new Point(x,y)),"Five-row center corridor stays clear");
        for(int x=6;x<=8;x++)for(int y=0;y<15;y++)
            assertFalse(obstacles.contains(new Point(x,y)),"Three-column vertical escape lane stays clear");
        assertTrue(Level.RUINS.speed()<=9,"Final level should allow time to react");
    }

    @ParameterizedTest @EnumSource(Level.class)
    void layoutIsInBoundsUniqueAndKeepsStartingCorridorClear(Level level) {
        for (int[] size : new int[][]{{15,15},{20,18}}) {
            var cells=level.obstacles(size[0],size[1]);
            assertEquals(cells.size(),new HashSet<>(cells).size());
            for (Point p : cells) {
                assertTrue(p.x>=0&&p.x<size[0]&&p.y>=0&&p.y<size[1]);
                assertTrue(Math.abs(p.y-size[1]/2)>1,"Spawn corridor must be clear");
            }
            Snake snake=new Snake(size[0],size[1],false);
            assertFalse(cells.contains(snake.getHead()));
            assertTrue(snake.getBody().stream().noneMatch(cells::contains));
        }
    }

    @ParameterizedTest @EnumSource(Level.class)
    void everyFreeCellIsReachableFromSpawn(Level level) {
        for(int[] size:new int[][]{{15,15},{20,18}}) {
            int width=size[0],height=size[1];Set<Point> blocked=new HashSet<>(level.obstacles(width,height));
            Set<Point> visited=new HashSet<>();ArrayDeque<Point> queue=new ArrayDeque<>();
            Point start=new Snake(width,height,false).getHead();visited.add(start);queue.add(start);
            while(!queue.isEmpty()) {
                Point p=queue.remove();
                for(Direction d:Direction.values()) {
                    Point next=new Point(p.x+d.dx(),p.y+d.dy());
                    if(next.x>=0&&next.x<width&&next.y>=0&&next.y<height&&!blocked.contains(next)&&visited.add(next))queue.add(next);
                }
            }
            assertEquals(width*height-blocked.size(),visited.size(),"No unreachable apple pockets");
        }
    }

    @ParameterizedTest @EnumSource(Level.class)
    void layoutsAreIndependentAndSmallBoardsAreRejected(Level level) {
        var first=level.obstacles(15,15);
        if(!first.isEmpty())first.get(0).setLocation(-1,-1);
        first.clear();
        assertTrue(level.obstacles(15,15).stream().noneMatch(p->p.x<0||p.y<0));
        assertThrows(IllegalArgumentException.class,()->level.obstacles(14,15));
        assertThrows(IllegalArgumentException.class,()->level.obstacles(15,14));
    }
}
