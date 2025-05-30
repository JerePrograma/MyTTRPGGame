package com.jereprograma.myttrpg.core.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.jereprograma.myttrpg.core.StageManager;
import com.jereprograma.myttrpg.core.events.EventBus;
import com.jereprograma.myttrpg.core.commands.game.SaveGameCommand;

import java.util.List;

import com.jereprograma.myttrpg.core.ecs.system.EcsSystem;
import com.jereprograma.myttrpg.core.ecs.system.GameInputSystem;
import com.jereprograma.myttrpg.core.ecs.system.ConsoleSystem;

/**
 * Pantalla de pausa con opciones: Resume, Save, Exit to Menu.
 */
public class PauseScreen extends ScreenBase {
    public PauseScreen(StageManager screens,
                       ScreenBase gameScreen,
                       Skin skin,
                       List<EcsSystem> ecsSystems,
                       GameInputSystem inputSystem,
                       ConsoleSystem console) {
        super(skin);

        Table table = new Table(skin);
        table.setFillParent(true);
        table.align(Align.center);

        TextButton resume = new TextButton("Resume", skin);
        TextButton save = new TextButton("Save", skin);
        TextButton exit = new TextButton("Exit to Menu", skin);

        resume.addListener(evt -> {
            screens.setScreen(gameScreen);
            return true;
        });

        save.addListener(evt -> {
            EventBus.fire(new SaveGameCommand("pause_save"));
            return true;
        });

        exit.addListener(evt -> {
            // Ahora pasamos todos los parámetros que MainMenuScreen requiere
            screens.setScreen(new MainMenuScreen(
                    screens,
                    skin,
                    ecsSystems,
                    inputSystem,
                    console
            ));
            return true;
        });

        table.add(resume).pad(10).row();
        table.add(save).pad(10).row();
        table.add(exit).pad(10);

        stage.addActor(table);

        // Capturar ESC para volver también
        Gdx.input.setCatchKey(Input.Keys.ESCAPE, true);
        resume.addListener(evt -> {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                screens.setScreen(gameScreen);
            }
            return false;
        });
    }
}
