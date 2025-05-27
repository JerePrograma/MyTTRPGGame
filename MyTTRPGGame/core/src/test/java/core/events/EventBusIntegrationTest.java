// src/test/java/com/jereprograma/myttrpg/core/events/EventBusIntegrationTest.java
package core.events;

import com.jereprograma.myttrpg.core.commands.GameCommandParser;
import com.jereprograma.myttrpg.core.commands.game.RollGameCommand;
import com.jereprograma.myttrpg.core.commands.game.SpawnGameCommand;
import com.jereprograma.myttrpg.core.commands.game.UnknownGameCommand;
import com.jereprograma.myttrpg.core.events.EventBus;
import com.jereprograma.myttrpg.core.events.RollResultEvent;
import com.jereprograma.myttrpg.core.services.RollService;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class EventBusIntegrationTest {

    @BeforeEach
    void clearEventBusListeners() throws Exception {
        Field listenersField = EventBus.class.getDeclaredField("listeners");
        listenersField.setAccessible(true);
        Map<?, ?> map = (Map<?, ?>) listenersField.get(null);
        map.clear();
    }

    @Test
    void testCommandParserRollAndSpawnAndUnknown() {
        // 2.3.1 & 2.3.2: parser tests
        // /roll d6
        var rollCmd = GameCommandParser.parse("/roll d6");
        assertInstanceOf(RollGameCommand.class, rollCmd);
        assertEquals("d6", ((RollGameCommand) rollCmd).notation());

        // /spawn 3 5
        var spawnCmd = GameCommandParser.parse("/spawn 3 5");
        assertInstanceOf(SpawnGameCommand.class, spawnCmd);
        assertEquals(3, ((SpawnGameCommand) spawnCmd).x());
        assertEquals(5, ((SpawnGameCommand) spawnCmd).y());

        // unknown
        var unknown = GameCommandParser.parse("/foo bar");
        assertInstanceOf(UnknownGameCommand.class, unknown);
        assertEquals("/foo bar", ((UnknownGameCommand) unknown).text());
    }

    @Test
    void testRollCommandFiresRollResultEvent() {
        // 2.3.3: register production-like handler
        EventBus.register(RollGameCommand.class, cmd -> {
            int value = RollService.lanzar(cmd.notation());
            EventBus.fire(new RollResultEvent(value));
        });

        AtomicInteger captured = new AtomicInteger(-1);
        EventBus.register(RollResultEvent.class, e -> captured.set(e.value()));

        // dispatch
        EventBus.fire(new RollGameCommand("d4"));

        int result = captured.get();
        assertTrue(result >= 1 && result <= 4, "roll result should be between 1 and 4");
    }
}
