package com.jereprograma.myttrpg.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.jereprograma.myttrpg.core.commands.CommandParser;
import com.jereprograma.myttrpg.core.commands.Command;
import com.jereprograma.myttrpg.core.commands.UnknownCommand;
import com.jereprograma.myttrpg.core.ecs.EntityPool;
import com.jereprograma.myttrpg.core.ecs.Entity;
import com.jereprograma.myttrpg.core.ecs.system.ConsoleSystem;
import com.jereprograma.myttrpg.core.ecs.system.EcsSystem;
import com.jereprograma.myttrpg.core.ecs.system.GridSystem;
import com.jereprograma.myttrpg.core.ecs.system.InputSystem;
import com.jereprograma.myttrpg.core.ecs.system.LogicSystem;
import com.jereprograma.myttrpg.core.ecs.system.RenderSystem;
import com.jereprograma.myttrpg.core.events.EventBus;
import com.jereprograma.myttrpg.core.events.RollResultEvent;
import com.jereprograma.myttrpg.core.events.SpawnResultEvent;
import com.jereprograma.myttrpg.core.events.MoveResultEvent;
import com.jereprograma.myttrpg.core.events.DescribeResultEvent;
import com.jereprograma.myttrpg.core.events.SaveResultEvent;
import com.jereprograma.myttrpg.core.events.LoadResultEvent;
import com.jereprograma.myttrpg.core.events.UnknownCommandEvent;
import com.jereprograma.myttrpg.core.map.MapGenerator;
import com.jereprograma.myttrpg.core.services.MapPersistenceService;
import com.jereprograma.myttrpg.core.services.RollService;
import com.jereprograma.myttrpg.core.commands.RollCommand;
import com.jereprograma.myttrpg.core.commands.SpawnCommand;
import com.jereprograma.myttrpg.core.commands.MoveCommand;
import com.jereprograma.myttrpg.core.commands.DescribeCommand;
import com.jereprograma.myttrpg.core.commands.SaveCommand;
import com.jereprograma.myttrpg.core.commands.LoadCommand;
import com.jereprograma.myttrpg.core.ecs.components.PositionComponent;
import com.jereprograma.myttrpg.core.ecs.components.RenderComponent;

import java.util.ArrayList;
import java.util.List;

public class GameApp extends ApplicationAdapter {
    public static final int CELL_SIZE = 32;
    private SpriteBatch batch;
    private Skin skin;
    private ConsoleSystem console;
    private AssetManager assets;

    // Sistemas de juego (sin incluir consola)
    private GridSystem gridSystem;
    private InputSystem inputSystem;
    private LogicSystem logicSystem;
    private RenderSystem renderSystem;
    private final List<EcsSystem> gameSystems = new ArrayList<>();
    private final List<Entity> entities = new ArrayList<>();

    @Override
    public void create() {
        // 1) Batch & Cámara
        batch = new SpriteBatch();
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false, 400, 300);

        // 2) AssetManager & texturas
        assets = new AssetManager();
        List<String> tilePaths = List.of(
                "tiles/grass.png", "tiles/dirt.png",
                "tiles/water.png", "tiles/sand.png"
        );
        for (String p : tilePaths) assets.load(p, Texture.class);
        assets.load("player.png", Texture.class);
        assets.finishLoading();

        // 3) Skin para UI
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas"));
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"), atlas);

        // 4) Mapa inicial
        entities.addAll(MapGenerator.randomMap(25, 25, tilePaths));

        // 5) Consola
        console = new ConsoleSystem(skin, camera);

        // 6) Sistemas de juego
        gridSystem = new GridSystem(camera, 25, 25);
        inputSystem = new InputSystem(console);
        logicSystem = new LogicSystem();
        renderSystem = new RenderSystem(batch, camera, assets);

        gameSystems.add(gridSystem);
        gameSystems.add(inputSystem);
        gameSystems.add(logicSystem);
        gameSystems.add(renderSystem);

