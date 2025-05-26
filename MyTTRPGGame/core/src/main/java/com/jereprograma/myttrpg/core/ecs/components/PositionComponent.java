// src/main/java/com/jereprograma/myttrpg/core/ecs/components/PositionComponent.java
package com.jereprograma.myttrpg.core.ecs.components;

/**
 * Componente que guarda la posición (x,y) de una entidad en el grid.
 */
public record PositionComponent(int x, int y) implements Component {
}
