// core/src/main/java/com/jereprograma/myttrpg/core/ecs/system/GameInputProcessor.java
package com.jereprograma.myttrpg.core.ecs.system;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.jereprograma.myttrpg.core.commands.game.MoveGameCommand;
import com.jereprograma.myttrpg.core.events.EventBus;

/**
 * Procesador de input para modo juego: flechas para mover.
 */
public class GameInputProcessor extends InputAdapter {
    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.UP -> EventBus.fire(new MoveGameCommand(MoveGameCommand.Dir.UP));
            case Keys.DOWN -> EventBus.fire(new MoveGameCommand(MoveGameCommand.Dir.DOWN));
            case Keys.LEFT -> EventBus.fire(new MoveGameCommand(MoveGameCommand.Dir.LEFT));
            case Keys.RIGHT -> EventBus.fire(new MoveGameCommand(MoveGameCommand.Dir.RIGHT));
            default -> {
                return false;
            }
        }
        return true;
    }
}
