// src/main/java/com/jereprograma/myttrpg/core/ecs/system/RenderSystem.java
package com.jereprograma.myttrpg.core.ecs.system;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jereprograma.myttrpg.core.GameApp;
import com.jereprograma.myttrpg.core.ecs.Entity;
import com.jereprograma.myttrpg.core.ecs.components.PositionComponent;
import com.jereprograma.myttrpg.core.ecs.components.RenderComponent;

import java.util.Collections;
import java.util.List;

/**
 * Dibuja entidades en pantalla usando el CELL_SIZE de GameApp.
 */
public class RenderSystem implements EcsSystem {
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final AssetManager assets;
    private List<Entity> entities = Collections.emptyList();

    public RenderSystem(SpriteBatch batch,
                        OrthographicCamera camera,
                        AssetManager assets) {
        this.batch = batch;
        this.camera = camera;
        this.assets = assets;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    @Override
    public void update(float delta) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Entity e : entities) {
            var pos = e.getComponent(PositionComponent.class);
            var rc = e.getComponent(RenderComponent.class);
            if (pos != null && rc != null) {
                Texture tex = assets.get(rc.spritePath(), Texture.class);
                batch.draw(tex,
                        pos.x() * GameApp.CELL_SIZE,
                        pos.y() * GameApp.CELL_SIZE);
            }
        }
        batch.end();
    }

    public void dispose() {
        // seguir el patrón; aquí no hay recursos propios
    }
}
