package com.jereprograma.myttrpg.core.ecs.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.jereprograma.myttrpg.core.GameApp;
import com.jereprograma.myttrpg.core.ecs.Entity;
import com.jereprograma.myttrpg.core.ecs.components.PositionComponent;
import com.jereprograma.myttrpg.core.ecs.components.RenderComponent;

import java.util.Collections;
import java.util.List;

public class RenderSystem implements EcsSystem {
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final TextureAtlas atlas;
    private List<Entity> entities = Collections.emptyList();

    public RenderSystem(SpriteBatch batch,
                        OrthographicCamera camera,
                        TextureAtlas tilesAtlas) {
        this.batch = batch;
        this.camera = camera;
        this.atlas = tilesAtlas;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    @Override
    public void update(float delta) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        float camLeft = camera.position.x - camera.viewportWidth / 2f;
        float camRight = camera.position.x + camera.viewportWidth / 2f;
        float camBottom = camera.position.y - camera.viewportHeight / 2f;
        float camTop = camera.position.y + camera.viewportHeight / 2f;

        for (Entity e : entities) {
            var pos = e.getComponent(PositionComponent.class);
            var rc = e.getComponent(RenderComponent.class);
            if (pos == null || rc == null) continue;

            float cellX = pos.x() * GameApp.CELL_SIZE;
            float cellY = pos.y() * GameApp.CELL_SIZE;

            // Culling: dibujar solo si la celda está en el cuadro de la cámara
            if (cellX + GameApp.CELL_SIZE < camLeft ||
                    cellX > camRight ||
                    cellY + GameApp.CELL_SIZE < camBottom ||
                    cellY > camTop) {
                continue;
            }

            TextureRegion region = atlas.findRegion(rc.spritePath());
            if (region != null) {
                batch.draw(region, cellX, cellY);
            } else {
                // Error detallado: región faltante + coordenadas + bounds cámara
                Gdx.app.error("RenderSystem",
                        String.format(
                                "Región no encontrada: '%s' en coords=(%d,%d)  camBounds=[%.1f,%.1f → %.1f,%.1f]",
                                rc.spritePath(),
                                pos.x(), pos.y(),
                                camLeft, camBottom,
                                camRight, camTop
                        )
                );
            }
        }

        batch.end();
    }

    public void dispose() {
        // no posee recursos propios
    }
}
