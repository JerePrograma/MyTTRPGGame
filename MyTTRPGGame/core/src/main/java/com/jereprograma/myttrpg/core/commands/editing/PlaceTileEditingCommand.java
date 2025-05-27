// core/src/main/java/com/jereprograma/myttrpg/core/commands/PlaceTileCommand.java
package com.jereprograma.myttrpg.core.commands.editing;

public final class PlaceTileEditingCommand {
    private final int x, y;

    public PlaceTileEditingCommand(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }
}
