// core/src/main/java/com/jereprograma/myttrpg/core/editing/PlaceTile.java
package com.jereprograma.myttrpg.core.editing;

import com.jereprograma.myttrpg.core.commands.editing.PlaceTileEditingCommand;
import com.jereprograma.myttrpg.core.ecs.Entity;
import com.jereprograma.myttrpg.core.ecs.EntityPool;
import com.jereprograma.myttrpg.core.ecs.components.PositionComponent;
import com.jereprograma.myttrpg.core.ecs.components.RenderComponent;

import java.util.List;

public class PlaceTile implements UndoableCommand {
    private final PlaceTileEditingCommand event;
    private final List<Entity> entities;
    private Entity tile;

    public PlaceTile(PlaceTileEditingCommand event, List<Entity> entities) {
        this.event = event;
        this.entities = entities;
    }

    @Override
    public void execute() {
        tile = EntityPool.borrow();
        tile.addComponent(new PositionComponent(event.x(), event.y()));
        tile.addComponent(new RenderComponent("grass"));  // o el spriteKey activo
        entities.add(tile);
    }

    @Override
    public void undo() {
        entities.remove(tile);
        EntityPool.release(tile);
    }
}
