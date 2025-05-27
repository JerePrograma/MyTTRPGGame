// src/test/java/com/jereprograma/myttrpg/core/events/SaveLoadCommandIntegrationTest.java
package core.events;

import com.jereprograma.myttrpg.core.commands.SaveCommand;
import com.jereprograma.myttrpg.core.commands.LoadCommand;
import com.jereprograma.myttrpg.core.ecs.Entity;
import com.jereprograma.myttrpg.core.ecs.EntityPool;
import com.jereprograma.myttrpg.core.ecs.components.PositionComponent;
import com.jereprograma.myttrpg.core.ecs.components.RenderComponent;
import com.jereprograma.myttrpg.core.events.EventBus;
import com.jereprograma.myttrpg.core.events.LoadResultEvent;
import com.jereprograma.myttrpg.core.events.SaveResultEvent;
import com.jereprograma.myttrpg.core.events.UnknownCommandEvent;
import com.jereprograma.myttrpg.core.services.MapPersistenceService;
import org.junit.jupiter.api.*;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class SaveLoadCommandIntegrationTest {

    private List<Entity> fakeWorld;

    @BeforeEach
    void setup() throws Exception {
        // Limpiar EventBus
        Field field = EventBus.class.getDeclaredField("listeners");
        field.setAccessible(true);
        ((Map<?, ?>) field.get(null)).clear();

        // Preparar fakeWorld con una entidad
        fakeWorld = new ArrayList<>();
        Entity e = EntityPool.borrow();
        e.addComponent(new PositionComponent(2, 2));
        e.addComponent(new RenderComponent("tiles/grass.png"));
        fakeWorld.add(e);
    }

    @Test
    void testSaveCommandFiresSaveResultEvent() {
        AtomicReference<String> savedName = new AtomicReference<>();

        // Handler de SaveCommand
        EventBus.register(SaveCommand.class, cmd -> {
            try {
                MapPersistenceService.save(cmd.name(), fakeWorld);
                EventBus.fire(new SaveResultEvent(cmd.name()));
            } catch (Exception ex) {
                EventBus.fire(new UnknownCommandEvent(cmd.name()));
            }
        });
        // Subscriber de SaveResultEvent
        EventBus.register(SaveResultEvent.class, e -> savedName.set(e.name()));

        String testName = "test_save_" + UUID.randomUUID();
        EventBus.fire(new SaveCommand(testName));

        assertEquals(testName, savedName.get());
        File f = new File(testName + ".json");
        assertTrue(f.exists());

        // Cleanup
        f.delete();
    }

    @Test
    void testLoadCommandFiresLoadResultEvent() throws Exception {
        String testName = "test_load_" + UUID.randomUUID();
        // Guardar para luego cargar
        MapPersistenceService.save(testName, fakeWorld);

        // Clear world to simulate fresh load
        fakeWorld.clear();

        AtomicReference<String> loadedName = new AtomicReference<>();

        // Handler de LoadCommand
        EventBus.register(LoadCommand.class, cmd -> {
            try {
                for (Entity old : fakeWorld) EntityPool.release(old);
                List<Entity> loaded = MapPersistenceService.load(cmd.name());
                fakeWorld.clear();
                fakeWorld.addAll(loaded);
                EventBus.fire(new LoadResultEvent(cmd.name()));
            } catch (Exception ex) {
                EventBus.fire(new UnknownCommandEvent(cmd.name()));
            }
        });
        // Subscriber de LoadResultEvent
        EventBus.register(LoadResultEvent.class, e -> loadedName.set(e.name()));

        EventBus.fire(new LoadCommand(testName));

        assertEquals(testName, loadedName.get());
        File f = new File(testName + ".json");
        assertTrue(f.exists());

        // Cleanup
        f.delete();
    }
}
