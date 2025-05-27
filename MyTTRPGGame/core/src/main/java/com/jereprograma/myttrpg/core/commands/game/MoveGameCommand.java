package com.jereprograma.myttrpg.core.commands.game;

import com.jereprograma.myttrpg.core.commands.GameCommand;

public record MoveGameCommand(MoveGameCommand.Dir dir) implements GameCommand {
    public enum Dir {UP, DOWN, LEFT, RIGHT}
}
