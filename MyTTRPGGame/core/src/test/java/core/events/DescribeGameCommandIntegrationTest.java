// src/test/java/com/jereprograma/myttrpg/core/events/DescribeCommandIntegrationTest.java
package core.events;

import com.jereprograma.myttrpg.core.commands.game.DescribeGameCommand;
import com.jereprograma.myttrpg.core.ecs.Entity;
import com.jereprograma.myttrpg.core.ecs.EntityPool;
import com.jereprograma.myttrpg.core.ecs.components.PositionComponent;
import com.jereprograma.myttrpg.core.events.DescribeResultEvent;
import com.jereprograma.myttrpg.core.events.EventBus;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DescribeGameCommandIntegrationTest {

    private List<Entity> fakeWorld;

    @BeforeEach
    void setup() throws Exception {
        // Limpiar listeners
        Field field = EventBus.class.getDeclaredField("listeners");
        field.setAccessible(true);
        ((Map<?, ?>) field.get(null)).clear();

        // Entidad en posición conocida
        fakeWorld = new ArrayList<>();
        Entity player = EntityPool.borrow();
        player.addComponent(new PositionComponent(4, 7));
        fakeWorld.add(player);
    }

    @Test
    void testDescribeCommandFiresDescribeResultEvent() {
        // Registrar handler como en GameApp
        EventBus.register(DescribeGameCommand.class, cmd -> {
            Entity pl = fakeWorld.get(fakeWorld.size() - 1);
            var pos = pl.getComponent(PositionComponent.class);
            String info = "Jugador en x=" + pos.x() + ", y=" + pos.y();
            EventBus.fire(new DescribeResultEvent(info));
        });

        // Capturar resultado
        final String[] captured = {null};
        EventBus.register(DescribeResultEvent.class, e -> captured[0] = e.info());

        // Disparar comando
        EventBus.fire(new DescribeGameCommand(null));

        // Verificar
        assertEquals("Jugador en x=4, y=7", captured[0]);
    }
}