        // 7) Handlers de Commands → ResultEvents
        EventBus.register(RollCommand.class, cmd -> {
            int result = RollService.lanzar(cmd.notation());
            EventBus.fire(new RollResultEvent(result));
        });
        EventBus.register(SpawnCommand.class, cmd -> {
            Entity e = EntityPool.borrow();
            e.addComponent(new PositionComponent(cmd.x(), cmd.y()));
            e.addComponent(new RenderComponent("player.png"));
            entities.add(e);
            renderSystem.setEntities(entities);
            EventBus.fire(new SpawnResultEvent(cmd.x(), cmd.y()));
        });
        EventBus.register(MoveCommand.class, cmd -> {
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
        EventBus.register(DescribeCommand.class, cmd -> {
            Entity pl = entities.get(entities.size() - 1);
            var pos = pl.getComponent(PositionComponent.class);
            String info = "Jugador en x=" + pos.x() + ", y=" + pos.y();
            EventBus.fire(new DescribeResultEvent(info));
        });
        EventBus.register(SaveCommand.class, cmd -> {
            try {
                MapPersistenceService.save(cmd.name(), entities);
                EventBus.fire(new SaveResultEvent(cmd.name()));
            } catch (Exception e) {
                Gdx.app.error("Save", "Error al guardar", e);
                EventBus.fire(new UnknownCommandEvent(cmd.name()));
            }
        });
        EventBus.register(LoadCommand.class, cmd -> {
            try {
                for (Entity old : entities) EntityPool.release(old);
                List<Entity> loaded = MapPersistenceService.load(cmd.name());
                entities.clear();
                entities.addAll(loaded);
                renderSystem.setEntities(entities);
                EventBus.fire(new LoadResultEvent(cmd.name()));
            } catch (Exception e) {
                Gdx.app.error("Load", "Error al cargar", e);
                EventBus.fire(new UnknownCommandEvent(cmd.name()));
            }
        });

        // Player inicial por defecto
        Entity player = new Entity();
        player.addComponent(new PositionComponent(1, 1));
        player.addComponent(new RenderComponent("player.png"));
        entities.add(player);
        renderSystem.setEntities(entities);

        // 8) Subscribers de ResultEvents → consola
        EventBus.register(RollResultEvent.class, e -> console.appendOutput("Resultado: " + e.value()));
        EventBus.register(SpawnResultEvent.class, e -> console.appendOutput("Spawn en: " + e.x() + ", " + e.y()));
        EventBus.register(MoveResultEvent.class, e -> console.appendOutput("Movimiento a: " + e.x() + ", " + e.y()));
        EventBus.register(DescribeResultEvent.class, e -> console.appendOutput(e.info()));
        EventBus.register(SaveResultEvent.class, e -> console.appendOutput("Mapa guardado: " + e.name()));
        EventBus.register(LoadResultEvent.class, e -> console.appendOutput("Mapa cargado: " + e.name()));
        EventBus.register(UnknownCommandEvent.class, e -> console.appendOutput("Comando desconocido: " + e.input()));

        // Pre-capturar ENTER para toggle
        Gdx.input.setCatchKey(Input.Keys.ENTER, true);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        // 1) Dibujo del juego siempre
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        for (EcsSystem sys : gameSystems) sys.update(delta);

        // 2) Toggle de consola
        boolean openKey = Gdx.input.isKeyJustPressed(Input.Keys.ENTER);
        boolean closeKey = Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE);
        if (!console.isVisible() && openKey) console.toggle();
        else if (console.isVisible() && (openKey || closeKey)) console.toggle();

        // 3) Overlay de consola
        if (console.isVisible()) console.update(delta);

        // 4) Procesar comandos
        String line;
        while ((line = console.fetchCommand()) != null) {
            Command cmd = CommandParser.parse(line);
            EventBus.fire(cmd);
            if (cmd instanceof UnknownCommand uc) {
                EventBus.fire(new UnknownCommandEvent(uc.text()));
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        skin.dispose();
        console.dispose();
        assets.dispose();
        gridSystem.dispose();
        renderSystem.dispose();
    }
}
