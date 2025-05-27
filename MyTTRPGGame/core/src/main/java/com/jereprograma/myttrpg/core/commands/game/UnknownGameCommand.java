package com.jereprograma.myttrpg.core.commands.game;

import com.jereprograma.myttrpg.core.commands.GameCommand;

public record UnknownGameCommand(String text) implements GameCommand {
}
