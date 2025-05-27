package com.jereprograma.myttrpg.core.editing;

public interface UndoableCommand {
    void execute();
    void undo();
}
