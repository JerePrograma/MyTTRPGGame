// src/main/java/com/jereprograma/myttrpg/core/ecs/system/GridSystem.java
package com.jereprograma.myttrpg.core.ecs.system;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class GridSystem implements EcsSystem {
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;
    private final int worldWidth;    // en celdas
    private final int worldHeight;   // en celdas

    public GridSystem(OrthographicCamera camera, int worldWidth, int worldHeight) {
        this.camera = camera;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void update(float delta) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // líneas verticales
        int cellSize = 32;
        for (int x = 0; x <= worldWidth; x++) {
            float xpos = x * cellSize;
            shapeRenderer.line(xpos, 0, xpos, worldHeight * cellSize);
        }
        // líneas horizontales
        for (int y = 0; y <= worldHeight; y++) {
            float ypos = y * cellSize;
            shapeRenderer.line(0, ypos, worldWidth * cellSize, ypos);
        }

        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
