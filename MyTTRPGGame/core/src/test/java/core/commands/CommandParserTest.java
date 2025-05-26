// src/test/java/com/jereprograma/myttrpg/core/commands/CommandParserTest.java
package core.commands;

import com.jereprograma.myttrpg.core.commands.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandParserTest {

    @Test
    void parseRollCommand() {
        Command cmd = CommandParser.parse("/roll d20");
        assertTrue(cmd instanceof RollCommand);
        assertEquals("d20", ((RollCommand) cmd).notation());
    }

    @Test
    void parseSpawnCommand() {
        Command cmd = CommandParser.parse("/spawn 5 10");
        assertTrue(cmd instanceof SpawnCommand);
        assertEquals(5, ((SpawnCommand) cmd).x());
        assertEquals(10, ((SpawnCommand) cmd).y());
    }

    @Test
    void parseMoveCommand() {
        Command cmd = CommandParser.parse("/move north");
        assertTrue(cmd instanceof MoveCommand);
        assertEquals(MoveCommand.Dir.UP, ((MoveCommand) cmd).dir());
    }

    @Test
    void parseDescribeCommandWithTarget() {
        Command cmd = CommandParser.parse("/describe enemy");
        assertTrue(cmd instanceof DescribeCommand);
        assertEquals("enemy", ((DescribeCommand) cmd).target());
    }

    @Test
    void parseDescribeCommandWithoutTarget() {
        Command cmd = CommandParser.parse("/describe");
        assertTrue(cmd instanceof DescribeCommand);
        assertNull(((DescribeCommand) cmd).target());
    }

    @Test
    void parseSaveCommandWithName() {
        Command cmd = CommandParser.parse("/save testMap");
        assertTrue(cmd instanceof SaveCommand);
        assertEquals("testMap", ((SaveCommand) cmd).name());
    }

    @Test
    void parseSaveCommandDefault() {
        Command cmd = CommandParser.parse("/save");
        assertTrue(cmd instanceof SaveCommand);
        assertEquals("default", ((SaveCommand) cmd).name());
    }

    @Test
    void parseLoadCommand() {
        Command cmd = CommandParser.parse("/load mymap");
        assertTrue(cmd instanceof LoadCommand);
        assertEquals("mymap", ((LoadCommand) cmd).name());
    }

    @Test
    void parseUnknownCommand() {
        Command cmd = CommandParser.parse("/foo bar");
        assertTrue(cmd instanceof UnknownCommand);
        assertEquals("/foo bar", ((UnknownCommand) cmd).text());
    }
}
