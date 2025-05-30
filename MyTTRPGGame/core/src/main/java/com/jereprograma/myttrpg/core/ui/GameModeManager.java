// src/main/java/com/jereprograma/myttrpg/core/ui/GameModeManager.java
package com.jereprograma.myttrpg.core.ui;

/**
 * Simple gestor de modo de juego: PLAY vs EDIT.
 * Tú puedes conectar aquí cualquier lógica de cambio de modo,
 * o exponer un método estático/Singleton para acceder globalmente.
 */
public class GameModeManager {
    private boolean editMode = false;

    /**
     * true = EDIT mode; false = PLAY mode
     */
    public boolean isEditMode() {
        return editMode;
    }

    /**
     * Cambia entre PLAY y EDIT
     */
    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }
}
