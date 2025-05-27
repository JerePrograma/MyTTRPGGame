// src/test/java/com/jereprograma/myttrpg/core/commands/CommandParserTest.java
package core.commands;

import com.jereprograma.myttrpg.core.commands.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandParserTest {

    @Test
    void parseRollCommand() {
        Command cmd = CommandParser.parse("/roll d20");
        assertInstanceOf(RollCommand.class, cmd);
        assertEquals("d20", ((RollCommand) cmd).notation());
    }

    @Test
    void parseSpawnCommand() {
        Command cmd = CommandParser.parse("/spawn 5 10");
        assertInstanceOf(SpawnCommand.class, cmd);
        assertEquals(5, ((SpawnCommand) cmd).x());
        assertEquals(10, ((SpawnCommand) cmd).y());
    }

    @Test
    void parseMoveCommand() {
        Command cmd = CommandParser.parse("/move north");
        assertInstanceOf(MoveCommand.class, cmd);
        assertEquals(MoveCommand.Dir.UP, ((MoveCommand) cmd).dir());
    }

    @Test
    void parseDescribeCommandWithTarget() {
        Command cmd = CommandParser.parse("/describe enemy");
        assertInstanceOf(DescribeCommand.class, cmd);
        assertEquals("enemy", ((DescribeCommand) cmd).target());
    }

    @Test
    void parseDescribeCommandWithoutTarget() {
        Command cmd = CommandParser.parse("/describe");
        assertInstanceOf(DescribeCommand.class, cmd);
        assertNull(((DescribeCommand) cmd).target());
    }

    @Test
    void parseSaveCommandWithName() {
        Command cmd = CommandParser.parse("/save testMap");
        assertInstanceOf(SaveCommand.class, cmd);
        assertEquals("testMap", ((SaveCommand) cmd).name());
    }

    @Test
    void parseSaveCommandDefault() {
        Command cmd = CommandParser.parse("/save");
        assertInstanceOf(SaveCommand.class, cmd);
        assertEquals("default", ((SaveCommand) cmd).name());
    }

    @Test
    void parseLoadCommand() {
        Command cmd = CommandParser.parse("/load mymap");
        assertInstanceOf(LoadCommand.class, cmd);
        assertEquals("mymap", ((LoadCommand) cmd).name());
    }

    @Test
    void parseUnknownCommand() {
        Command cmd = CommandParser.parse("/foo bar");
        assertInstanceOf(UnknownCommand.class, cmd);
        assertEquals("/foo bar", ((UnknownCommand) cmd).text());
    }
}
