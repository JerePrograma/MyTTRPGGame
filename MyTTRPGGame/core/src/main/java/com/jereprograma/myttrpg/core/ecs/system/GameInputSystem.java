package com.jereprograma.myttrpg.core.ecs.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Configura el multiplexer de input para alternar entre modos juego y edición.
 */
public class GameInputSystem {
    private final InputMultiplexer multiplexer;
    private final InputProcessor gameProcessor;
    private final InputProcessor editProcessor;
    private final Stage uiStage;

    public GameInputSystem(InputProcessor gameProcessor, InputProcessor editProcessor, Stage uiStage) {
        this.gameProcessor = gameProcessor;
        this.editProcessor = editProcessor;
        this.uiStage = uiStage;
        this.multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStage);
        // Default: solo modo juego activo
        multiplexer.addProcessor(gameProcessor);
        Gdx.input.setInputProcessor(multiplexer);
    }

    /**
     * Permite alternar entre modo juego/edición
     */
    public void setEditMode(boolean editMode) {
        multiplexer.clear();
        multiplexer.addProcessor(uiStage);
        if (editMode) multiplexer.addProcessor(editProcessor);
        else multiplexer.addProcessor(gameProcessor);
        Gdx.input.setInputProcessor(multiplexer);
    }

    /**
     * ¡Getter para exponer el multiplexer!
     */
    public InputMultiplexer getMultiplexer() {
        return multiplexer;
    }
}
