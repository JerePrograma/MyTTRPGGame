package com.jereprograma.myttrpg.core.ecs.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.OrthographicCamera;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Consola in-game con historial, salida con scroll, toggle de visibilidad y captura exclusiva de input.
 */
public class ConsoleSystem implements EcsSystem {
    private final Stage stage;
    private final TextField input;
    private final TextArea consoleOutput;
    private final ScrollPane outputScroll;
    private final Queue<String> commandQueue = new ArrayDeque<>();

    private final List<String> history = new ArrayList<>();
    private int historyCursor = -1;

    private boolean visible = false;
    private final com.badlogic.gdx.InputProcessor previousProcessor;

    public ConsoleSystem(Skin skin, OrthographicCamera camera) {
        // Crear Stage propio
        this.stage = new Stage(new ScreenViewport(camera));
        this.previousProcessor = Gdx.input.getInputProcessor();

        Table table = new Table(skin);
        table.setFillParent(true);
        table.bottom().left().pad(10);

        // 1) Area de salida (read-only) dentro de ScrollPane
        consoleOutput = new TextArea("", skin);
        consoleOutput.setDisabled(true);
        consoleOutput.setPrefRows(8);

        outputScroll = new ScrollPane(consoleOutput, skin);
        outputScroll.setFadeScrollBars(false);
        outputScroll.setScrollingDisabled(true, false);

        table.add(outputScroll).width(400f).height(200f).row();

        // 2) Campo de entrada
        input = new TextField("", skin);
        input.setMessageText(">> escribe comando");
        table.add(input).width(400f);

        // 3) Listener de teclas para input
        input.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char key) {
                if (key == '\n' || key == '\r') {
                    String text = textField.getText().trim();
                    if (!text.isEmpty()) {
                        commandQueue.add(text);
                        appendOutput(">> " + text);
                        history.add(text);
                        historyCursor = history.size();
                        textField.setText("");
                    }
                }
            }
        });
        
        input.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.UP) {
                    if (historyCursor > 0) {
                        historyCursor--;
                        input.setText(history.get(historyCursor));
                        input.setCursorPosition(input.getText().length());
                    }
                    return true;
                }
                if (keycode == Input.Keys.DOWN) {
                    if (historyCursor < history.size() - 1) {
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

        stage.addActor(table);
    }

    /**
     * Alterna visibilidad de la consola y gestiona el InputProcessor.
     */
    public void toggle() {
        visible = !visible;
        if (visible) {
            Gdx.input.setInputProcessor(stage);
            stage.setKeyboardFocus(input);
        } else {
            Gdx.input.setInputProcessor(previousProcessor);
        }
    }

    /**
     * Añade una línea de texto a la salida y hace scroll al final.
     */
    public void appendOutput(String line) {
        consoleOutput.appendText(line + "\n");
        outputScroll.layout();
        outputScroll.setScrollPercentY(1f);
    }

    /**
     * Indica si la consola está visible.
     */
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void update(float delta) {
        if (visible) {
            stage.act(delta);
            stage.draw();
        }
    }

    /**
     * Recupera el siguiente comando ingresado (o null si no hay).
     */
    public String fetchCommand() {
        return commandQueue.poll();
    }

    public void dispose() {
        stage.dispose();
    }

    /**
     * Getter para poder registrar el stage en el multiplexer
     */
    public Stage getStage() {
        return stage;
    }

}
