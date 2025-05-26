package com.jereprograma.myttrpg.core.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parsea líneas de comando y devuelve el objeto Command correspondiente.
 */
public class CommandParser {
    private static final Pattern ROLL_PATTERN = Pattern.compile("^/roll\\s+(\\w+)$");
    private static final Pattern SPAWN_PATTERN = Pattern.compile("^/spawn\\s+(\\d+)\\s+(\\d+)$");
    private static final Pattern MOVE_PATTERN = Pattern.compile("^/move\\s+(north|south|east|west)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern DESCRIBE_PATTERN = Pattern.compile("^/describe(?:\\s+(\\w+))?$");
    private static final Pattern SAVE_PATTERN = Pattern.compile("^/save\\s+(\\w+)$");
    private static final Pattern LOAD_PATTERN = Pattern.compile("^/load\\s+(\\w+)$");
    private static final Pattern SAVE_DEFAULT_PATTERN = Pattern.compile("^/save$");

    public static Command parse(String line) {
        Matcher m;
        if ((m = ROLL_PATTERN.matcher(line)).matches()) {
            return new RollCommand(m.group(1));
        }
        if ((m = SPAWN_PATTERN.matcher(line)).matches()) {
            int x = Integer.parseInt(m.group(1));
            int y = Integer.parseInt(m.group(2));
            return new SpawnCommand(x, y);
        }
        if ((m = MOVE_PATTERN.matcher(line)).matches()) {
            String dirToken = m.group(1).toLowerCase();
            MoveCommand.Dir dir = switch (dirToken) {
                case "north" -> MoveCommand.Dir.UP;
                case "south" -> MoveCommand.Dir.DOWN;
                case "east" -> MoveCommand.Dir.RIGHT;
                case "west" -> MoveCommand.Dir.LEFT;
                default -> throw new IllegalArgumentException("Dirección inválida: " + dirToken);
            };
            return new MoveCommand(dir);
        }
        if ((m = DESCRIBE_PATTERN.matcher(line)).matches()) {
            return new DescribeCommand(m.group(1));
        }
        if ((m = SAVE_PATTERN.matcher(line)).matches()) {
            return new SaveCommand(m.group(1));
        }
        if (SAVE_DEFAULT_PATTERN.matcher(line).matches()) {
            return new SaveCommand("default");
        }
        if ((m = LOAD_PATTERN.matcher(line)).matches()) {
            return new LoadCommand(m.group(1));
        }
        return new UnknownCommand(line);
    }
}
