// core/src/main/java/com/jereprograma/myttrpg/core/screens/MainMenuScreen.java
package com.jereprograma.myttrpg.core.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.jereprograma.myttrpg.core.StageManager;
import com.jereprograma.myttrpg.core.ecs.system.EcsSystem;
import com.jereprograma.myttrpg.core.ecs.system.GameInputSystem;
import com.jereprograma.myttrpg.core.ecs.system.ConsoleSystem;

import java.util.List;

/**
 * Pantalla de menú principal con opciones: Start Game, Options y Quit.
 */
public class MainMenuScreen extends ScreenBase {
    public MainMenuScreen(StageManager screens,
                          Skin skin,
                          List<EcsSystem> ecsSystems,
                          GameInputSystem inputSystem,
                          ConsoleSystem console) {
        super(skin);

        Table table = new Table(skin);
        table.setFillParent(true);
        table.align(Align.center);

        TextButton startBtn = new TextButton("Start Game", skin);
        TextButton optionsBtn = new TextButton("Options", skin);
        TextButton quitBtn = new TextButton("Quit", skin);

        // Iniciar juego: crear y setear GameScreen
        startBtn.addListener(evt -> {
            screens.setScreen(new GameScreen(
                    screens,
                    ecsSystems,
                    inputSystem,
                    console,
                    skin
            ));
            return true;
        });

        // Opciones (pendiente implementar)
        optionsBtn.addListener(evt -> {
            screens.setScreen(new OptionsScreen(
                    screens,
                    skin,
                    ecsSystems,
                    inputSystem,
                    console
            ));
            return true;
        });

        // Salir de la aplicación
        quitBtn.addListener(evt -> {
            Gdx.app.exit();
            return true;
        });

        table.add(startBtn).pad(10).row();
        table.add(optionsBtn).pad(10).row();
        table.add(quitBtn).pad(10);

        stage.addActor(table);
    }
}


