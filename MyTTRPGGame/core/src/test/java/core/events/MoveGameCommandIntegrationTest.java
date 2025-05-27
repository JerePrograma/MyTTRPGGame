// src/test/java/com/jereprograma/myttrpg/core/events/MoveCommandIntegrationTest.java
package core.events;

import com.jereprograma.myttrpg.core.commands.game.MoveGameCommand;
import com.jereprograma.myttrpg.core.commands.game.MoveGameCommand.Dir;
import com.jereprograma.myttrpg.core.ecs.Entity;
import com.jereprograma.myttrpg.core.ecs.EntityPool;
import com.jereprograma.myttrpg.core.ecs.components.PositionComponent;
import com.jereprograma.myttrpg.core.events.EventBus;
import com.jereprograma.myttrpg.core.events.MoveResultEvent;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MoveGameCommandIntegrationTest {

    @BeforeEach
    void clearEventBusListeners() throws Exception {
        Field listenersField = EventBus.class.getDeclaredField("listeners");
        listenersField.setAccessible(true);
        Map<?, ?> map = (Map<?, ?>) listenersField.get(null);
        map.clear();
    }

    @Test
    void moveCommandFiresMoveResultEvent() {
        // 1) Prepara una entidad en la pool con posición conocida
        Entity e = EntityPool.borrow();
        e.addComponent(new PositionComponent(2, 3));
        // al despachar, nuestro GameApp toma siempre la última entidad de `entities`,
        // así que simulamos ese escenario:
        List<Entity> fakeWorld = new ArrayList<>();
        fakeWorld.add(e);

        // 2) Registra el handler idéntico al de GameApp.create()
        EventBus.register(MoveGameCommand.class, cmd -> {
            Entity pl = fakeWorld.get(fakeWorld.size() - 1);
            var pos = pl.getComponent(PositionComponent.class);
            int x = pos.x(), y = pos.y();
            switch (cmd.dir()) {
                case UP -> y++;
                case DOWN -> y--;
                case LEFT -> x--;
                case RIGHT -> x++;
            }
            pl.removeComponent(PositionComponent.class);
            pl.addComponent(new PositionComponent(x, y));
            EventBus.fire(new MoveResultEvent(x, y));
        });

        // 3) Capturamos el resultado
        final int[] received = new int[2];
        EventBus.register(MoveResultEvent.class, ev -> {
            received[0] = ev.x();
            received[1] = ev.y();
        });

        // 4) Disparamos el comando
        EventBus.fire(new MoveGameCommand(Dir.UP));

        // 5) Aserciones: original (2,3) → (2,4)
        assertEquals(2, received[0]);
        assertEquals(4, received[1]);
    }
}
