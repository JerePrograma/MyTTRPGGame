package com.jereprograma.myttrpg.core.commands;

import com.jereprograma.myttrpg.core.commands.game.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parsea líneas de comando y devuelve el objeto Command correspondiente.
 */
public class GameCommandParser {
    private static final Pattern ROLL_PATTERN = Pattern.compile("^/roll\\s+(\\w+)$");
    private static final Pattern SPAWN_PATTERN = Pattern.compile("^/spawn\\s+(\\d+)\\s+(\\d+)$");
    private static final Pattern MOVE_PATTERN = Pattern.compile("^/move\\s+(north|south|east|west)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern DESCRIBE_PATTERN = Pattern.compile("^/describe(?:\\s+(\\w+))?$");
    private static final Pattern SAVE_PATTERN = Pattern.compile("^/save\\s+(\\w+)$");
    private static final Pattern LOAD_PATTERN = Pattern.compile("^/load\\s+(\\w+)$");
    private static final Pattern SAVE_DEFAULT_PATTERN = Pattern.compile("^/save$");

    public static GameCommand parse(String line) {
        Matcher m;
        if ((m = ROLL_PATTERN.matcher(line)).matches()) {
            return new RollGameCommand(m.group(1));
        }
        if ((m = SPAWN_PATTERN.matcher(line)).matches()) {
            int x = Integer.parseInt(m.group(1));
            int y = Integer.parseInt(m.group(2));
            return new SpawnGameCommand(x, y);
        }
        if ((m = MOVE_PATTERN.matcher(line)).matches()) {
            String dirToken = m.group(1).toLowerCase();
            MoveGameCommand.Dir dir = switch (dirToken) {
                case "north" -> MoveGameCommand.Dir.UP;
                case "south" -> MoveGameCommand.Dir.DOWN;
                case "east" -> MoveGameCommand.Dir.RIGHT;
                case "west" -> MoveGameCommand.Dir.LEFT;
                default -> throw new IllegalArgumentException("Dirección inválida: " + dirToken);
            };
            return new MoveGameCommand(dir);
        }
        if ((m = DESCRIBE_PATTERN.matcher(line)).matches()) {
            return new DescribeGameCommand(m.group(1));
        }
        if ((m = SAVE_PATTERN.matcher(line)).matches()) {
            return new SaveGameCommand(m.group(1));
        }
        if (SAVE_DEFAULT_PATTERN.matcher(line).matches()) {
            return new SaveGameCommand("default");
        }
        if ((m = LOAD_PATTERN.matcher(line)).matches()) {
            return new LoadGameCommand(m.group(1));
        }
        return new UnknownGameCommand(line);
    }
}
