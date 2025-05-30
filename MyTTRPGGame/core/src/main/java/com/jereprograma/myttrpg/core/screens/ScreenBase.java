// core/src/main/java/com/jereprograma/myttrpg/core/screens/ScreenBase.java
package com.jereprograma.myttrpg.core.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Clase base para todas las pantallas del juego.
 * Mantiene un Stage con su propia cámara y viewport,
 * y define el ciclo de vida por defecto.
 */
public abstract class ScreenBase implements Screen {
    protected final Stage stage;
    protected final OrthographicCamera camera;
    protected final Skin skin;

    public ScreenBase(Skin skin) {
        this.skin = skin;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
        stage = new Stage(new ScreenViewport(camera));
    }

    /**
     * Se llama cuando esta pantalla se hace activa
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Renderiza la pantalla: limpia la pantalla y dibuja el stage
     */
    @Override
    public void render(float delta) {
        // Limpia fondo
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Actualiza cámara y viewport
        camera.update();
        stage.getViewport().apply();

        // Lógica de act y draw de los actores
        stage.act(delta);
        stage.draw();
    }

    /**
     * Ajusta el viewport si cambia el tamaño de la ventana
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        camera.setToOrtho(false, width, height);
    }

    /**
     * Pausa (no usado por defecto)
     */
    @Override
    public void pause() {
    }

    /**
     * Reanuda (no usado por defecto)
     */
    @Override
    public void resume() {
    }

    /**
     * Se llama cuando esta pantalla deja de estar activa
     */
    @Override
    public void hide() {
    }

    /**
     * Limpia recursos del stage
     */
    @Override
    public void dispose() {
        stage.dispose();
    }
}
