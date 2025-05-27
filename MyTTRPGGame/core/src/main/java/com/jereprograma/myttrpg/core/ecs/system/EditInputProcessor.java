// core/src/main/java/com/jereprograma/myttrpg/core/ecs/system/EditInputProcessor.java
package com.jereprograma.myttrpg.core.ecs.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.jereprograma.myttrpg.core.GameApp;
import com.jereprograma.myttrpg.core.commands.editing.PlaceTileEditingCommand;
import com.jereprograma.myttrpg.core.commands.editing.UndoEditingCommand;
import com.jereprograma.myttrpg.core.commands.editing.RedoEditingCommand;
import com.jereprograma.myttrpg.core.events.EventBus;

/**
 * Procesador de input para modo edición: click y undo/redo.
 */
public class EditInputProcessor extends InputAdapter {
    private final OrthographicCamera camera;

    public EditInputProcessor(OrthographicCamera camera) {
        this.camera = camera;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 world = camera.unproject(new Vector3(screenX, screenY, 0));
        int cx = (int) (world.x / GameApp.CELL_SIZE);
        int cy = (int) (world.y / GameApp.CELL_SIZE);
        EventBus.fire(new PlaceTileEditingCommand(cx, cy));
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
            if (keycode == Keys.Z) {
                EventBus.fire(new UndoEditingCommand());
                return true;
            } else if (keycode == Keys.Y) {
                EventBus.fire(new RedoEditingCommand());
                return true;
            }
        }
        return false;
    }
}