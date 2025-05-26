package com.jereprograma.myttrpg.core.map;

import com.jereprograma.myttrpg.core.ecs.Entity;
import com.jereprograma.myttrpg.core.ecs.components.PositionComponent;
import com.jereprograma.myttrpg.core.ecs.components.RenderComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapGenerator {
    private static final Random RNG = new Random();

    /**
     * Genera un mapa de width×height celdas, eligiendo para cada celda
     * un sprite al azar de tileSprites.
     */
    public static List<Entity> randomMap(int width, int height, List<String> tileSprites) {
        List<Entity> tiles = new ArrayList<>(width * height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                String sprite = tileSprites.get(RNG.nextInt(tileSprites.size()));
                Entity tile = new Entity();
                tile.addComponent(new PositionComponent(x, y));
                tile.addComponent(new RenderComponent(sprite));
                tiles.add(tile);
            }
        }
        return tiles;
    }
}
