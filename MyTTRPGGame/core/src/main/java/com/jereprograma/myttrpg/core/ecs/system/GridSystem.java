// src/main/java/com/jereprograma/myttrpg/core/ecs/system/GridSystem.java
package com.jereprograma.myttrpg.core.ecs.system;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.jereprograma.myttrpg.core.GameApp;

/**
 * Dibuja el grid 2D usando ShapeRenderer.
 * Ya no usa valores mágicos, toma CELL_SIZE de GameApp.
 */
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
        for (int x = 0; x <= worldWidth; x++) {
            float xpos = x * GameApp.CELL_SIZE;
            shapeRenderer.line(xpos, 0, xpos, worldHeight * GameApp.CELL_SIZE);
        }
        // líneas horizontales
        for (int y = 0; y <= worldHeight; y++) {
            float ypos = y * GameApp.CELL_SIZE;
            shapeRenderer.line(0, ypos, worldWidth * GameApp.CELL_SIZE, ypos);
        }

        shapeRenderer.end();
    }

    /**
     * Libera recursos del ShapeRenderer.
     */
    public void dispose() {
        shapeRenderer.dispose();
    }
}
