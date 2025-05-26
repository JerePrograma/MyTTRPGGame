// src/test/java/com/jereprograma/myttrpg/core/services/RollServiceTest.java
package core.services;

import com.jereprograma.myttrpg.core.services.RollService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RollServiceTest {

    @Test
    void rollValidDice() {
        int result = RollService.lanzar("d6");
        assertTrue(result >= 1 && result <= 6);
    }

    @Test
    void rollAnotherDice() {
        int result = RollService.lanzar("d20");
        assertTrue(result >= 1 && result <= 20);
    }

    @Test
    void invalidNotationReturnsZero() {
        assertEquals(0, RollService.lanzar("x5"));
        assertEquals(0, RollService.lanzar("d"));
    }
}