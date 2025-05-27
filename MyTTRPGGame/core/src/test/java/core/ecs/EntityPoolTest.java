// core/src/test/java/com/jereprograma/myttrpg/core/ecs/EntityPoolTest.java
package core.ecs;

import com.jereprograma.myttrpg.core.ecs.Entity;
import com.jereprograma.myttrpg.core.ecs.EntityPool;
import com.jereprograma.myttrpg.core.ecs.components.PositionComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EntityPoolTest {

    @BeforeEach
    void clearPool() {
        // Vaciamos el pool para empezar cada test con size = 0
        while (EntityPool.size() > 0) {
            EntityPool.borrow();
        }
    }

    @Test
    void borrowCreatesNewIfEmpty() {
        int initial = EntityPool.size();
        Entity e = EntityPool.borrow();
        assertNotNull(e, "borrow() no debe devolver null");
        assertEquals(initial, EntityPool.size(),
                "borrow() sobre pool vacío no debe cambiar tamaño");
    }

    @Test
    void releaseAddsToPool() {
        Entity e = new Entity();
        e.addComponent(new PositionComponent(2, 3));
        EntityPool.release(e);
        assertEquals(1, EntityPool.size(),
                "release() de una entidad nueva debe incrementar pool a 1");
    }

    @Test
    void reuseEntityClearsComponents() {
        Entity e = EntityPool.borrow();
        e.addComponent(new PositionComponent(5, 5));
        EntityPool.release(e);

        Entity e2 = EntityPool.borrow();
        assertNull(e2.getComponent(PositionComponent.class),
                "Al reusar, sus componentes previos deben haber sido removidos");
    }

    @Test
    void borrowReusesSameEntity() {
        Entity e1 = EntityPool.borrow();
        UUID id1 = e1.getId();

        EntityPool.release(e1);
        assertEquals(1, EntityPool.size(), "Pool debe tener 1 tras release");

        Entity e2 = EntityPool.borrow();
        UUID id2 = e2.getId();
        assertEquals(id1, id2,
                "El UUID debe coincidir: la misma entidad reusada");
        assertEquals(0, EntityPool.size(),
                "Tras volver a borrow, pool debe quedar vacío");
    }
}
