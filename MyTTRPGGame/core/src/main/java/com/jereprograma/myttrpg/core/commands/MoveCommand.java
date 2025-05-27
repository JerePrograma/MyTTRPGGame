package com.jereprograma.myttrpg.core.commands;

public record MoveCommand(MoveCommand.Dir dir) implements Command {
    public enum Dir {UP, DOWN, LEFT, RIGHT}
}
