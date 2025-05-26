package com.jereprograma.myttrpg.core.commands;

public record MoveCommand(com.jereprograma.myttrpg.core.commands.MoveCommand.Dir dir) implements Command {
    public enum Dir {UP, DOWN, LEFT, RIGHT}
}
