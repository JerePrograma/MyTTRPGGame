// src/test/java/com/jereprograma/myttrpg/core/services/MapPersistenceServiceTest.java
package core.services;

import com.jereprograma.myttrpg.core.ecs.Entity;
import com.jereprograma.myttrpg.core.ecs.components.PositionComponent;
import com.jereprograma.myttrpg.core.ecs.components.RenderComponent;
import com.jereprograma.myttrpg.core.services.MapPersistenceService;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MapPersistenceServiceTest {

    @Test
    void saveAndLoadMap() throws Exception {
        // Crear entidades de prueba
        Entity e1 = new Entity();
        e1.addComponent(new PositionComponent(0, 0));
        e1.addComponent(new RenderComponent("tiles/grass.png"));

        Entity e2 = new Entity();
        e2.addComponent(new PositionComponent(1, 1));
        e2.addComponent(new RenderComponent("tiles/dirt.png"));

        List<Entity> original = List.of(e1, e2);
        String fname = "testmap";

        // Guardar
        MapPersistenceService.save(fname, original);
        File f = new File(fname + ".json");
        assertTrue(f.exists());

        // Cargar
        List<Entity> loaded = MapPersistenceService.load(fname);
        assertEquals(2, loaded.size());

        // Comparar datos esenciales
        PositionComponent p1 = loaded.get(0).getComponent(PositionComponent.class);
        RenderComponent r1 = loaded.get(0).getComponent(RenderComponent.class);
        assertEquals(0, p1.x());
        assertEquals(0, p1.y());
        assertEquals("tiles/grass.png", r1.spritePath());

        PositionComponent p2 = loaded.get(1).getComponent(PositionComponent.class);
        RenderComponent r2 = loaded.get(1).getComponent(RenderComponent.class);
        assertEquals(1, p2.x());
        assertEquals(1, p2.y());
        assertEquals("tiles/dirt.png", r2.spritePath());

        // Limpiar
        Files.delete(f.toPath());
    }
}