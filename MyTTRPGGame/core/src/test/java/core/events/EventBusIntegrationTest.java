// src/test/java/com/jereprograma/myttrpg/core/events/EventBusIntegrationTest.java
package core.events;

import com.jereprograma.myttrpg.core.commands.CommandParser;
import com.jereprograma.myttrpg.core.commands.RollCommand;
import com.jereprograma.myttrpg.core.commands.SpawnCommand;
import com.jereprograma.myttrpg.core.commands.UnknownCommand;
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
        var rollCmd = CommandParser.parse("/roll d6");
        assertInstanceOf(RollCommand.class, rollCmd);
        assertEquals("d6", ((RollCommand) rollCmd).notation());

        // /spawn 3 5
        var spawnCmd = CommandParser.parse("/spawn 3 5");
        assertInstanceOf(SpawnCommand.class, spawnCmd);
        assertEquals(3, ((SpawnCommand) spawnCmd).x());
        assertEquals(5, ((SpawnCommand) spawnCmd).y());

        // unknown
        var unknown = CommandParser.parse("/foo bar");
        assertInstanceOf(UnknownCommand.class, unknown);
        assertEquals("/foo bar", ((UnknownCommand) unknown).text());
    }

    @Test
    void testRollCommandFiresRollResultEvent() {
        // 2.3.3: register production-like handler
        EventBus.register(RollCommand.class, cmd -> {
            int value = RollService.lanzar(cmd.notation());
            EventBus.fire(new RollResultEvent(value));
        });

        AtomicInteger captured = new AtomicInteger(-1);
        EventBus.register(RollResultEvent.class, e -> captured.set(e.value()));

        // dispatch
        EventBus.fire(new RollCommand("d4"));

        int result = captured.get();
        assertTrue(result >= 1 && result <= 4, "roll result should be between 1 and 4");
    }
}
