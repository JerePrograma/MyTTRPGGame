// src/test/java/com/jereprograma/myttrpg/core/commands/CommandParserTest.java
package core.commands;

import com.jereprograma.myttrpg.core.commands.*;
import com.jereprograma.myttrpg.core.commands.game.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameGameCommandParserTest {

    @Test
    void parseRollCommand() {
        GameCommand cmd = GameCommandParser.parse("/roll d20");
        assertInstanceOf(RollGameCommand.class, cmd);
        assertEquals("d20", ((RollGameCommand) cmd).notation());
    }

    @Test
    void parseSpawnCommand() {
        GameCommand cmd = GameCommandParser.parse("/spawn 5 10");
        assertInstanceOf(SpawnGameCommand.class, cmd);
        assertEquals(5, ((SpawnGameCommand) cmd).x());
        assertEquals(10, ((SpawnGameCommand) cmd).y());
    }

    @Test
    void parseMoveCommand() {
        GameCommand cmd = GameCommandParser.parse("/move north");
        assertInstanceOf(MoveGameCommand.class, cmd);
        assertEquals(MoveGameCommand.Dir.UP, ((MoveGameCommand) cmd).dir());
    }

    @Test
    void parseDescribeCommandWithTarget() {
        GameCommand cmd = GameCommandParser.parse("/describe enemy");
        assertInstanceOf(DescribeGameCommand.class, cmd);
        assertEquals("enemy", ((DescribeGameCommand) cmd).target());
    }

    @Test
    void parseDescribeCommandWithoutTarget() {
        GameCommand cmd = GameCommandParser.parse("/describe");
        assertInstanceOf(DescribeGameCommand.class, cmd);
        assertNull(((DescribeGameCommand) cmd).target());
    }

    @Test
    void parseSaveCommandWithName() {
        GameCommand cmd = GameCommandParser.parse("/save testMap");
        assertInstanceOf(SaveGameCommand.class, cmd);
        assertEquals("testMap", ((SaveGameCommand) cmd).name());
    }

    @Test
    void parseSaveCommandDefault() {
        GameCommand cmd = GameCommandParser.parse("/save");
        assertInstanceOf(SaveGameCommand.class, cmd);
        assertEquals("default", ((SaveGameCommand) cmd).name());
    }

    @Test
    void parseLoadCommand() {
        GameCommand cmd = GameCommandParser.parse("/load mymap");
        assertInstanceOf(LoadGameCommand.class, cmd);
        assertEquals("mymap", ((LoadGameCommand) cmd).name());
    }

    @Test
    void parseUnknownCommand() {
        GameCommand cmd = GameCommandParser.parse("/foo bar");
        assertInstanceOf(UnknownGameCommand.class, cmd);
        assertEquals("/foo bar", ((UnknownGameCommand) cmd).text());
    }
}
