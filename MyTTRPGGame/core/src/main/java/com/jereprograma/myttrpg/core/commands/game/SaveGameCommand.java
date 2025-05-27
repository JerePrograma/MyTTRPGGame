package com.jereprograma.myttrpg.core.commands.game;

import com.jereprograma.myttrpg.core.commands.GameCommand;

// SaveCommand.java
public record SaveGameCommand(String name) implements GameCommand {
}