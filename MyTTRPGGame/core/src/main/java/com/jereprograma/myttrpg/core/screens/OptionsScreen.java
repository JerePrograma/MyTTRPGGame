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
 * Pantalla de opciones (volumen, controles, etc.).
 * Por ahora incluye sólo un botón \"Back\" para volver al menú principal.
 */
public class OptionsScreen extends ScreenBase {
    public OptionsScreen(StageManager screens,
                         Skin skin,
                         List<EcsSystem> ecsSystems,
                         GameInputSystem inputSystem,
                         ConsoleSystem console) {
        super(skin);

        Table table = new Table(skin);
        table.setFillParent(true);
        table.align(Align.center);

        // Botón para volver al menú principal
        TextButton backBtn = new TextButton("Back", skin);
        backBtn.addListener(evt -> {
            screens.setScreen(new MainMenuScreen(
                    screens,
                    skin,
                    ecsSystems,
                    inputSystem,
                    console
            ));
            return true;
        });

        table.add(backBtn).pad(10);
        stage.addActor(table);
    }
}
