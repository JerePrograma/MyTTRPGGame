package com.jereprograma.myttrpg.core.ecs;

import com.badlogic.gdx.Gdx;
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
            e = new Entity();
            if (Gdx.app != null) {
                Gdx.app.debug("EntityPool", "borrow(): creada nueva entidad " + e.getId());
            }
        } else {
            if (Gdx.app != null) {
                Gdx.app.debug("EntityPool", "borrow(): reusando entidad " + e.getId());
            }
        }
        return e;
    }

    /**
     * Devuelve una entidad al pool, limpiando sus componentes.
     */
    public static void release(Entity entity) {
        // Limpiar componentes antes de devolver
        for (Component c : entity.getAllComponents()) {
            entity.removeComponent(c.getClass());
        }
        pool.offer(entity);
        if (Gdx.app != null) {
            Gdx.app.debug("EntityPool", "release(): entidad devuelta " + entity.getId() +
                    " | pool size = " + pool.size());
        }
    }

    /**
     * Tamaño actual del pool (para pruebas/debug).
     */
    public static int size() {
        // Evitar Gdx.app para no lanzar NPE en tests
        return pool.size();
    }
}
