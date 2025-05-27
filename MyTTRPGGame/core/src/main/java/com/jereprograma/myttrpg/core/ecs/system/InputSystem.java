package com.jereprograma.myttrpg.core.ecs.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.jereprograma.myttrpg.core.commands.MoveCommand;
import com.jereprograma.myttrpg.core.events.EventBus;

/**
 * Lee las flechas del teclado y emite MoveCommand por EventBus.
 */
public class InputSystem implements EcsSystem {
    private final ConsoleSystem console;

    public InputSystem(ConsoleSystem console) {
        this.console = console;
    }

    @Override
    public void update(float delta) {
        if (console.isVisible()) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            EventBus.fire(new MoveCommand(MoveCommand.Dir.UP));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            EventBus.fire(new MoveCommand(MoveCommand.Dir.DOWN));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            EventBus.fire(new MoveCommand(MoveCommand.Dir.LEFT));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            EventBus.fire(new MoveCommand(MoveCommand.Dir.RIGHT));
        }
    }
}
