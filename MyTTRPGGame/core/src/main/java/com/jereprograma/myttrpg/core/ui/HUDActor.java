package com.jereprograma.myttrpg.core.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class HUDActor extends Actor {
    private final Skin skin;
    private final BitmapFont font;
    private final OrthographicCamera camera;
    private final GameModeManager modeManager; // o donde guardes Play/Edit
    private final int tileSize;

    // Labels o strings temporales
    private String fpsText = "";
    private String modeText = "";
    private String coordText = "";

    // Layout interno (opcional)
    private final Table table;

    public HUDActor(Skin skin,
                    OrthographicCamera camera,
                    GameModeManager modeManager,
                    int tileSize)
    {
        this.skin = skin;
        this.font = new BitmapFont();            // o skin.getFont("default")
        this.camera = camera;
        this.modeManager = modeManager;
        this.tileSize = tileSize;

        // Configuración inicial de tabla
        table = new Table(skin);
        table.top().left().pad(10);
        // Si usas Labels:
        // table.add(new Label("", skin, "small")).row();
        // table.add(new Label("", skin, "small")).row();
        // table.add(new Label("", skin, "small")).row();

        setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // 1) Actualizar datos cada frame
        fpsText   = "FPS: " + Gdx.graphics.getFramesPerSecond();
        modeText  = "Modo: " + (modeManager.isEditMode() ? "EDIT" : "PLAY");
        coordText = computeTileCoords();

        // 2) Dibujar texto directo con BitmapFont
        batch.begin();
        font.draw(batch, fpsText,    10, getStage().getViewport().getWorldHeight() - 10);
        font.draw(batch, modeText,   10, getStage().getViewport().getWorldHeight() - 30);
        font.draw(batch, coordText,  10, getStage().getViewport().getWorldHeight() - 50);
        batch.end();

        // 3) (Opcional) Dibujar table si la usas
        // table.draw(batch, parentAlpha);
    }

    private String computeTileCoords() {
        Vector3 worldCoords = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(worldCoords);
        int tx = (int)(worldCoords.x / tileSize);
        int ty = (int)(worldCoords.y / tileSize);
        return "Tile: [" + tx + ", " + ty + "]";
    }

}
