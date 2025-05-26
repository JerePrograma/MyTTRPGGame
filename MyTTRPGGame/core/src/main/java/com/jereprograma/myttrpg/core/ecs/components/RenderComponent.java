// src/main/java/com/jereprograma/myttrpg/core/ecs/components/RenderComponent.java
package com.jereprograma.myttrpg.core.ecs.components;

/**
 * Componente que indica el sprite (ruta de textura) a dibujar.
 */
public record RenderComponent(String spritePath) implements Component {
}
