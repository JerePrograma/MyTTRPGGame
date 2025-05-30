// core/src/main/java/com/jereprograma/myttrpg/core/StageManager.java
package com.jereprograma.myttrpg.core;

import com.badlogic.gdx.Game;
import com.jereprograma.myttrpg.core.screens.ScreenBase;

/**
 * Administra el cambio de pantallas en la aplicación.
 * En tu GameApp debería usarse un Game (no ApplicationAdapter)
 * para poder llamar setScreen() aquí.
 */
public class StageManager {
    private final Game game;

    public StageManager(Game game) {
        this.game = game;
    }

    /**
     * Cambia a la pantalla indicada.
     */
    public void setScreen(ScreenBase screen) {
        game.setScreen(screen);
    }
}
