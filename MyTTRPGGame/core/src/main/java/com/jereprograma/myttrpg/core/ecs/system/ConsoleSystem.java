package com.jereprograma.myttrpg.core.ecs.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Sistema para mostrar y capturar comandos en consola in-game usando Scene2D,
 * con historial navegable con flechas arriba/abajo.
 */
public class ConsoleSystem implements EcsSystem {
    private final Stage stage;
    private final TextField input;
    private final Queue<String> commandQueue = new ArrayDeque<>();

    private final List<String> history = new ArrayList<>();
    private int historyCursor = -1;

    public ConsoleSystem(Skin skin, OrthographicCamera camera) {
        this.stage = new Stage(new ScreenViewport(camera));

        Table table = new Table(skin);
        table.setFillParent(true);
        table.bottom().left().pad(10);

        this.input = new TextField("", skin);
        input.setMessageText(">> escribe comando");

        // Listener para manejo de teclas
        input.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    String cmd = input.getText().trim();
                    if (!cmd.isEmpty()) {
                        commandQueue.add(cmd);
                        history.add(cmd);
                        historyCursor = history.size(); // apunta tras el último
                        input.setText("");
                    }
                    return true;
                }
                if (keycode == Input.Keys.UP) {
                    if (!history.isEmpty() && historyCursor > 0) {
                        historyCursor--;
                        input.setText(history.get(historyCursor));
                        input.setCursorPosition(input.getText().length());
                    }
                    return true;
                }
                if (keycode == Input.Keys.DOWN) {
                    if (!history.isEmpty() && historyCursor < history.size() - 1) {
                        historyCursor++;
                        input.setText(history.get(historyCursor));
                        input.setCursorPosition(input.getText().length());
                    } else {
                        historyCursor = history.size();
                        input.setText("");
                    }
                    return true;
                }
                return false;
            }
        });

        table.add(input).width(400f);
        stage.addActor(table);

        // Establecer focus y multiplexor
        InputMultiplexer mux = new InputMultiplexer();
        mux.addProcessor(stage);
        InputProcessor prev = Gdx.input.getInputProcessor();
        if (prev != null) mux.addProcessor(prev);
        Gdx.input.setInputProcessor(mux);
        stage.setKeyboardFocus(input);
    }

    @Override
    public void update(float delta) {
        stage.act(delta);
        stage.draw();
    }

    /**
     * Si hay comandos en cola devuelve el primero, si no retorna null.
     */
    public String fetchCommand() {
        return commandQueue.poll();
    }

    /**
     * Liberar recursos de la UI.
     */
    public void dispose() {
        stage.dispose();
    }
}
