// src/main/java/com/jereprograma/myttrpg/core/ecs/EcsSystem.java
package com.jereprograma.myttrpg.core.ecs.system;

/**
 * Contrato para todos los sistemas del ECS.
 */
public interface EcsSystem {
    /**
     * Se llama cada frame para procesar lógica o render.
     *
     * @param delta tiempo en segundos desde el último frame
     */
    void update(float delta);
}
