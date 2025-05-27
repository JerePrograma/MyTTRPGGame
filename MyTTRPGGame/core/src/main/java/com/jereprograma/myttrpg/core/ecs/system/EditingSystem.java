// core/src/main/java/com/jereprograma/myttrpg/core/ecs/system/EditingSystem.java
package com.jereprograma.myttrpg.core.ecs.system;

import com.jereprograma.myttrpg.core.commands.editing.PlaceTileEditingCommand;
import com.jereprograma.myttrpg.core.commands.editing.RedoEditingCommand;
import com.jereprograma.myttrpg.core.commands.editing.UndoEditingCommand;
import com.jereprograma.myttrpg.core.editing.CommandManager;
import com.jereprograma.myttrpg.core.editing.PlaceTile;
import com.jereprograma.myttrpg.core.editing.UndoableCommand;
import com.jereprograma.myttrpg.core.ecs.Entity;
import com.jereprograma.myttrpg.core.events.EventBus;

import java.util.List;

/**
 * Sistema de edición que aplica comandos undoable.
 */
public class EditingSystem implements EcsSystem {
    private final CommandManager commandManager;
    private final List<Entity> entities;

    public EditingSystem(CommandManager commandManager, List<Entity> entities) {
        this.commandManager = commandManager;
        this.entities = entities;
        EventBus.register(UndoEditingCommand.class, cmd -> this.commandManager.undo());
        EventBus.register(RedoEditingCommand.class, cmd -> this.commandManager.redo());
        EventBus.register(PlaceTileEditingCommand.class, ev -> {
            UndoableCommand cmd = new PlaceTile(ev, this.entities);
            this.commandManager.push(cmd);
        });
    }

    @Override
    public void update(float delta) {
        // lógica adicional de edición puede ir aquí si es necesario
    }
}
