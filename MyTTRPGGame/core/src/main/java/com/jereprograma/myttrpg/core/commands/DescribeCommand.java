// src/main/java/com/jereprograma/myttrpg/core/commands/DescribeCommand.java
package com.jereprograma.myttrpg.core.commands;

/**
 * @param target por ahora ignoramos y siempre describimos al jugador
 */
public record DescribeCommand(String target) implements Command {
}
