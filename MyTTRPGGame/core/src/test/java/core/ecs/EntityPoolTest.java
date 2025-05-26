// src/test/java/com/jereprograma/myttrpg/core/ecs/EntityPoolTest.java
package core.ecs;

import com.jereprograma.myttrpg.core.ecs.Entity;
import com.jereprograma.myttrpg.core.ecs.EntityPool;
import com.jereprograma.myttrpg.core.ecs.components.PositionComponent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EntityPoolTest {

    @Test
    void borrowCreatesNewIfEmpty() {
        int initial = EntityPool.size();
        Entity e = EntityPool.borrow();
        assertNotNull(e);
        assertEquals(initial, EntityPool.size());
    }

    @Test
    void releaseAddsToPool() {
        Entity e = new Entity();
        e.addComponent(new PositionComponent(2, 3));
        EntityPool.release(e);
        assertTrue(EntityPool.size() > 0);
    }

    @Test
    void reuseEntityClearsComponents() {
        Entity e = EntityPool.borrow();
        e.addComponent(new PositionComponent(5, 5));
        EntityPool.release(e);
        Entity e2 = EntityPool.borrow();
        assertNull(e2.getComponent(PositionComponent.class));
    }
}
