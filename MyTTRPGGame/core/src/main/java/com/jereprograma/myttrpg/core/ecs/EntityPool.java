package com.jereprograma.myttrpg.core.ecs;

import com.jereprograma.myttrpg.core.ecs.components.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Pool de entidades para reutilización y reducción de GC.
 */
public class EntityPool {
    private static final Queue<Entity> pool = new ConcurrentLinkedQueue<>();

    /**
     * Obtiene una entidad del pool o crea una nueva si está vacío.
     */
    public static Entity borrow() {
        Entity e = pool.poll();
        if (e == null) {
            return new Entity();
        }
        return e;
    }

    /**
     * Devuelve una entidad al pool, limpiando sus componentes.
     */
    public static void release(Entity entity) {
        // Remover todos los componentes antes de devolver
        for (Component c : entity.getAllComponents()) {
            entity.removeComponent(c.getClass());
        }
        pool.offer(entity);
    }

    /**
     * Tamaño actual del pool (para pruebas/debug).
     */
    public static int size() {
        return pool.size();
    }
}
