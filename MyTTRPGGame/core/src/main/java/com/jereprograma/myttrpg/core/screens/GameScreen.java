// core/src/main/java/com/jereprograma/myttrpg/core/screens/GameScreen.java
package com.jereprograma.myttrpg.core.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.jereprograma.myttrpg.core.StageManager;
import com.jereprograma.myttrpg.core.ecs.system.EcsSystem;
import com.jereprograma.myttrpg.core.ecs.system.GameInputSystem;
import com.jereprograma.myttrpg.core.ecs.system.ConsoleSystem;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import java.util.List;

/**
 * Pantalla principal del juego: renderiza el mundo y muestra un HUD básico.
 */
public class GameScreen extends ScreenBase {
    private final StageManager screens;
    private final List<EcsSystem> systems;
    private final GameInputSystem inputSystem;
    private final ConsoleSystem console;

    private final Label fpsLabel;

    public GameScreen(StageManager screens,
                      List<EcsSystem> systems,
                      GameInputSystem inputSystem,
                      ConsoleSystem console,
                      Skin skin) {
        super(skin);
        this.screens = screens;
        this.systems = systems;
        this.inputSystem = inputSystem;
        this.console = console;

        // HUD: FPS y Modo
        fpsLabel = new Label("FPS: 0", skin);
        fpsLabel.setPosition(10, camera.viewportHeight - 20);
        Label modeLabel = new Label("Mode: PLAY", skin);
        modeLabel.setPosition(10, camera.viewportHeight - 40);

        stage.addActor(fpsLabel);
        stage.addActor(modeLabel);

        // Registrar pausa con ESC
        Gdx.input.setCatchKey(Input.Keys.ESCAPE, true);
    }

    @Override
    public void show() {
        super.show();
        // Asegurar input processor del multiplexer
        inputSystem.setEditMode(false);
    }

    @Override
    public void render(float delta) {
        // Limpia pantalla
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Procesa pausa
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            screens.setScreen(new GameScreen(screens, systems, inputSystem, console, skin));
            return;
        }

        // Actualiza HUD
        fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());

        // Ejecuta sistemas ECS
        for (EcsSystem sys : systems) sys.update(delta);

        // Dibuja consola si visible
        console.update(delta);

        // Dibuja escena UI
        stage.act(delta);
        stage.draw();
    }
}


