// src/main/java/com/jereprograma/myttrpg/core/commands/DescribeCommand.java
package com.jereprograma.myttrpg.core.commands.game;

import com.jereprograma.myttrpg.core.commands.GameCommand;

/**
 * @param target por ahora ignoramos y siempre describimos al jugador
 */
public record DescribeGameCommand(String target) implements GameCommand {
}
