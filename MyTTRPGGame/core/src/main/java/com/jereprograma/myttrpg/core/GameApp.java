package com.jereprograma.myttrpg.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.jereprograma.myttrpg.core.commands.GameCommand;
import com.jereprograma.myttrpg.core.commands.GameCommandParser;
import com.jereprograma.myttrpg.core.commands.game.*;
import com.jereprograma.myttrpg.core.ecs.Entity;
import com.jereprograma.myttrpg.core.ecs.EntityPool;
import com.jereprograma.myttrpg.core.ecs.components.PositionComponent;
import com.jereprograma.myttrpg.core.ecs.components.RenderComponent;
import com.jereprograma.myttrpg.core.ecs.system.*;
import com.jereprograma.myttrpg.core.editing.CommandManager;
import com.jereprograma.myttrpg.core.events.EventBus;
import com.jereprograma.myttrpg.core.map.MapGenerator;
import com.jereprograma.myttrpg.core.screens.MainMenuScreen;
import com.jereprograma.myttrpg.core.services.MapPersistenceService;
import com.jereprograma.myttrpg.core.services.RollService;
import com.jereprograma.myttrpg.core.events.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase principal que orquesta los sistemas ECS, input y rendering.
 */
public class GameApp extends Game {
    public static final int CELL_SIZE = 32;

    private SpriteBatch batch;
    private Skin skin;
    private Stage uiStage;
    private AssetManager assets;
    private Label messageLabel;
    private ConsoleSystem console;
    private OrthographicCamera camera;

    private final List<Entity> entities = new ArrayList<>();

    private GridSystem gridSystem;
    private RenderSystem renderSystem;

    private final List<EcsSystem> gameSystems = new ArrayList<>();

    @Override
    public void create() {
        // 1) Batch & Cámara
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        // 2) AssetManager & TextureAtlas
        assets = new AssetManager();
        assets.load("tiles/tiles.atlas", TextureAtlas.class);
        assets.finishLoading();
        TextureAtlas tilesAtlas = assets.get("tiles/tiles.atlas", TextureAtlas.class);

        // 3) UI Skin & Stage
        FileHandle uiAtlasFile = Gdx.files.internal("ui/uiskin.atlas");
        TextureAtlas uiAtlas = new TextureAtlas(uiAtlasFile);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"), uiAtlas);
        uiStage = new Stage();

        // 4) Sistemas ECS básicos
        gridSystem = new GridSystem(camera, 25, 18);
        renderSystem = new RenderSystem(batch, camera, tilesAtlas);

        // 5) Modo Edición
        CommandManager cmdMgr = new CommandManager();
        EditingSystem editingSystem = new EditingSystem(cmdMgr, entities);

        // 6) Input: procesadores y multiplexer
        GameInputProcessor gameProc = new GameInputProcessor();
        EditInputProcessor editProc = new EditInputProcessor(camera);
        GameInputSystem inputSystem = new GameInputSystem(gameProc, editProc, uiStage);

        // 7) Consola (tras configurar el multiplexer)
        console = new ConsoleSystem(skin, camera);

        // 8) MessageLabel para mensajes flotantes
        messageLabel = new Label("", skin);
        messageLabel.setPosition(10, camera.viewportHeight - 30);
        messageLabel.getColor().a = 0f;
        uiStage.addActor(messageLabel);

        // 9) Mapa inicial
        List<String> regionNames = List.of("grass", "dirt", "water", "sand");
        entities.addAll(MapGenerator.randomMap(25, 18, regionNames));

        // 10) Registro en bucle de sistemas
        gameSystems.clear();
        gameSystems.add(gridSystem);
        gameSystems.add(editingSystem);
        gameSystems.add(renderSystem);
        gameSystems.add(console);   // para que la consola se actualice y dibuje

        // 11) Comandos de juego
        registerGameCommands();

        // 12) Jugador inicial
        Entity player = new Entity();
        player.addComponent(new PositionComponent(1, 1));
        player.addComponent(new RenderComponent("player"));
        entities.add(player);
        renderSystem.setEntities(entities);

        // 13) Suscripción de resultados a UI
        EventBus.register(RollResultEvent.class, e -> displayMessage("Resultado: " + e.value()));
        EventBus.register(SpawnResultEvent.class, e -> displayMessage("Spawn en: " + e.x() + ", " + e.y()));
        EventBus.register(MoveResultEvent.class, e -> displayMessage("Movimiento a: " + e.x() + ", " + e.y()));
        EventBus.register(DescribeResultEvent.class, e -> displayMessage(e.info()));
        EventBus.register(SaveResultEvent.class, e -> displayMessage("Mapa guardado: " + e.name()));
        EventBus.register(LoadResultEvent.class, e -> displayMessage("Mapa cargado: " + e.name()));
        EventBus.register(UnknownCommandEvent.class, e -> displayMessage("Comando desconocido: " + e.input()));

        // 14) Capturar ENTER y ESC para toggle de consola
        Gdx.input.setCatchKey(Input.Keys.ENTER, true);
        Gdx.input.setCatchKey(Input.Keys.ESCAPE, true);

        // 15) Navegación de pantallas: arrancamos en MainMenuScreen
        StageManager screens = new StageManager(this);
        screens.setScreen(new MainMenuScreen(
                screens,
                skin,
                gameSystems,
                inputSystem,
                console
        ));
    }

