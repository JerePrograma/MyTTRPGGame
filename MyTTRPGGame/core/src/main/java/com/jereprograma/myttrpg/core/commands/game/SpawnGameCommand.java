package com.jereprograma.myttrpg.core.commands.game;

import com.jereprograma.myttrpg.core.commands.GameCommand;

public record SpawnGameCommand(int x, int y) implements GameCommand {
}
