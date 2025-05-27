package com.jereprograma.myttrpg.core.editing;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Gestor de comandos con pila de undo/redo.
 */
public class CommandManager {
    private final Deque<UndoableCommand> undoStack = new LinkedList<>();
    private final Deque<UndoableCommand> redoStack = new LinkedList<>();

    /**
     * Ejecuta y registra un comando para poder deshacerlo.
     */
    public void push(UndoableCommand cmd) {
        cmd.execute();
        undoStack.push(cmd);
        redoStack.clear();
    }

    /**
     * Deshace el último comando si existe.
     */
    public void undo() {
        if (!undoStack.isEmpty()) {
            UndoableCommand cmd = undoStack.pop();
            cmd.undo();
            redoStack.push(cmd);
        }
    }

    /**
     * Rehace el último comando deshecho.
     */
    public void redo() {
        if (!redoStack.isEmpty()) {
            UndoableCommand cmd = redoStack.pop();
            cmd.execute();
            undoStack.push(cmd);
        }
    }

    /**
     * Indica si hay comandos para deshacer.
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    /**
     * Indica si hay comandos para rehacer.
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
}