    private void registerGameCommands() {
        EventBus.register(RollGameCommand.class, cmd -> {
            int val = RollService.lanzar(cmd.notation());
            EventBus.fire(new RollResultEvent(val));
        });

        EventBus.register(SpawnGameCommand.class, cmd -> {
            Entity e = EntityPool.borrow();
            e.addComponent(new PositionComponent(cmd.x(), cmd.y()));
            e.addComponent(new RenderComponent("player"));
            entities.add(e);
            renderSystem.setEntities(entities);
            EventBus.fire(new SpawnResultEvent(cmd.x(), cmd.y()));
        });

        EventBus.register(MoveGameCommand.class, cmd -> {
            Entity pl = entities.get(entities.size() - 1);
            var pos = pl.getComponent(PositionComponent.class);
            int x = pos.x(), y = pos.y();
            switch (cmd.dir()) {
                case UP -> y++;
                case DOWN -> y--;
                case LEFT -> x--;
                case RIGHT -> x++;
            }
            pl.removeComponent(PositionComponent.class);
            pl.addComponent(new PositionComponent(x, y));
            EventBus.fire(new MoveResultEvent(x, y));
        });

        EventBus.register(DescribeGameCommand.class, cmd -> {
            Entity pl = entities.get(entities.size() - 1);
            var pos = pl.getComponent(PositionComponent.class);
            EventBus.fire(new DescribeResultEvent("Jugador en x=" + pos.x() + ", y=" + pos.y()));
        });

        EventBus.register(SaveGameCommand.class, cmd -> {
            try {
                MapPersistenceService.save(cmd.name(), entities);
                EventBus.fire(new SaveResultEvent(cmd.name()));
            } catch (Exception ex) {
                Gdx.app.error("Save", "Error al guardar", ex);
                EventBus.fire(new UnknownCommandEvent(cmd.name()));
            }
        });

        EventBus.register(LoadGameCommand.class, cmd -> {
            String filename = cmd.name() + ".json";
            File mapFile = new File(filename);
            if (!mapFile.exists()) {
                displayMessage("No existe el mapa: " + cmd.name());
                return;
            }
            try {
                for (Entity old : entities) EntityPool.release(old);
                entities.clear();
                var loaded = MapPersistenceService.load(cmd.name());
                entities.addAll(loaded);
                renderSystem.setEntities(entities);
                EventBus.fire(new LoadResultEvent(cmd.name()));
            } catch (Exception ex) {
                Gdx.app.error("Load", "Error al cargar", ex);
                EventBus.fire(new UnknownCommandEvent(cmd.name()));
            }
        });
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        // 1) Actualiza la cámara
        camera.update();

        // 2) Limpia la pantalla
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 3) Si la consola está abierta, primero actualízala y procesa comandos
        if (console.isVisible()) {
            console.update(delta);
            String line;
            while ((line = console.fetchCommand()) != null) {
                GameCommand cmd = GameCommandParser.parse(line);
                EventBus.fire(cmd);
                if (cmd instanceof UnknownGameCommand u) {
                    EventBus.fire(new UnknownCommandEvent(u.text()));
                }
            }
        }

        // 4) Toggle de consola con ENTER / ESC (después de procesar comandos)
        boolean enterJust = Gdx.input.isKeyJustPressed(Input.Keys.ENTER);
        boolean escJust = Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE);
        if (!console.isVisible() && enterJust) {
            console.toggle();
        } else if (console.isVisible() && (enterJust || escJust)) {
            console.toggle();
        }

        // 5) Ejecuta todos los sistemas ECS (grid, lógica, render, etc.)
        for (EcsSystem sys : gameSystems) {
            sys.update(delta);
        }

        // 6) Actualiza y dibuja la UI principal (mensajes flotantes)
        uiStage.act(delta);
        uiStage.draw();
    }

    @Override
    public void dispose() {
        batch.dispose();
        skin.dispose();
        uiStage.dispose();
        assets.dispose();
        gridSystem.dispose();
        renderSystem.dispose();
    }

    private void displayMessage(String text) {
        // detiene animaciones anteriores
        messageLabel.clearActions();
        // actualiza texto
        messageLabel.setText(text);
        // fade in → mostrar 2s → fade out
        messageLabel.addAction(Actions.sequence(Actions.fadeIn(0.2f), Actions.delay(2f), Actions.fadeOut(0.5f)));
    }

}
