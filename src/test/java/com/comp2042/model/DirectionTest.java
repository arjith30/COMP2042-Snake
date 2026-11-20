package com.comp2042.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {
    @ParameterizedTest @EnumSource(Direction.class)
    void onlyTheReverseDirectionIsOpposite(Direction direction) {
        for (Direction other : Direction.values())
            assertEquals(direction.dx() + other.dx() == 0 && direction.dy() + other.dy() == 0,
                    direction.isOppositeTo(other));
        assertFalse(direction.isOppositeTo(null));
    }
}
